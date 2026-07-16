# Reusable Prompt: Generate Design Pattern Documentation

## How to Use

Copy the prompt below, replace `{{PATTERN_NAME}}` with the design pattern name (e.g., `Strategy`, `Factory`, `Decorator`), and send it to Claude Code.

---

## The Prompt

```
Document the {{PATTERN_NAME}} design pattern.

1. Locate all source files under `src/main/java/com/demo/lld/{{patternFolderName}}/` and read every `.java` file in that folder.

2. Create the output file at:
   `src/main/java/com/demo/lld/{{patternFolderName}}/docs/{{patternFolderName}}_docs.md`

3. The document must contain exactly these sections in this order:

---

### Section 1 — What It Is
One crisp paragraph: define the pattern, its intent, and the core problem it solves.

### Section 2 — Code Structure (This Implementation)
- ASCII tree showing every class/interface in the scanned folder, its role (Subject / Observer / ConcreteX / Demo etc.), and the relationship between them.
- A second ASCII tree showing the runtime call flow from the Demo class through to the final output.

### Section 3 — SOLID Principles Applied
A markdown table with columns: Principle | How.
Cover all five (S, O, L, I, D). If a principle is not strongly demonstrated, say so briefly rather than inventing a connection.

### Section 4 — When to Use This Pattern
Two subsections:
- "Use when" — bullet list of concrete triggering conditions (not generic advice).
- "Avoid when" — bullet list of anti-conditions where a simpler approach wins.

### Section 5 — Real Production Implementations
Exactly 5 real-world examples. For each:
- Title + technology stack (e.g., "Kafka / RabbitMQ", "Spring Framework", "React / RxJS")
- ASCII flow diagram showing how the pattern maps to that technology
- 1–2 sentence explanation of the mapping
- One real company or framework that uses it (Amazon, Spring Boot, Angular, etc.)
- A short, runnable code snippet (Java, TypeScript, or relevant language) where it adds clarity

### Section 6 — Trade-offs vs Other Design Patterns
Compare against exactly 3–4 related patterns. For each comparison:
- A markdown table with rows: Communication | Coupling | When to use | Risk
- A one-line "Rule of thumb" to decide between them

### Section 7 — Production Pitfalls
A markdown table: Pitfall | Problem | Fix
Cover at least 5 pitfalls specific to this pattern (thread safety, memory leaks, ordering, performance, etc.)

### Section 8 — Summary
Two sentences max. What the pattern excels at and where you will encounter it in the wild.

---

4. Writing rules:
   - No filler phrases ("In conclusion", "It is worth noting", "As we can see").
   - Every claim must map to something in the scanned code OR to a named real technology.
   - Code snippets: syntactically correct, ≤ 15 lines each, no placeholder comments like `// ... rest of code`.
   - Tables must have consistent column widths (pad with spaces if needed).
   - Use `**bold**` only for pattern-role labels (Subject, Observer, ConcreteX) and pitfall names.
```

---

## Quick Reference — Pattern to Folder Name Mapping

| Pattern Name | Folder to scan | Output file |
|---|---|---|
| Observer | `observer` | `observer/docs/observer_docs.md` |
| Strategy | `strategy` | `strategy/docs/strategy_docs.md` |
| Factory Method | `factory` | `factory/docs/factory_docs.md` |
| Abstract Factory | `abstractfactory` | `abstractfactory/docs/abstractfactory_docs.md` |
| Decorator | `decorator` | `decorator/docs/decorator_docs.md` |
| Singleton | `singleton` | `singleton/docs/singleton_docs.md` |
| Command | `command` | `command/docs/command_docs.md` |
| Builder | `builder` | `builder/docs/builder_docs.md` |
| Adapter | `adapter` | `adapter/docs/adapter_docs.md` |
| Facade | `facade` | `facade/docs/facade_docs.md` |
| Proxy | `proxy` | `proxy/docs/proxy_docs.md` |
| Template Method | `templatemethod` | `templatemethod/docs/templatemethod_docs.md` |
| Chain of Responsibility | `chainofresponsibility` | `chainofresponsibility/docs/chainofresponsibility_docs.md` |
| Iterator | `iterator` | `iterator/docs/iterator_docs.md` |
| State | `state` | `state/docs/state_docs.md` |

> If your folder name differs, just adjust the path — the prompt structure stays the same.

---

## Example Invocation

To document the **Strategy** pattern, send:

```
Document the Strategy design pattern.

1. Locate all source files under `src/main/java/com/demo/lld/strategy/` and read every `.java` file in that folder.

2. Create the output file at:
   `src/main/java/com/demo/lld/strategy/docs/strategy_docs.md`

3. The document must contain exactly these sections in this order:
[... paste Section 3 onward from the prompt above ...]
```
