# Observer Pattern

### Section 1 — What It Is

The Observer pattern defines a one-to-many dependency between objects so that when one object (the **Subject**) changes state, all its dependents (**Observers**) are notified and updated automatically. The core problem it solves is decoupling the state-holder from the objects that react to state changes — the **Subject** knows only that it has a list of **Observers** implementing a common interface, not what they do with the notification.

---

### Section 2 — Code Structure (This Implementation)

**Class / Interface Tree**

```
StockObservable (interface — Subject)
│  addObserver(UserObserver)
│  removeObserver(UserObserver)
│  notifyObservers()
│  setData(int)
│
└── IphoneStockObservable (ConcreteSubject)
      - int count
      - List<UserObserver> observers

UserObserver (interface — Observer)
│  update()
│
├── EmailObserver  (ConcreteObserver)  ──── holds ref to StockObservable
└── MobileObserver (ConcreteObserver) ──── holds ref to StockObservable

ObserverDemo (Demo / Client)
```

**Runtime Call Flow**

```
ObserverDemo.main()
  │
  ├─ new IphoneStockObservable()          → ConcreteSubject created (count=0)
  ├─ new EmailObserver("rishi@gmail.com") → ConcreteObserver created
  ├─ new MobileObserver("98767878")       → ConcreteObserver created
  │
  ├─ stockObservable.addObserver(emailObserver)
  ├─ stockObservable.addObserver(mobileObserver)
  │
  └─ stockObservable.setData(10)
       │
       ├─ this.count = 0 + 10 = 10  (count > 0, so notify)
       └─ notifyObservers()
            ├─ emailObserver.update()  → prints "Email sent to rishi@gmail.com"
            └─ mobileObserver.update() → prints "Mobile number 98767878 updated"
```

---

### Section 3 — SOLID Principles Applied

| Principle | How |
|-----------|-----|
| **S**ingle Responsibility | `IphoneStockObservable` manages stock state and notification dispatch. `EmailObserver` and `MobileObserver` each handle one delivery channel. No class does both. |
| **O**pen/Closed | New observer types (e.g., `SlackObserver`) can be added without touching `IphoneStockObservable` or `StockObservable`. |
| **L**iskov Substitution | `EmailObserver` and `MobileObserver` are fully substitutable for `UserObserver`; `IphoneStockObservable` is substitutable for `StockObservable`. The demo uses both only through their interfaces. |
| **I**nterface Segregation | `UserObserver` exposes only `update()`; `StockObservable` exposes only the four subject-side methods. Neither interface forces implementors to carry unneeded methods. |
| **D**ependency Inversion | `ObserverDemo` depends on `StockObservable` and `UserObserver` interfaces, not on `IphoneStockObservable`, `EmailObserver`, or `MobileObserver` concrete types. |

---

### Section 4 — When to Use This Pattern

#### Use when
- Multiple independent components must react to state changes in a single source of truth (e.g., stock level, user login, sensor reading).
- The set of listeners is unknown at compile time or changes at runtime (`addObserver` / `removeObserver`).
- You want to broadcast an event without the sender knowing receivers' identities or counts.
- Cross-cutting concerns (logging, metrics, notifications) must not pollute core domain logic.
- You need to support multiple delivery channels (email, SMS, push) for the same event without conditional branching in the sender.

