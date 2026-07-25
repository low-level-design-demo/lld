# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./mvnw clean package        # Compile and package
./mvnw spring-boot:run      # Run the application
./mvnw test                 # Run all tests
./mvnw test -Dtest=LldApplicationTests  # Run a single test class
```

Java 21, Spring Boot 4.x, Maven wrapper available as `./mvnw`.

## Architecture

This is an LLD (Low-Level Design) interview preparation codebase. Spring Boot is used purely as a build harness — the real content is standalone design pattern implementations under `src/main/java/com/demo/lld/`.

Each module is self-contained with:
- A `*Demo.java` entry point with a `main()` method for manual testing
- A `docs/` subdirectory with detailed design documentation

### Modules

| Package | Pattern(s) | Domain |
|---|---|---|
| `decorator/` | Decorator | Pizza toppings with stacked cost |
| `observer/` | Observer | iPhone stock alerts via email/mobile |
| `ticTacDesign/` | Template Method | Tic-Tac-Toe game loop |
| `parkingLotDesign/` | Strategy, Factory, Template Method | Parking lot v1 (original) |
| `parkingLotDesignAfterReview/` | Strategy, Aggregate Root | Parking lot v2 (refactored) |

### Parking Lot v1 → v2 Design Evolution

The most instructive part of the codebase. Key improvements in v2:
- **Strategy interfaces** replace abstract classes — `ParkingSlotAssignmentStrategy`, `CostCalculationStrategy`
- **Strategy Registry** (`EnumMap<VehicleType, CostCalculationStrategy>` in `BillingService`) replaces factory if-else chains — adding a vehicle type no longer requires touching the factory
- **`ParkingLot` as Aggregate Root** owns all slot mutations (`assignSlot`, `releaseSlot`), preventing external corruption
- **Exception hierarchy** (`ParkingLotException` → `NoSlotAvailableException`) replaces null returns
- **Immutable `Ticket`** (assigned at entry, `checkOutTime` set at exit) replaces mutable model

See `parkingLotDesignAfterReview/docs/design_evolution.md` for a full side-by-side comparison.

## Adding New Design Patterns

Follow the existing module convention:
1. Create a new package under `com.demo.lld/<patternName>/`
2. Add a `docs/<patternName>_docs.md` with design rationale
3. Add a `*Demo.java` with a `main()` to exercise the pattern
4. No Spring annotations needed — modules are plain Java

To document a pattern after implementation, use `/document-pattern <packageName>`.
