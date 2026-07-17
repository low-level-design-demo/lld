# Parking Lot Design (v1 — Before Review)

## What It Is

A parking lot management system that models the entry, slot assignment, and exit billing flow. This version demonstrates the **Strategy**, **Factory Method**, and **Template Method** patterns — but also carries several design issues that are fixed in the reviewed version.

---

## Code Structure

```
parkingLotDesign
├── model/
│   ├── VehicleType            (enum: FOUR_WHEELER, TWO_WHEELER)
│   ├── Vehicle                (mutable; holds vehicleNumber + vehicleType)
│   ├── ParkingSlot            (base class; holds slotNumber, isAvailable, price)
│   ├── FourWheelerParkingSlot (extends ParkingSlot; hard-codes FOUR_WHEELER type)
│   ├── TwoWheelerParkingSlot  (extends ParkingSlot; hard-codes TWO_WHEELER type)
│   └── Ticket                 (mutable; holds vehicle, slot object, issueDateTime, gateNumber)
├── strategy/
│   ├── ParkingStrategy        (abstract class — parking slot selection contract)
│   ├── NearestToEntranceParkingStrategy            (stub — returns null)
│   ├── NearestToEntranceAndElevatorParkingStrategy (stub — returns null)
│   ├── PricingStrategy        (concrete base class — returns base slot price)
│   ├── HourlyPricingStrategy  (extends PricingStrategy — hours × base price)
│   └── MinutePricingStrategy  (extends PricingStrategy — minutes × base price)
├── manager/
│   ├── ParkingSlotManager             (manages a list of slots + strategy)
│   ├── FourWheelerParkingSlotManager  (hard-wires NearestToEntranceAndElevator)
│   └── TwoWheelerParkingSlotManager   (hard-wires NearestToEntrance)
├── factory/
│   ├── ParkingSlotManagerFactory  (returns correct manager subclass by VehicleType)
│   └── CostComputationFactory     (returns correct cost service by VehicleType)
└── service/
    ├── CostComputationService             (abstract; holds PricingStrategy)
    ├── FourWheelerCostComputationService  (injects HourlyPricingStrategy)
    ├── TwoWheelerCostComputationService   (injects MinutePricingStrategy)
    ├── EntranceGate                       (findParkingSpot + issueTicket + parkVehicle)
    └── ExitGateService                    (computeCost + freeParkingSlot)
```

---

## Flow

```
EntranceGate
  │
  ├── findParkingSpot(vehicleType)
  │     └── ParkingSlotManagerFactory → correct Manager subclass
  │           └── manager.findParkingSlot() → strategy.findParkingSlot() → [STUB: null]
  │
  ├── issueTicket(vehicle, slot, gateNumber)
  │     └── new Ticket(Random.nextInt(), vehicle, LocalDateTime.now(), slot, gate)
  │
  └── parkVehicle(vehicle, slot)
        └── ParkingSlotManagerFactory → manager.parkVehicle() → slot.setAvailable(false)

ExitGateService
  │
  ├── computeCost(ticket)
  │     └── CostComputationFactory → FourWheelerCostComputationService
  │           └── HourlyPricingStrategy.calculatePrice()
  │                 └── (System.currentTimeMillis() - issueDateTime) × slotPrice
  │
  └── freeParkingSlot(ticket)
        └── ParkingSlotManagerFactory → manager.removeVehicle() → slot.setAvailable(true)
```

---

## Design Patterns Used

### 1. Strategy Pattern
`ParkingStrategy` declares the slot-finding contract. Subclasses provide implementations for different strategies (nearest to entrance, nearest to elevator). Similarly, `PricingStrategy` is the base for `HourlyPricingStrategy` and `MinutePricingStrategy`.

> **Issue:** Both parking strategy implementations are stubs that return `null`. The strategy abstraction exists but is never actually exercised.

### 2. Template Method Pattern
`CostComputationService` is an abstract class that holds a `PricingStrategy` and declares the abstract `computeCost()`. Subclasses (`FourWheelerCostComputationService`, `TwoWheelerCostComputationService`) fill in which pricing strategy to inject.

