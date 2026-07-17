# Design Evolution: v1 → v2

A side-by-side breakdown of every structural change from `parkingLotDesign` (v1) to `parkingLotDesignAfterReview` (v2), and the reasoning behind each decision.

---

## 1. Strategy Abstraction: Abstract Class → Interface

**v1**
```java
public abstract class ParkingStrategy {
    public abstract ParkingSlot findParkingSlot(List<ParkingSlot> slots,
                                                 VehicleType vehicleType,
                                                 int gateNumber);
}
public abstract class PricingStrategy {
    public double calculatePrice(Ticket ticket) { ... } // concrete base
}
```

**v2**
```java
public interface ParkingSlotAssignmentStrategy {
    ParkingSlot findSlot(List<ParkingSlot> slots, VehicleType vehicleType);
}
public interface CostCalculationStrategy {
    double calculateCost(Ticket ticket);
}
```

**Why it matters:**
Abstract classes lock the hierarchy into single inheritance and carry the risk of accumulated state and behavior leaking into subclasses. Interfaces are the correct abstraction when the contract is behavioral only — no shared state, no default logic. v1's `PricingStrategy` was a concrete class that subclasses extended, which breaks the Open/Closed Principle: changing base behavior would silently affect all subclasses.

---

## 2. Parking Slot Model: Inheritance → Composition

**v1** — separate subclass per vehicle type
```
ParkingSlot
├── FourWheelerParkingSlot  (hard-codes FOUR_WHEELER in constructor)
└── TwoWheelerParkingSlot   (hard-codes TWO_WHEELER in constructor)
```
`FourWheelerParkingSlot(int slotId, VehicleType vehicleType)` — accepts `vehicleType` but ignores it entirely. The Liskov Substitution Principle is violated: the subtype changes the constructor's effective contract without signalling it.

**v2** — single class, type as a field
```java
public class ParkingSlot {
    private final String id;
    private final VehicleType supportedVehicleType;
    private boolean occupied;

    public boolean supports(VehicleType type) {
        return this.supportedVehicleType == type;
    }
}
```

**Why it matters:**
The subclass hierarchy added zero behaviour — it only hard-coded a field value. Replacing it with a `supportedVehicleType` field eliminates two classes, removes the LSP violation, and makes filtering explicit via `supports(VehicleType)` rather than via instanceof checks or type casting.

---

## 3. Central Aggregate: Scattered Lists → `ParkingLot`

**v1** — raw `List<ParkingSlot>` passed between `EntranceGate`, `ExitGateService`, `ParkingSlotManager`, and the factory. Any caller could mutate a slot directly.

**v2** — `ParkingLot` owns all slots
```java
public class ParkingLot {
    private final Map<String, ParkingSlot> slotsById;
    private final ParkingSlotAssignmentStrategy assignmentStrategy;

    public ParkingSlot assignSlot(VehicleType vehicleType) { ... }
    public void releaseSlot(String slotId) { ... }
    public Optional<ParkingSlot> findSlot(String slotId) { ... }
}
```

**Why it matters:**
The Aggregate Root pattern ensures all mutations to the slot collection go through one controlled entry point. In v1 nothing prevented `ExitGateService` from marking a slot occupied, or `EntranceGate` from releasing one. `ParkingLot` makes those paths impossible. It also absorbs the entire `ParkingSlotManager` hierarchy (3 classes) into one class.

---

## 4. Manager Hierarchy Eliminated

**v1** — 3-class hierarchy
```
ParkingSlotManager
├── FourWheelerParkingSlotManager  (static field shadowing parent fields)
└── TwoWheelerParkingSlotManager
```
`FourWheelerParkingSlotManager` re-declared `parkingSlots` and `gateNumber` as instance fields, silently hiding the parent's fields. This is a classic field-hiding bug — the parent methods operated on the parent's fields; the subclass methods operated on the subclass's fields. Both existed in memory simultaneously.

**v2** — no manager layer at all. `ParkingLot` replaces all three classes.

---

## 5. Factory Classes → Strategy Registry

**v1** — two static factory classes with `if-else` chains
```java
public class CostComputationFactory {
    public static CostComputationService getCostComputationService(VehicleType type) {
        if (type == VehicleType.FOUR_WHEELER) return new FourWheelerCostComputationService();
        if (type == VehicleType.TWO_WHEELER)  return new TwoWheelerCostComputationService();
        throw new IllegalArgumentException(...);
    }
}
```
Adding a new `VehicleType` required editing both factory classes.

**v2** — `BillingService` holds an `EnumMap` registry
```java
public class BillingService {
    private final Map<VehicleType, CostCalculationStrategy> strategies;

    public double calculateCost(Ticket ticket) {
        CostCalculationStrategy strategy = strategies.get(ticket.getVehicle().getVehicleType());
        if (strategy == null) throw new IllegalArgumentException(...);
        return strategy.calculateCost(ticket);
    }
}
```
Adding a new vehicle type is a wiring change at the call site — `strategies.put(NEW_TYPE, new NewStrategy())` — with zero changes inside `BillingService`.

---

## 6. Cost Computation Service Hierarchy Eliminated

**v1** — abstract class + 2 subclasses
```
CostComputationService  (abstract; holds PricingStrategy)
├── FourWheelerCostComputationService  (injects HourlyPricingStrategy)
└── TwoWheelerCostComputationService   (injects MinutePricingStrategy)
```
5 classes total (3 services + 2 pricing strategies) to compute a cost.

**v2** — 2 flat `CostCalculationStrategy` implementations, no inheritance
```
FourWheelerCostCalculationStrategy  (implements CostCalculationStrategy)
TwoWheelerCostCalculationStrategy   (implements CostCalculationStrategy)
```
2 classes. The Template Method layer (`CostComputationService`) was replaced by straightforward delegation to a single interface method.

