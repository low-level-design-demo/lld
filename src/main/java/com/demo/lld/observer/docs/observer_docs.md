# Observer Design Pattern

## What It Is

The Observer pattern defines a **one-to-many dependency** between objects. When the subject (observable) changes state, all its dependents (observers) are notified and updated automatically.

---

## Code Structure (This Implementation)

```
StockObservable (interface) ─── Subject contract
    └── IphoneStockObservable  ─── Concrete subject: holds state + observer list

UserObserver (interface) ──────── Observer contract
    ├── EmailObserver          ─── Sends email notification
    └── MobileObserver         ─── Sends SMS notification

ObserverDemo ──────────────────── Wires everything together
```

### Flow

```
ObserverDemo
  │
  ├── creates IphoneStockObservable (subject)
  ├── creates EmailObserver + MobileObserver
  ├── registers both via addObserver()
  └── calls setData(10)
        │
        └── IphoneStockObservable.setData()
              ├── updates count
              └── if count > 0 → notifyObservers()
                    ├── EmailObserver.update()  → "Email sent to rishi@gmail.com"
                    └── MobileObserver.update() → "Mobile number 98767878 updated"
```

---

## SOLID Principles Applied

| Principle | How |
|---|---|
| **S — Single Responsibility** | `IphoneStockObservable` manages stock state; `EmailObserver` handles email logic; `MobileObserver` handles SMS logic. Each class has one reason to change. |
| **O — Open/Closed** | Adding a new notification channel (e.g., `PushNotificationObserver`) requires zero changes to existing classes — just implement `UserObserver` and register it. |
| **L — Liskov Substitution** | `EmailObserver` and `MobileObserver` are interchangeable wherever `UserObserver` is expected. |
| **D — Dependency Inversion** | `IphoneStockObservable` depends on the `UserObserver` interface, not on concrete `EmailObserver` or `MobileObserver` classes. |

> **I (Interface Segregation)** is less prominent here but is respected — `UserObserver` exposes only `update()`, keeping the contract minimal.

---

## When to Use This Pattern

Use Observer when:

- **State change in one object should trigger actions in others** — and you don't want the subject to know who those others are.
- **The number or type of dependents can vary at runtime** — observers can be added/removed dynamically.
- **You want loose coupling** — the subject and observers evolve independently.
- **Fan-out notifications** — one event, multiple handlers (email + SMS + push + logging).

Avoid Observer when:
- The notification chain is deeply nested (observer triggers another observable) — can lead to hard-to-debug cascades.
- All observers are known at compile time and never change — a direct method call is simpler.
- Order of observer execution matters — the standard pattern gives no ordering guarantees.

---

## Real Production Implementations

### 1. Event-Driven Microservices (Kafka / RabbitMQ)

In distributed systems, the Observer pattern is the backbone of event streaming.

```
Order Service (Subject)
    │
    └── publishes OrderPlaced event to Kafka topic
          │
          ├── InventoryService (Observer) → reserves stock
          ├── NotificationService (Observer) → emails the customer
          ├── InvoiceService (Observer) → generates invoice
          └── AnalyticsService (Observer) → records the sale
```

- The `Order Service` does not import or know about any downstream service.
- New consumers (observers) subscribe to the Kafka topic without touching `Order Service`.
- This is the Observer pattern scaled to distributed systems — the "observable" is a Kafka topic, the "update()" is message consumption.

**Real example:** Amazon's order pipeline, Uber's trip events, Swiggy's delivery tracking.

---

### 2. Spring Framework — `ApplicationEventPublisher`

Spring's built-in event system is a direct implementation of Observer.

```java
// Subject publishes an event
applicationEventPublisher.publishEvent(new UserRegisteredEvent(user));

// Observers (listeners) react
@EventListener
public void onUserRegistered(UserRegisteredEvent event) {
    emailService.sendWelcomeEmail(event.getUser());
}

@EventListener
public void onUserRegistered(UserRegisteredEvent event) {
    analyticsService.track("signup", event.getUser().getId());
}
```

Used heavily in Spring Boot for decoupling cross-cutting concerns like audit logging, notifications, and metrics from core business logic.

---

### 3. UI Frameworks — React / Angular Change Detection

Frontend reactive frameworks implement Observer at their core.

