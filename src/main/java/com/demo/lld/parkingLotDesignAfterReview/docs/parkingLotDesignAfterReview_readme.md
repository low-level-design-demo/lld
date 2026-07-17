# Parking Lot Design (v2 — After Review)

## What It Is

A revised parking lot management system that fixes every structural issue from v1. It applies **Strategy**, **Aggregate Root**, **Registry/Map**, and **Immutable Value Object** patterns to produce a design that is fully working, deterministic, null-safe, and extensible without modifying existing code.

---

## Code Structure

```
parkingLotDesignAfterReview
├── exception/
│   ├── ParkingLotException         (base RuntimeException for the domain)
│   └── NoSlotAvailableException    (extends ParkingLotException)
├── model/
│   ├── VehicleType                 (enum: TWO_WHEELER, FOUR_WHEELER)
│   ├── Vehicle                     (final, immutable; licenseNumber + vehicleType)
│   ├── ParkingSlot                 (single class; id + supportedVehicleType + occupied)
│   ├── ParkingLot                  (aggregate root; owns all slots + assignment strategy)
│   └── Ticket                      (mostly immutable; close() state transition; TicketStatus enum)
├── strategy/
│   ├── ParkingSlotAssignmentStrategy           (interface — slot selection contract)
│   ├── FirstAvailableParkingSlotAssignmentStrategy (fully implemented)
│   ├── CostCalculationStrategy                 (interface — billing contract)
│   ├── FourWheelerCostCalculationStrategy      (RATE = 40.0/hr, rounds up to 1hr min)
│   └── TwoWheelerCostCalculationStrategy       (RATE = 20.0/hr, same rounding logic)
├── service/
│   ├── BillingService    (EnumMap<VehicleType, CostCalculationStrategy> registry)
│   ├── EntryGateService  (single issueTicket() — assign + occupy + return ticket)
│   └── ExitGateService   (single processExit() — close ticket + bill + release slot)
└── ParkingLotDemo        (main() — wires and exercises the full system)
```

---

## Flow

```
EntryGateService
  │
  └── issueTicket(vehicle, gateNumber)
        ├── parkingLot.assignSlot(vehicleType)
        │     ├── strategy.findSlot(slots, vehicleType)  → first free matching slot
        │     └── slot.occupy()  [throws if already occupied]
        └── new Ticket(UUID, vehicle, slotId, gateNumber, LocalDateTime.now())

ExitGateService
  │
  └── processExit(ticket, exitTime)
        ├── ticket.close(exitTime)              [throws if already closed]
        ├── billingService.calculateCost(ticket)
        │     └── strategyMap.get(vehicleType).calculateCost(ticket)
        │           └── Duration.between(entryTime, exitTime) → ceil to hours × rate
        └── parkingLot.releaseSlot(ticket.getParkingSlotId())
              └── slot.release()  [throws if already free]
```

---

## Design Patterns Used

### 1. Strategy Pattern
Two separate strategy interfaces each with a working implementation:

- **`ParkingSlotAssignmentStrategy`** — `FirstAvailableParkingSlotAssignmentStrategy` iterates slots and returns the first `isFree() && supports(vehicleType)` match.
- **`CostCalculationStrategy`** — `FourWheelerCostCalculationStrategy` and `TwoWheelerCostCalculationStrategy` implement deterministic hourly billing using `Duration.between(entryTime, exitTime)`.

New strategies (e.g., `NearestToElevatorStrategy`, `WeekendRateCostStrategy`) can be added without touching any existing class.

### 2. Aggregate Root (`ParkingLot`)
`ParkingLot` is the single gatekeeper for all slot state. External code cannot directly call `slot.occupy()` or `slot.release()` — it must go through `parkingLot.assignSlot()` and `parkingLot.releaseSlot()`. This prevents slots from being put into inconsistent states by callers.

```java
// All slot mutations go through ParkingLot
Ticket ticket = entryGateService.issueTicket(vehicle, gateNumber);
double cost   = exitGateService.processExit(ticket, exitTime);
```

### 3. Registry / Strategy Map (`BillingService`)
Instead of a factory class with `if-else` on `VehicleType`, `BillingService` holds an `EnumMap<VehicleType, CostCalculationStrategy>`. Adding support for a new vehicle type is a configuration change (put a new entry in the map at wiring time) rather than a code change inside `BillingService`.

```java
Map<VehicleType, CostCalculationStrategy> strategies = new EnumMap<>(VehicleType.class);
strategies.put(VehicleType.FOUR_WHEELER, new FourWheelerCostCalculationStrategy());
strategies.put(VehicleType.TWO_WHEELER,  new TwoWheelerCostCalculationStrategy());
BillingService billingService = new BillingService(strategies);
```

### 4. Immutable Value Objects
- **`Vehicle`** is `final` with `final` fields and no setters. Constructed with `Objects.requireNonNull()`. Once created, its state cannot change.
- **`Ticket`** uses `final` fields for all identity data (`id`, `vehicle`, `parkingSlotId`, `entryTime`). Mutable state is limited to `exitTime` and `status`, which transition exactly once via `ticket.close(exitTime)`.

---

## SOLID Principles Applied