### 3. Factory Method Pattern
`CostComputationFactory` and `ParkingSlotManagerFactory` map `VehicleType` to the correct service/manager subclass via `if-else` chains.

---

## SOLID Principles — Where Applied and Where Violated

| Principle | Status | Detail |
|---|---|---|
| **S — Single Responsibility** | Partial | `EntranceGate` handles spot-finding, ticket issuance, and vehicle parking in three separate methods that the caller must coordinate. A split of concerns exists but the coordination responsibility leaks to the caller. |
| **O — Open/Closed** | Violated | Adding a new `VehicleType` requires editing both factory classes (`CostComputationFactory`, `ParkingSlotManagerFactory`). |
| **L — Liskov Substitution** | Violated | `FourWheelerParkingSlot(int slotId, VehicleType vehicleType)` accepts a `VehicleType` param but ignores it entirely — the subtype changes the constructor contract invisibly. |
| **D — Dependency Inversion** | Partial | `CostComputationService` depends on the `PricingStrategy` class (concrete), not an interface. `FourWheelerParkingSlotManager` hard-wires `new NearestToEntranceAndElevatorParkingStrategy()` as a `static` field. |

---

## Known Issues in This Version

| Issue | Location | Detail |
|---|---|---|
| **Stub strategies** | `NearestToEntrance...`, `NearestToEntranceAndElevator...` | Both `findParkingSlot()` return `null` — slot assignment never works |
| **Non-deterministic pricing** | `HourlyPricingStrategy`, `MinutePricingStrategy` | Uses `System.currentTimeMillis()` instead of a passed-in exit time — impossible to unit test |
| **Field hiding** | `FourWheelerParkingSlotManager` | Re-declares `parkingSlots` and `gateNumber` fields already in the parent, silently shadowing them |
| **Mutable Vehicle** | `Vehicle` | No null checks; all fields have setters — Vehicle state can be corrupted after construction |
| **Ticket holds ParkingSlot object** | `Ticket` | Stores a direct reference to the `ParkingSlot` — leaks model internals and prevents the slot from being independently managed |
| **Random ticket ID** | `EntranceGate.issueTicket()` | `new Random().nextInt()` can produce negatives and is not globally unique |
| **Unused Spring import** | `EntranceGate` | `import org.springframework.data.annotation.Indexed` imported but never used |
| **No ticket status** | `Ticket` | No distinction between an active and a closed ticket |
| **Static field strategy** | `FourWheelerParkingSlotManager` | `parkingStrategy` is `static` — all instances share one strategy instance, preventing per-instance customization |

---

## Class Relationships

```
Vehicle ──────────────────────────────────────────────────── Ticket
ParkingSlot ◄─── FourWheelerParkingSlot                        │
            ◄─── TwoWheelerParkingSlot                         │
ParkingStrategy ◄─── NearestToEntrance...                      │
                ◄─── NearestToEntranceAndElevator...            │
PricingStrategy ◄─── HourlyPricingStrategy                     │
                ◄─── MinutePricingStrategy                     │
ParkingSlotManager ◄─── FourWheelerParkingSlotManager          │
                   ◄─── TwoWheelerParkingSlotManager           │
CostComputationService ◄─── FourWheelerCostComputationService  │
                       ◄─── TwoWheelerCostComputationService   │
CostComputationFactory ──── creates ──► CostComputationService │
ParkingSlotManagerFactory ── creates ──► ParkingSlotManager    │
EntranceGate ──── uses ──► ParkingSlotManagerFactory           │
ExitGateService ── uses ──► CostComputationFactory             │
```

---

## What This Version Gets Right

- The **layered structure** (model / strategy / manager / factory / service) is clear and navigable.
- The **separation between slot management and cost computation** is a sound architectural instinct.
- The factory classes correctly isolate the `VehicleType → implementation` dispatch in one place.
- The **Template Method** in `CostComputationService` is a clean approach to injecting different pricing strategies.

---

## Summary

This version establishes the right vocabulary and separation of concerns for a parking lot system, but leaves key abstractions unimplemented, introduces mutable models that are easy to corrupt, and ties pricing to wall-clock time rather than a deterministic exit timestamp. These issues are all addressed in `parkingLotDesignAfterReview`.
