Document the design pattern in the folder: $ARGUMENTS

Steps:
1. Glob all `.java` files under `src/main/java/com/demo/lld/$ARGUMENTS/` and read every one of them.
2. Create the output file at `src/main/java/com/demo/lld/$ARGUMENTS/docs/$ARGUMENTS_docs.md`.
3. The document must contain exactly these sections in this order:

---

### Section 1 — What It Is
One crisp paragraph: define the pattern, its intent, and the core problem it solves.

### Section 2 — Code Structure (This Implementation)
- ASCII tree showing every class/interface in the scanned folder with its role (Subject / Observer / ConcreteX / Demo etc.) and the relationships between them.
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
- Title + technology stack (e.g., "Kafka / RabbitMQ", "Java,""Spring boot Framework")
- ASCII flow diagram showing how the pattern maps to that technology
- 1–2 sentence explanation of the mapping
- One real company or framework that uses it (Amazon, Spring Boot,  etc.)
- A short, runnable code snippet (Java,Spring boot) where it adds clarity

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

Writing rules:
- No filler phrases ("In conclusion", "It is worth noting", "As we can see").
- Every claim must map to something in the scanned code OR to a named real technology.
- Code snippets: syntactically correct, 15 lines max each, no placeholder comments like `// ... rest of code`.
- Use `**bold**` only for pattern-role labels (Subject, Observer, ConcreteX) and pitfall names.