#### Avoid when
- There is exactly one fixed listener — a direct method call is simpler and faster.
- Notification order is safety-critical and the pattern's unordered iteration cannot be tolerated.
- The state change graph is a complex DAG — cascading updates between observers create update storms that are hard to reason about.
- The subject and observer live in different processes; use a message broker instead (the pattern's in-process coupling becomes a distributed systems problem).

---

### Section 5 — Real Production Implementations

#### 1. Kafka — Event Streaming (Apache Kafka)

```
IphoneStockObservable          Kafka Topic              EmailObserver
  (Producer)        ──event──▶  "stock-updates"  ──▶   (Consumer Group A)
                                      │
                                      └──────────────▶  MobileObserver
                                                        (Consumer Group B)
```

`IphoneStockObservable.setData()` maps to a Kafka producer publishing to a topic; each `UserObserver` maps to an independent consumer group that reads the same topic. Amazon uses this at scale for order and inventory events.

```java
// Producer side (maps to setData / notifyObservers)
producer.send(new ProducerRecord<>("stock-updates", "iphone", "count=10"));

// Consumer side (maps to update())
ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
records.forEach(r -> System.out.println("Notified: " + r.value()));
```

---

#### 2. Spring Framework — ApplicationEvent / ApplicationListener

```
IphoneStockObservable          ApplicationEventPublisher     EmailObserver
  (publisher)       ──event──▶  publishEvent(StockEvent)  ──▶ onApplicationEvent()
```

`IphoneStockObservable.notifyObservers()` maps to `ApplicationEventPublisher.publishEvent()`; each `UserObserver` maps to a `@EventListener` method. Spring Boot wires listeners automatically via the application context.

```java
@Component
public class StockService implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher publisher;
    public void setData(int count) { publisher.publishEvent(new StockEvent(count)); }
    public void setApplicationEventPublisher(ApplicationEventPublisher p) { publisher = p; }
}

@Component
public class EmailListener {
    @EventListener
    public void onStock(StockEvent e) { System.out.println("Email sent for stock: " + e.getCount()); }
}
```

---

#### 3. RxJS / Angular — Reactive Streams

```
IphoneStockObservable     BehaviorSubject<number>    EmailObserver
  (Subject)     ──next──▶   stockSubject$          ──▶ subscribe(update)
```

`stockSubject$.next(count)` maps to `setData`; `.subscribe()` maps to `addObserver`. Angular uses `BehaviorSubject` in services to push state to components without direct coupling.

```typescript
const stockSubject$ = new BehaviorSubject<number>(0);

// addObserver equivalent
stockSubject$.subscribe(count => console.log(`Email sent, stock: ${count}`));
stockSubject$.subscribe(count => console.log(`SMS sent, stock: ${count}`));

// setData equivalent
stockSubject$.next(10);
```

---

#### 4. Java Swing — Event Listeners (JDK)

```
IphoneStockObservable    JButton (ActionSource)    EmailObserver
  (subject)     ──────▶  addActionListener()  ──▶ actionPerformed()
```

`ActionListener.actionPerformed()` is the `update()` method; `JButton` is the **ConcreteSubject**. JDK's entire UI event model is built on this pattern.

```java
JButton button = new JButton("Notify");
button.addActionListener(e -> System.out.println("Email sent"));
button.addActionListener(e -> System.out.println("SMS sent"));
// clicking button calls both listeners — identical to notifyObservers()
```

---

#### 5. Node.js — EventEmitter

```
IphoneStockObservable    EventEmitter        EmailObserver
  (subject)     ──emit──▶ "stock"  ──────▶  on("stock", handler)
```

`emitter.emit("stock", count)` maps to `notifyObservers()`; `emitter.on("stock", fn)` maps to `addObserver`. Node's entire I/O model (streams, HTTP server, file system) uses `EventEmitter`. Used by Netflix for server-side event routing.

```javascript
const EventEmitter = require("events");
const emitter = new EventEmitter();

emitter.on("stock", (count) => console.log(`Email sent, stock: ${count}`));
emitter.on("stock", (count) => console.log(`SMS sent, stock: ${count}`));

emitter.emit("stock", 10);
```

---

### Section 6 — Trade-offs vs Other Design Patterns

#### Observer vs Mediator

| | Observer | Mediator |
|---|---|---|
| Communication | **Subject** broadcasts to all registered **Observers** directly | All objects talk through a central mediator; no direct peer links |
| Coupling | **Subject** coupled to **Observer** interface; **Observers** may hold a back-reference to **Subject** | Each colleague coupled only to the mediator |
| When to use | One authoritative state source, many independent consumers | Complex many-to-many interactions where peers need to coordinate |
| Risk | Update storms when observers themselves change state; hard to trace cascades | Mediator becomes a god object; single point of failure |

**Rule of thumb:** use Observer when one thing changes and many react independently; use Mediator when many things must negotiate with each other.

---

#### Observer vs Event Bus / Message Broker

| | Observer | Event Bus |
|---|---|---|
| Communication | In-process synchronous call chain | Async, decoupled by queue or topic |
| Coupling | **Subject** holds observer list in memory | Publisher and consumer share only a topic name |
| When to use | Same JVM, low latency needed, simple fan-out | Cross-service, cross-process, or when backpressure / replay is needed |
| Risk | Blocking the notification thread if any observer is slow | Operational overhead, message ordering guarantees vary by broker |

**Rule of thumb:** use Observer inside a service boundary; use a message broker across service boundaries.

---

#### Observer vs Strategy

| | Observer | Strategy |
|---|---|---|
| Communication | **Subject** calls `update()` on a list of listeners | Context calls one algorithm through a common interface |
| Coupling | One-to-many; subject unaware of what observers do | One-to-one; context depends on the algorithm type |
| When to use | Reacting to state changes in many places | Swapping a single algorithm at runtime |
| Risk | Proliferating observers with overlapping responsibilities | Overkill if only one algorithm is used |

**Rule of thumb:** use Observer for event fanout; use Strategy for runtime algorithm selection.

---

#### Observer vs Pub/Sub (in-process)

| | Observer | Pub/Sub |
|---|---|---|
| Communication | **Subject** notifies observers directly via reference | Publisher fires to a channel; broker dispatches to subscribers |
| Coupling | **Subject** and **Observer** share interfaces | Publisher and subscriber are fully decoupled — never reference each other |
| When to use | Tight feedback loop, same module | Cross-module events, plugin systems, frameworks |
| Risk | Can't filter or route events by topic within the pattern itself | Harder to trace event flow; requires a dispatcher component |

**Rule of thumb:** use Observer when the subject and its listeners are in the same bounded context; use Pub/Sub when they must not know about each other.

---

### Section 7 — Production Pitfalls

| Pitfall | Problem | Fix |
|---------|---------|-----|
| **Memory leak via stale observer** | `addObserver` adds a reference; if `removeObserver` is never called the **Subject** retains the **Observer** indefinitely, preventing GC. `IphoneStockObservable.observers` grows unbounded. | Always call `removeObserver` in the observer's teardown/lifecycle hook. Use `WeakReference<UserObserver>` in the list so dead observers are collected automatically. |
| **Thread-safety on observer list** | `IphoneStockObservable` iterates `observers` in `notifyObservers()` while another thread may call `addObserver` or `removeObserver`, causing `ConcurrentModificationException`. | Replace `ArrayList` with `CopyOnWriteArrayList`; or synchronize both `notifyObservers()` and list-mutation methods on the same lock. |
| **Blocking notification thread** | `notifyObservers()` calls `observer.update()` synchronously and sequentially. If `EmailObserver.update()` makes a blocking network call, the entire notification chain stalls. | Dispatch each `update()` call to a thread pool (`ExecutorService`) or use a reactive pipeline so observers run concurrently and do not block each other. |
| **Undefined notification order** | `IphoneStockObservable` iterates `observers` in insertion order. Code that relies on `EmailObserver` always firing before `MobileObserver` will break if registration order changes. | Never write observer logic that assumes a specific sibling fires first. If ordering is required, use a priority queue or a chain-of-responsibility instead. |
| **Duplicate registration** | Calling `addObserver(emailObserver)` twice adds the same instance twice; `emailObserver.update()` fires twice per event with no runtime error. | Check for duplicates before adding: `if (!observers.contains(observer)) observers.add(observer);`, or use a `LinkedHashSet`. |
| **Exception in one observer aborts the rest** | If `emailObserver.update()` throws an unchecked exception, `notifyObservers()` never reaches `mobileObserver.update()`. | Wrap each `observer.update()` call in a try-catch inside the notification loop; log the exception and continue to the next observer. |

---

### Section 8 — Summary

The Observer pattern excels at decoupling a state source from an open-ended set of consumers that must react to its changes without the source knowing who they are or what they do. In the wild you encounter it in every event-driven framework: JDK `ActionListener`, Spring `ApplicationEvent`, RxJS `Subject`, Kafka consumers, and Node.js `EventEmitter` all implement the same structure as `IphoneStockObservable` / `UserObserver`.
