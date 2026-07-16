# Decorator Pattern

### Section 1 — What It Is

The Decorator pattern attaches additional responsibilities to an object dynamically by wrapping it in one or more decorator objects that share the same abstract type. The core problem it solves is combinatorial class explosion: instead of creating `VegPizzaWithMushroom`, `VegPizzaWithSpicy`, `VegPizzaWithMushroomAndSpicy`, etc., you compose behaviors at runtime by nesting wrappers around a base object. Each wrapper delegates to the component it wraps and adds its own contribution before or after the delegation.

---

### Section 2 — Code Structure (This Implementation)

**Class / Interface Tree**

```
BasePizza (abstract Component)
│  + calculateCost(): int
│
├── VegPizza      (ConcreteComponent) — base cost 12
├── MargaritaPizza (ConcreteComponent) — base cost 15
│
└── Decorator (abstract ConcreteDecorator base) extends BasePizza
      │
      ├── MushRoomDecorator (ConcreteDecorator)
      │     - BasePizza basePizza  ← wraps any BasePizza
      │     + calculateCost(): mushRoomToppingCost(5) + basePizza.calculateCost()
      │
      └── SpicyDecorator    (ConcreteDecorator)
            - BasePizza basePizza  ← wraps any BasePizza
            + calculateCost(): spicyToppingCost(5) + basePizza.calculateCost()

DecoratorDemo (Demo / Client)
```

**Runtime Call Flow**

```
DecoratorDemo.main()
  │
  ├─ Case 1: VegPizza + Mushroom
  │    new VegPizza()                                → basePizza (cost=12)
  │    new MushRoomDecorator(basePizza)              → mushroomDecorator
  │    mushroomDecorator.calculateCost()
  │      ├─ mushRoomToppingCost = 5
  │      └─ basePizza.calculateCost()               → prints "Cost of VegPizza: 12", returns 12
  │    returns 5 + 12 = 17
  │    prints "Cost of Veg Pizza with Mushroom Topping: 17"
  │
  └─ Case 2: MargaritaPizza + Spicy + Mushroom
       new MargaritaPizza()                          → basePizza1 (cost=15)
       new SpicyDecorator(basePizza1)                → spicyDecorator (inner)
       new MushRoomDecorator(spicyDecorator)         → mushroomDecorator2 (outer)
       mushroomDecorator2.calculateCost()
         ├─ mushRoomToppingCost = 5
         └─ spicyDecorator.calculateCost()
               ├─ spicyToppingCost = 5
               └─ basePizza1.calculateCost()        → prints "Cost of MargaritaPizza: 15", returns 15
            returns 5 + 15 = 20
         returns 5 + 20 = 25
       prints "Cost of Margarita Pizza with Spicy Topping: 25"
```

---

### Section 3 — SOLID Principles Applied

| Principle | How |
|-----------|-----|
| **S**ingle Responsibility | `VegPizza` only knows its base cost. `MushRoomDecorator` only knows mushroom topping cost and delegation. No class handles both base pricing and topping logic. |
| **O**pen/Closed | New toppings (e.g., `CheeseDecorator`) are added as new classes without modifying `BasePizza`, `VegPizza`, or existing decorators. |
| **L**iskov Substitution | `MushRoomDecorator` and `SpicyDecorator` are fully substitutable for `BasePizza`; `DecoratorDemo` passes `new SpicyDecorator(basePizza1)` where a `BasePizza` is expected and the contract holds. |
| **I**nterface Segregation | `BasePizza` exposes only `calculateCost()`; no implementor is forced to implement unneeded methods. Modestly demonstrated given the small interface. |
| **D**ependency Inversion | `MushRoomDecorator` and `SpicyDecorator` depend on `BasePizza` (the abstraction), not on `VegPizza` or `MargaritaPizza` concretions — enabling arbitrary nesting. |

---

### Section 4 — When to Use This Pattern

#### Use when
- You need to add behaviors to individual objects, not to an entire class, and combinations of behaviors should be composable at runtime.
- Subclassing would produce an exponential number of classes (e.g., every combination of N toppings over M pizza bases).
- The wrapped component's interface must remain unchanged to callers — `DecoratorDemo` calls `calculateCost()` on both raw pizzas and decorated ones.
- Behaviors must be stackable and order-sensitive (outer decorator runs before inner).
- You are extending third-party or final classes that cannot be subclassed.

#### Avoid when
- There is only one fixed behavior to add — a simple subclass or method parameter is clearer.
- The decorator chain is always the same at runtime; a hardcoded composite object or a Builder is more readable.
- Consumers need to inspect the runtime type of the object (decorator identity is opaque — `instanceof MushRoomDecorator` is fragile across nested chains).
- The component interface has many methods; every decorator must delegate them all, creating verbose boilerplate.