- **React:** `useState` + re-render — when state changes, the component (observer) re-renders.
- **RxJS (Angular):** `Observable.subscribe()` is literally the pattern — streams are subjects, subscribers are observers.
- **Redux:** The store is the subject; connected components are observers via `useSelector`.

```ts
// RxJS — explicit Observer pattern
const stock$ = new Subject<number>();

stock$.subscribe(price => updatePriceLabel(price));
stock$.subscribe(price => triggerAlertIfDropped(price));

stock$.next(150); // notifies all subscribers
```

---

### 4. Database Change Data Capture (CDC)

Tools like **Debezium** watch a database (subject) and stream row-level changes to consumers (observers):

```
PostgreSQL (subject)
    └── Debezium captures INSERT/UPDATE/DELETE
          ├── Search index (Elasticsearch) stays in sync
          ├── Cache (Redis) gets invalidated
          └── Audit log gets an entry
```

The database has no knowledge of Elasticsearch or Redis — classic Observer decoupling.

---

### 5. Stock / Financial Market Data Feeds

Identical to this implementation's domain. Bloomberg Terminal, NSE/BSE feeds:

```
MarketDataFeed (subject)
    └── price tick arrives
          ├── TradingAlgorithm1.onTick()
          ├── RiskEngine.onTick()
          ├── PnLCalculator.onTick()
          └── UIChart.onTick()
```

High-performance variants use **lock-free ring buffers** (e.g., LMAX Disruptor) instead of an ArrayList of observers, but the pattern is identical.

---

## Trade-offs vs Other Design Patterns

### Observer vs Mediator

| | Observer | Mediator |
|---|---|---|
| **Communication** | Subject → Observers (broadcast) | All components ↔ Mediator (hub-and-spoke) |
| **Coupling** | Subject knows observer interface; observers may know subject | All components know only the mediator |
| **When to use** | One subject, many independent listeners | Many components that need to coordinate with each other |
| **Risk** | Cascade of notifications; hard to trace flow | Mediator becomes a god object |

**Example:** A chat room uses Mediator (the room routes messages). A stock price alerting system uses Observer (the price notifies all watchers).

---

### Observer vs Strategy

| | Observer | Strategy |
|---|---|---|
| **Intent** | Notify many about a state change | Swap one algorithm at runtime |
| **Cardinality** | One subject, many observers | One context, one strategy at a time |
| **Trigger** | Subject-driven (push) | Client-driven (explicit call) |

---

### Observer vs Pub-Sub

Pub-Sub is a **distributed** evolution of Observer with a message broker in between.

| | Observer | Pub-Sub |
|---|---|---|
| **Coupling** | Subject holds reference to observers | Publisher and subscriber never reference each other |
| **Broker** | None — direct call | Message broker (Kafka, RabbitMQ, SNS) |
| **Async** | Usually synchronous | Always asynchronous |
| **Delivery** | Guaranteed (in-process) | At-least-once / exactly-once depends on broker config |
| **Scale** | Same JVM / process | Cross-service, cross-region |

**Rule of thumb:** Observer for in-process event handling; Pub-Sub for cross-service communication.

---

### Observer vs Event Bus (Guava EventBus / Spring Events)

An Event Bus is Observer with a registry layer — observers don't need a direct reference to the subject, they register with the bus. Spring's `ApplicationEventPublisher` is this pattern. It solves the problem of subjects needing to hold observer lists themselves.

---

## Production Pitfalls to Watch

| Pitfall | Problem | Fix |
|---|---|---|
| **Memory leaks** | Observer registered but never removed — subject holds a strong reference | Use `WeakReference` or ensure explicit `removeObserver()` on teardown |
| **Notification storms** | One state change triggers a chain of observers that each trigger more changes | Use async dispatch or a circuit breaker |
| **Ordering dependency** | Observer B assumes Observer A already ran | Don't assume order; make observers independent |
| **Slow observers** | One blocking observer slows all others | Dispatch notifications asynchronously (thread pool / event queue) |
| **Thread safety** | Multiple threads calling `addObserver`/`notifyObservers` concurrently | Use `CopyOnWriteArrayList` instead of `ArrayList` for the observer list |

---

## Summary

The Observer pattern excels at **decoupling state changes from their side effects**. In production, you will encounter it as Kafka consumers, Spring events, RxJS streams, React hooks, and CDC pipelines. The core contract is always the same: the subject knows nothing about what its observers do — it just calls `update()`.