| Principle | How |
|---|---|
| **S — Single Responsibility** | `EntryGateService.issueTicket()` handles the full entry flow atomically. `ExitGateService.processExit()` handles the full exit flow atomically. `BillingService` is only responsible for cost lookup. `ParkingLot` is only responsible for slot lifecycle. |
| **O — Open/Closed** | New vehicle types: add an entry to `BillingService`'s map — no existing class changes. New parking strategies: implement `ParkingSlotAssignmentStrategy` — no existing class changes. New billing rates: implement `CostCalculationStrategy` — no existing class changes. |
| **L — Liskov Substitution** | `FourWheelerCostCalculationStrategy` and `TwoWheelerCostCalculationStrategy` are fully interchangeable as `CostCalculationStrategy`. `ParkingSlot` has no subclasses — vehicle-type filtering is handled by `supports(VehicleType)`. |
| **I — Interface Segregation** | `ParkingSlotAssignmentStrategy` exposes only `findSlot()`. `CostCalculationStrategy` exposes only `calculateCost()`. No fat interfaces. |
| **D — Dependency Inversion** | `EntryGateService` depends on `ParkingLot` (a domain object), not a concrete implementation. `BillingService` depends on `CostCalculationStrategy` interface. All strategies are injected at construction. |

---

## Key Improvements Over v1

| Dimension | v1 | v2 |
|---|---|---|
| **Strategy implementations** | Both parking strategies return `null` (stubs) | `FirstAvailableParkingSlotAssignmentStrategy` fully implemented |
| **Parking slot model** | `FourWheelerParkingSlot` / `TwoWheelerParkingSlot` subclasses | Single `ParkingSlot` with `supportedVehicleType` field + `supports()` |
| **Central aggregate** | No `ParkingLot` class; raw slot lists passed everywhere | `ParkingLot` owns all slot state; prevents external mutation |
| **Ticket status** | No status; always "live" | `TicketStatus.ACTIVE` / `CLOSED`; `close()` is a one-way transition |
| **Ticket reference** | Holds `ParkingSlot` object directly | Holds `String parkingSlotId` — decoupled from slot model |
| **Pricing determinism** | `System.currentTimeMillis()` — untestable | `Duration.between(entryTime, exitTime)` — fully deterministic |
| **Manager layer** | 3 manager classes (ParkingSlotManager + 2 subclasses) | Eliminated — `ParkingLot` absorbs this role |
| **Factory classes** | `CostComputationFactory` + `ParkingSlotManagerFactory` (if-else) | Replaced by `BillingService` registry map |
| **Vehicle immutability** | Mutable, no null checks | `final` class, all fields `final`, `Objects.requireNonNull()` |
| **Exception types** | Raw `IllegalArgumentException` | `ParkingLotException` → `NoSlotAvailableException` hierarchy |
| **Ticket ID** | `new Random().nextInt()` (negative-able, non-unique) | `UUID.randomUUID().toString()` |
| **Entry flow** | 3 caller-coordinated steps | Single atomic `issueTicket()` |
| **Exit flow** | 2 caller-coordinated steps | Single atomic `processExit()` |
| **Null safety** | No null checks anywhere | `Objects.requireNonNull()` in every constructor and public method |

---

## State Guards

`ParkingSlot` and `Ticket` both enforce valid state transitions:

```
ParkingSlot:
  free ──► occupy() ──► occupied
  occupied ──► release() ──► free
  occupy() on occupied    → IllegalStateException
  release() on free       → IllegalStateException

Ticket:
  ACTIVE ──► close(exitTime) ──► CLOSED
  close() on CLOSED → IllegalStateException
```

---

## Billing Calculation

Both cost strategies use the same formula with different rates:

```
minutes = Duration.between(entryTime, exitTime).toMinutes()
hours   = Math.max(1L, (minutes + 59) / 60)   // ceil, minimum 1 hour
cost    = hours × RATE_PER_HOUR

FourWheelerCostCalculationStrategy: RATE_PER_HOUR = 40.0
TwoWheelerCostCalculationStrategy:  RATE_PER_HOUR = 20.0
```

The exit time is passed in explicitly — making billing fully deterministic and unit-testable.

---

## Custom Exception Hierarchy

```
RuntimeException
  └── ParkingLotException
        └── NoSlotAvailableException
```

`NoSlotAvailableException` is thrown by `ParkingLot.assignSlot()` when no slot matches the vehicle type. Callers can catch the specific exception to show a user-facing message, or catch the base `ParkingLotException` to handle all domain errors uniformly.

---

## How to Run

`ParkingLotDemo.main()` wires the entire system:

```java
// Create slots and lot
List<ParkingSlot> slots = List.of(
    new ParkingSlot("S1", VehicleType.FOUR_WHEELER),
    new ParkingSlot("S2", VehicleType.FOUR_WHEELER),
    new ParkingSlot("S3", VehicleType.TWO_WHEELER),
    new ParkingSlot("S4", VehicleType.TWO_WHEELER)
);
ParkingLot lot = new ParkingLot("LOT-1", slots, new FirstAvailableParkingSlotAssignmentStrategy());

// Wire billing
Map<VehicleType, CostCalculationStrategy> strategies = new EnumMap<>(VehicleType.class);
strategies.put(VehicleType.FOUR_WHEELER, new FourWheelerCostCalculationStrategy());
strategies.put(VehicleType.TWO_WHEELER,  new TwoWheelerCostCalculationStrategy());
BillingService billing = new BillingService(strategies);

// Gates
EntryGateService entry = new EntryGateService(lot);
ExitGateService exit   = new ExitGateService(billing, lot);

// Use
Vehicle car    = new Vehicle("MH12AB1234", VehicleType.FOUR_WHEELER);
Ticket  ticket = entry.issueTicket(car, 1);
double  cost   = exit.processExit(ticket, ticket.getEntryTime().plusHours(3));
// cost = 3 × 40.0 = 120.0
```

---

## Summary

This version delivers a complete, production-quality parking lot design. It removes all inheritance hierarchies in favor of interfaces and composition, eliminates factory if-else in favor of a strategy registry, makes domain objects immutable or state-guarded, and ensures all billing logic is deterministic. The result is a system where adding new vehicle types, new slot assignment strategies, or new pricing models requires zero changes to existing classes.