---

### Section 5 — Real Production Implementations

#### 1. Java I/O Streams (JDK)

```
BasePizza                   InputStream / OutputStream
VegPizza          ──────▶   FileInputStream           (ConcreteComponent)
MushRoomDecorator ──────▶   BufferedInputStream       (ConcreteDecorator)
SpicyDecorator    ──────▶   GZIPInputStream           (ConcreteDecorator)
```

`BufferedInputStream` wraps any `InputStream` and delegates `read()` while adding buffering — identical to `MushRoomDecorator` wrapping `BasePizza` and delegating `calculateCost()`. Used by every Java application that reads files.

```java
InputStream raw    = new FileInputStream("data.bin");
InputStream buf    = new BufferedInputStream(raw);
InputStream zipped = new GZIPInputStream(buf);
int b = zipped.read(); // chains through 3 decorators
```

---

#### 2. Spring Security — Filter Chain

```
MargaritaPizza                 HttpServletRequest
SpicyDecorator      ──────▶   CsrfFilter            (ConcreteDecorator)
MushRoomDecorator   ──────▶   UsernamePasswordFilter (ConcreteDecorator)
                               ResourceController     (ConcreteComponent)
```

Each Spring Security `Filter` wraps the next `FilterChain`, pre-processing the request (authentication, CSRF) then delegating — mirroring `SpicyDecorator` adding cost then calling `basePizza.calculateCost()`. Spring Boot assembles the chain at startup.

```java
@Component
public class LoggingFilter extends OncePerRequestFilter {
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("Before: " + req.getRequestURI());
        chain.doFilter(req, res);
        System.out.println("After");
    }
}
```

---

#### 3. Node.js / Express — Middleware

```
VegPizza            express.Router (ConcreteComponent)
MushRoomDecorator   app.use(logger)   (ConcreteDecorator)
SpicyDecorator      app.use(auth)     (ConcreteDecorator)
```

Each Express middleware calls `next()` to delegate to the inner handler — the same delegation chain as `MushRoomDecorator → SpicyDecorator → BasePizza`. Netflix uses Express-style middleware chains for API gateway request processing.

```javascript
const logger = (req, res, next) => { console.log(req.url); next(); };
const auth   = (req, res, next) => { if (!req.headers.auth) return res.sendStatus(401); next(); };
app.use(logger);
app.use(auth);
app.get("/", (req, res) => res.send("ok"));
```

---

#### 4. Python — `functools.wraps` / Decorators

```
BasePizza           original function  (ConcreteComponent)
MushRoomDecorator   @timer             (ConcreteDecorator)
SpicyDecorator      @retry             (ConcreteDecorator)
```

Python's `@decorator` syntax literally implements the Decorator pattern: each decorator wraps a callable and adds behavior (timing, retrying, logging) without modifying the wrapped function. Used pervasively in Django, Flask, and FastAPI.

```python
import time, functools
def timer(fn):
    @functools.wraps(fn)
    def wrapper(*args, **kwargs):
        start = time.time()
        result = fn(*args, **kwargs)
        print(f"{fn.__name__} took {time.time()-start:.3f}s")
        return result
    return wrapper

@timer
def calculate_cost(): return 15
calculate_cost()
```

---

#### 5. Java Servlet API — `HttpServletRequestWrapper`

```
BasePizza                 HttpServletRequest        (ConcreteComponent)
MushRoomDecorator         HttpServletRequestWrapper (abstract Decorator)
SpicyDecorator            MultiReadRequestWrapper   (ConcreteDecorator)
```

`HttpServletRequestWrapper` wraps any `HttpServletRequest` and overrides only the methods that need new behavior (e.g., re-reading the body stream), delegating all others. Used by Spring's `ContentCachingRequestWrapper` for request logging.

```java
public class MultiReadWrapper extends HttpServletRequestWrapper {
    private final byte[] body;
    public MultiReadWrapper(HttpServletRequest req) throws IOException {
        super(req);
        body = req.getInputStream().readAllBytes();
    }
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(body);
    }
}
```

---

### Section 6 — Trade-offs vs Other Design Patterns

#### Decorator vs Inheritance (Subclassing)

| | Decorator | Inheritance |
|---|---|---|
| Communication | Delegates to wrapped component at runtime | Inherits and overrides at compile time |
| Coupling | Coupled to the abstract component type only | Coupled to parent class implementation |
| When to use | Behaviors are combinatorial or determined at runtime | Single, stable extension of a well-known base |
| Risk | Deep chains are hard to debug; `instanceof` checks fail | Class explosion for N×M combinations; brittle base-class changes propagate down |