---

## 7. Pricing Determinism

**v1** — wall-clock time; non-deterministic
```java
// HourlyPricingStrategy
long elapsed = System.currentTimeMillis() - ticket.getIssueDateTime()
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
long hours = elapsed / (1000 * 60 * 60);
return hours * slot.getPrice();
```
Calling `computeCost()` one second later returns a different result. Impossible to unit test.

**v2** — exit time passed explicitly; fully deterministic
```java
// FourWheelerCostCalculationStrategy
long minutes = Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toMinutes();
long hours   = Math.max(1L, (minutes + 59) / 60);
return hours * RATE_PER_HOUR;
```
The same ticket always produces the same cost for the same exit time. Fully testable.

---

## 8. Ticket Model: Mutable Reference → Immutable + State Transition

**v1**
- All fields mutable (setters on everything)
- Holds a direct `ParkingSlot` object reference
- No status — no way to tell if a ticket is active or closed
- ID from `new Random().nextInt()` — can be negative, not unique

**v2**
- All identity fields are `final`
- Holds `String parkingSlotId` — not the slot object; decoupled
- Inner `TicketStatus` enum: `ACTIVE` / `CLOSED`
- One-way `close(LocalDateTime exitTime)` transition method; throws if already closed
- ID from `UUID.randomUUID().toString()`

```java
// Ticket state transition
public void close(LocalDateTime exitTime) {
    if (isClosed()) throw new IllegalStateException("Ticket already closed");
    this.exitTime = exitTime;
    this.status   = TicketStatus.CLOSED;
}
```

---

## 9. Vehicle Model: Mutable → Immutable

**v1**
```java
public class Vehicle {
    private String vehicleNumber;   // mutable; no null check
    private VehicleType vehicleType;
    // full setters
}
```

**v2**
```java
public final class Vehicle {
    private final String licenseNumber;
    private final VehicleType vehicleType;

    public Vehicle(String licenseNumber, VehicleType vehicleType) {
        this.licenseNumber = Objects.requireNonNull(licenseNumber);
        this.vehicleType   = Objects.requireNonNull(vehicleType);
    }
    // no setters
}
```
A `Vehicle` is a value object — its identity never changes after construction. Making it immutable prevents any service from accidentally reassigning `vehicleNumber` mid-flow.

---

## 10. Slot State Guards

**v1** — no guards
```java
slot.setAvailable(false);  // called anywhere by anyone
slot.setAvailable(true);   // same
```

**v2** — state validated on transition
```java
public void occupy() {
    if (occupied) throw new IllegalStateException("Slot " + id + " is already occupied");
    occupied = true;
}
public void release() {
    if (!occupied) throw new IllegalStateException("Slot " + id + " is already free");
    occupied = false;
}
```
Double-parking and double-releasing are caught immediately with a clear error rather than silently corrupting state.

---

## 11. Entry and Exit Flows: Fragmented → Atomic

**v1** — caller must coordinate 3 steps for entry, 2 for exit
```java
// EntranceGate — caller's responsibility to call all three
ParkingSlot slot   = gate.findParkingSpot(vehicleType);
Ticket      ticket = gate.issueTicket(vehicle, slot, gateNumber);
gate.parkVehicle(vehicle, slot);

// ExitGateService
double cost = exitService.computeCost(ticket);
exitService.freeParkingSlot(ticket);
```
If the caller forgets a step — or calls them out of order — the system is left in an inconsistent state.

**v2** — single method per gate, internally atomic
```java
Ticket ticket = entryGateService.issueTicket(vehicle, gateNumber);  // assign + occupy + build ticket
double cost   = exitGateService.processExit(ticket, exitTime);      // close + bill + release
```

---

## 12. Exception Hierarchy

**v1** — raw JDK exceptions
```java
throw new IllegalArgumentException("Unknown vehicle type: " + type);
```

**v2** — domain exception hierarchy
```
RuntimeException
  └── ParkingLotException
        └── NoSlotAvailableException
```
Callers can catch `NoSlotAvailableException` to show a user-facing "lot is full" message, or catch `ParkingLotException` to handle all domain errors in one place — without catching every `IllegalArgumentException` in the JVM.

---

## Summary Table

| Change | v1 | v2 | Principle |
|---|---|---|---|
| Strategy abstraction | Abstract class | Interface | OCP, DIP |
| Parking slot types | Subclass per type | Single class + `supports()` | LSP, composition over inheritance |
| Slot ownership | Raw lists | `ParkingLot` aggregate | Aggregate Root, encapsulation |
| Manager layer | 3-class hierarchy (with field hiding bug) | Eliminated | YAGNI, SRP |
| Factory dispatch | `if-else` factory classes | `EnumMap` registry in `BillingService` | OCP |
| Cost service | Abstract class + 2 subclasses + 2 pricing subclasses | 2 flat interface implementations | SRP, simplicity |
| Pricing determinism | `System.currentTimeMillis()` | `Duration.between(entry, exit)` | Testability |
| Ticket model | Mutable, no status, holds slot object | Immutable fields + `close()` transition + slot ID | Immutability, state safety |
| Vehicle model | Mutable, no null checks | `final` class, `final` fields, null-checked | Immutability |
| Slot state guards | None (`setAvailable(bool)`) | `occupy()` / `release()` with guards | Fail-fast |
| Entry flow | 3 caller-coordinated steps | 1 atomic `issueTicket()` | SRP, atomicity |
| Exit flow | 2 caller-coordinated steps | 1 atomic `processExit()` | SRP, atomicity |
| Exceptions | `IllegalArgumentException` | `ParkingLotException` hierarchy | Domain expressiveness |