**Rule of thumb:** if you need more than 2–3 fixed combinations, use Decorator; if you have one well-understood extension point, subclass.

---

#### Decorator vs Composite

| | Decorator | Composite |
|---|---|---|
| Communication | Wraps exactly one component and adds behavior | Holds a list of components and aggregates results |
| Coupling | Linear chain — each node has one child | Tree — each node may have many children |
| When to use | Adding responsibilities to a single object | Building tree structures (menus, file systems, UI layouts) |
| Risk | Chain length proportional to feature count; hard to remove middle nodes | Uniform treatment of leaf vs composite can mask type errors |

**Rule of thumb:** use Decorator to add behavior to one object; use Composite to aggregate results from many objects.

---

#### Decorator vs Strategy

| | Decorator | Strategy |
|---|---|---|
| Communication | Wraps and extends the object from outside | Object delegates one algorithm to a swappable object inside |
| Coupling | Decorator and component share the same type | Context and strategy are distinct types |
| When to use | Stacking multiple independent behaviors on an object | Selecting one algorithm variant at runtime |
| Risk | Multiple decorators can interact unexpectedly (order-sensitive side effects) | Context must be aware of which strategy it holds |

**Rule of thumb:** use Decorator when behaviors stack; use Strategy when only one algorithm is active at a time.

---

#### Decorator vs Proxy

| | Decorator | Proxy |
|---|---|---|
| Communication | Adds new behavior to the component | Controls access to the component (lazy init, caching, access control) |
| Coupling | Extends the component interface contract | Maintains the same interface, client is unaware of the proxy |
| When to use | Business feature enrichment at runtime | Cross-cutting concerns: security, caching, remote delegation |
| Risk | Feature creep — too many stacked decorators obscure the base | Proxy hiding failures can make debugging opaque |

**Rule of thumb:** use Decorator when the caller needs to ask for enriched behavior; use Proxy when the caller should not know any indirection exists.

---

### Section 7 — Production Pitfalls

| Pitfall | Problem | Fix |
|---------|---------|-----|
| **Order-sensitive cost calculation** | `new MushRoomDecorator(new SpicyDecorator(base))` and `new SpicyDecorator(new MushRoomDecorator(base))` both return 25 here, but decorators that apply percentage modifiers or stateful transformations (e.g., discounts) produce different results depending on nesting order, with no compile-time warning. | Document the required wrapping order in the factory or Builder that constructs the chain; use a Builder pattern to enforce ordering invariants. |
| **Identity and equality opacity** | `mushroomDecorator2 instanceof MargaritaPizza` returns `false`; `mushroomDecorator2.equals(new MargaritaPizza())` is meaningless. Code that inspects runtime type breaks silently. | Never rely on `instanceof` or `equals` for decorated objects. Expose a `getComponent()` unwrap method or use a Visitor to interrogate the chain. |
| **Infinite delegation loop** | If a **ConcreteDecorator** accidentally stores a reference to itself as `basePizza`, `calculateCost()` recurses until `StackOverflowError`. Easy to introduce during refactoring. | Validate in the decorator constructor that `basePizza != this`. Add a unit test that constructs the chain and asserts a finite result. |
| **Excessive object allocation** | Every toping combination creates a new decorator instance on the heap. In a high-throughput system (thousands of pizza orders per second) GC pressure from short-lived decorator chains is measurable. | Cache frequently-used decorator chains via a Factory or pool; consider a Flyweight for stateless decorators. |
| **Verbose delegation boilerplate** | If `BasePizza` grows additional methods, every **ConcreteDecorator** must add delegation stubs or risk `AbstractMethodError`. With N decorators and M new methods, maintenance cost is N×M. | Keep the **Component** interface minimal. If the interface must be rich, use a default-method interface (Java 8+) or an abstract base that provides no-op delegation so decorators only override what they change. |
| **Debugging stack depth** | A chain of 5 decorators produces a call stack 5 levels deep for a single `calculateCost()` call. Stack traces become cryptic, especially when decorators are anonymous or lambda-constructed. | Give each decorator a meaningful `toString()` or `describe()` method that prints the full chain. Use structured logging in each `calculateCost()` (as `MushRoomDecorator` already does with `System.out.println`). |

---

### Section 8 — Summary

The Decorator pattern excels at composing behaviors on individual objects at runtime without subclassing, eliminating class explosion when features combine freely — as shown by wrapping `VegPizza` and `MargaritaPizza` with any combination of `MushRoomDecorator` and `SpicyDecorator`. In the wild you encounter it in JDK I/O streams, Spring Security filter chains, Express/Koa middleware, Python `@decorator` syntax, and any framework that layers cross-cutting concerns (logging, auth, compression) around a core handler.
