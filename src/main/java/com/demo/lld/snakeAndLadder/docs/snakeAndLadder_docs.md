# Snake and Ladder — Design Pattern Documentation

---

## Section 1 — What It Is

Snake and Ladder is a turn-based board game modelled using the **State + Simulation** design approach. The core problem it solves is coordinating multiple independent entities (players, board, dice) through a shared game loop without any entity directly owning or controlling another. The `Game` class acts as an orchestrator: it holds a `Board`, a queue of `Player`s, and a list of `Dice`, advancing state on each turn without coupling the board layout to the player or the dice to the movement logic. The `Jump` abstraction unifies snakes and ladders into a single concept — a directed teleport between two cells — so the board does not need to distinguish between them during placement; only the direction of the jump (start < end vs start > end) encodes the type at runtime.

---

## Section 2 — Code Structure (This Implementation)

### Class Roles and Relationships

```
snakeAndLadder/
│
├── SnakeAndLadderDemo        [Demo]         — entry point, creates Game and calls start()
│
├── Game                      [Orchestrator] — owns Board, Deque<Player>, List<Dice>
│   │                                          drives the game loop, declares winner
│   ├── uses ──► Board
│   ├── uses ──► Dice
│   └── uses ──► Player
│
├── Board                     [Aggregate]    — owns Cell[][], places Jumps randomly
│   └── contains ──► Cell[][]
│
├── Cell                      [Value Object] — holds an optional Jump (null = plain cell)
│   └── contains ──► Jump (nullable)
│
├── Jump                      [Value Object] — (start, end) pair; start < end → ladder,
│                                              start > end → snake
│
├── Player                    [Entity]       — holds id and currentPosition
│
└── Dice                      [Service]      — stateless roller, nextInt(1,7)
```

### Runtime Call Flow

```
SnakeAndLadderDemo.main()
  └── new Game()
        ├── new Board(10, 5, 5)
        │     ├── initializeCells()        — fills Cell[10][10]
        │     └── putSnakeAndLadder()      — randomly places 5 snakes + 5 ladders
        └── initializePlayers()            — p1, p2 added to Deque
        └── initializeDices()              — single Dice added to List

  └── game.start()
        └── [loop until winner]
              ├── player = players.poll()  — round-robin via Deque
              ├── players.offer(player)
              ├── dice.throwDice()         — ThreadLocalRandom [1,6]
              ├── totalSteps = position + dice
              ├── overshoot check          — skip turn if totalSteps > 99
              ├── updateStepsIfSnakeOrLadder(totalSteps)
              │     ├── row = totalSteps / 10
              │     ├── col = totalSteps % 10
              │     ├── cell = board.getCells()[row][col]
              │     └── jump != null → return jump.getEnd()
              ├── player.setCurrentPosition(totalSteps)
              └── totalSteps == 99 → return winner id
```

---

## Section 3 — SOLID Principles Applied

| Principle | How |
|-----------|-----|
| **S**ingle Responsibility | `Board` only places and exposes cells. `Dice` only rolls. `Game` only orchestrates turns. `Jump` only stores a directed teleport. No class does more than one job. |
| **O**pen/Closed | Adding a new jump type (e.g., a wormhole that cycles) requires only a new `Jump` subclass and a check in `updateStepsIfSnakeOrLadder` — existing classes are not modified. |
| **L**iskov Substitution | Not strongly demonstrated — no inheritance hierarchy exists. All classes are concrete. |
| **I**nterface Segregation | Not demonstrated — no interfaces are defined. Extracting `Rollable` for `Dice` and `Jumpable` for `Cell` would improve this. |
| **D**ependency Inversion | Not demonstrated — `Game` directly instantiates `Board`, `Dice`, and `Player` via `new`. Injecting these via constructor would decouple `Game` from concrete implementations. |

---

## Section 4 — When to Use This Pattern

### Use when
- The system has multiple independent entities (players, board, dice) that must be coordinated through a central loop without coupling to each other.
- Game state progresses in discrete, deterministic turns — each turn is fully resolved before the next begins.
- You need to add new board elements (power-ups, portals, traps) without modifying the player or game loop logic — only `Cell`/`Jump` need to change.
- The number of participants (players, dice) is configurable at construction time.
- You want to replay or test a game deterministically by seeding the random source.

### Avoid when
- The game has real-time or concurrent turns — the single-threaded `Deque` poll/offer loop is not thread-safe.
- Board size or entity counts are enormous — random placement with rejection (`continue` on conflict) degrades to O(∞) in the worst case with a densely populated board.
- You need the game to be serialisable/pauseable — there is no game-state snapshot mechanism; all state is in live objects.
- A simple array and a loop would suffice — the multi-class structure is overkill for a one-player solitaire variant.

---

## Section 5 — Real Production Implementations

### 1. Turn-Based Multiplayer Game Server — Java / Netty / Redis

```
Client A ──► GameServer.handleTurn(playerId, diceRoll)
                  │
                  ├── GameSession (≈ Game)      — orchestrates turn order
                  ├── BoardState  (≈ Board)     — cell map in Redis hash
                  ├── PlayerState (≈ Player)    — position stored per session
                  └── EventLog                  — broadcasts result to all clients
```

`GameSession` mirrors `Game`: it polls a player queue, resolves the move against `BoardState`, and publishes the result. Redis stores `Cell` equivalents as hash fields keyed by position. Used by **Zynga** (Words With Friends turn engine).

```java
public String processTurn(String sessionId, int roll) {
    GameSession session = sessions.get(sessionId);
    Player current = session.nextPlayer();
    int next = current.getPosition() + roll;
    next = boardState.resolveJump(next);
    current.setPosition(next);
    return next == WINNING_CELL ? current.getId() : null;
}
```

---

### 2. Workflow Engine — Spring Boot / Camunda BPM

```
WorkflowEngine (≈ Game)
  └── ProcessInstance (≈ Player)
        └── FlowNode (≈ Cell)
              └── Gateway / ServiceTask (≈ Jump)
                    └── redirects token to another FlowNode
```

Each `FlowNode` maps to a `Cell`: plain tasks pass through, gateways (snakes/ladders) redirect the process token to a different node. **Camunda** drives enterprise workflows at ING Bank and Vodafone.

```java
@Component
public class GatewayHandler implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        String result = (String) execution.getVariable("approvalResult");
        execution.setVariable("nextNode",
            result.equals("APPROVED") ? "endEvent" : "revisionTask");
    }
}
```

---

### 3. CI/CD Pipeline — GitHub Actions / Jenkins

```
PipelineRunner (≈ Game)
  └── Stage[] (≈ Board / Cell[])
        └── each Stage has a successor or a redirect (≈ Jump)
              ├── test failure → redirect to "notify-team" stage  (snake)
              └── build success → jump to "deploy-prod" stage     (ladder)
```

Stages are cells; conditional redirects (on failure/success) are jumps. **GitHub Actions** implements this as `needs:` + `if:` expressions per job, directing flow forward or to a recovery job.

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - run: ./mvnw test
  deploy:
    needs: test
    if: success()
    steps:
      - run: ./mvnw spring-boot:run
  notify:
    needs: test
    if: failure()
    steps:
      - run: curl -X POST $SLACK_WEBHOOK -d '{"text":"Build failed"}'
```

---

### 4. Board Game AI (Monte Carlo Tree Search) — Java / DeepMind AlphaZero-style

```
Simulator (≈ Game)
  └── rollout()
        ├── clones BoardState (≈ Board)
        ├── simulates N turns with random Dice rolls
        └── returns win/loss signal to MCTS node
```

The `Game.start()` loop is the rollout function in MCTS: the simulator clones board+player state, plays to completion, and returns the outcome. **DeepMind** uses equivalent rollout loops in AlphaZero board-game engines.

```java
public double rollout(BoardState state, Player current) {
    BoardState sim = state.deepCopy();
    Player p = current.deepCopy();
    while (true) {
        int roll = dice.throwDice();
        int next = sim.resolveJump(p.getPosition() + roll);
        p.setPosition(next);
        if (next == WINNING_CELL) return 1.0;
    }
}
```

---

### 5. Event-Driven State Machine — Spring Statemachine / AWS Step Functions

```
StateMachine (≈ Game)
  └── State (≈ Cell)
        └── Transition (≈ Jump)
              ├── guard condition   — is there a snake/ladder?
              └── action            — update player position
```

`State` maps to `Cell`, `Transition` maps to `Jump`. Guards check whether a jump exists; actions apply `jump.getEnd()`. **AWS Step Functions** models this as `Choice` states with `Next` redirects, used by Amazon Order Pipeline.

```java
@Configuration
public class GameStateMachine extends StateMachineConfigurerAdapter<String, String> {
    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> t) throws Exception {
        t.withExternal()
            .source("CELL_50").target("CELL_5").event("SNAKE_HIT")
            .and()
            .withExternal()
            .source("CELL_20").target("CELL_80").event("LADDER_CLIMBED");
    }
}
```

---

## Section 6 — Trade-offs vs Other Design Patterns

### vs. State Pattern

| Dimension | Snake & Ladder (Orchestrator) | State Pattern |
|-----------|-------------------------------|---------------|
| Communication | `Game` calls methods on entities directly | Context delegates to a State object which handles the call |
| Coupling | `Game` knows all entity types | Context knows only the `State` interface |
| When to use | Turn logic is simple and centralized | Entity behaviour changes significantly based on internal state |
| Risk | `Game` grows large as rules accumulate | State explosion — one class per state variant |

**Rule of thumb:** if the game loop has 5+ conditional branches on player/board state, switch to the State pattern.

---

### vs. Command Pattern

| Dimension | Snake & Ladder (Orchestrator) | Command Pattern |
|-----------|-------------------------------|-----------------|
| Communication | `Game` executes moves inline | Each move is an object — queued, logged, undone |
| Coupling | Move logic lives in `Game` | Move logic lives in `Command` implementations |
| When to use | No undo/replay requirement | Undo, redo, audit log, or networked move sync needed |
| Risk | Cannot replay or undo a turn | Command object proliferation |

**Rule of thumb:** wrap dice rolls and position updates in `Command` objects the moment you need replay or undo.

---

### vs. Strategy Pattern

| Dimension | Snake & Ladder (Orchestrator) | Strategy Pattern |
|-----------|-------------------------------|------------------|
| Communication | Jump resolution is hardcoded in `updateStepsIfSnakeOrLadder` | A `JumpStrategy` interface is injected into `Cell` or `Board` |
| Coupling | `Game` is coupled to jump direction logic | `Game` is decoupled; any strategy can be swapped |
| When to use | Jump types are fixed (snake/ladder only) | Multiple teleport behaviours needed (wormholes, boosts) |
| Risk | Adding a new jump type requires modifying `Game` | Over-engineering for two jump types |

**Rule of thumb:** extract a `JumpStrategy` only when a third jump type is confirmed, not in anticipation.

---

### vs. Observer Pattern

| Dimension | Snake & Ladder (Orchestrator) | Observer Pattern |
|-----------|-------------------------------|-----------------|
| Communication | `Game` pushes log messages directly via `Logger` | `Game` fires events; listeners (UI, analytics, AI) react |
| Coupling | `Game` is coupled to `Logger` | `Game` is decoupled from all consumers |
| When to use | Single output channel (logs) | Multiple consumers need turn events (UI, replay, stats) |
| Risk | Adding a leaderboard requires modifying `Game` | Listener ordering and memory-leak risk |

**Rule of thumb:** add Observer when a second consumer (UI renderer, analytics sink) needs turn events.

---

## Section 7 — Production Pitfalls

| Pitfall | Problem | Fix |
|---------|---------|-----|
| **Infinite placement loop** | `putSnakeAndLadder()` retries forever if `snakeCount + ladderCount > available cells`. On a 10×10 board with 98 jumps requested, the `while` loop never exits. | Cap total jumps at `(size*size - 2) / 2` and throw `IllegalArgumentException` at construction if exceeded. |
| **Non-thread-safe game loop** | `Deque<Player>` is a `LinkedList` — `poll()` and `offer()` are not synchronized. A concurrent multiplayer server calling `start()` from multiple threads corrupts turn order. | Use `ArrayDeque` behind a lock or replace `Game.start()` with a single-threaded executor per game session. |
| **Jump overwrites another jump** | The `continue` dedup guard in `putSnakeAndLadder()` only checks the snake's own `start` cell. A ladder could overwrite a snake's `end` cell, corrupting a player teleported there. | Track all occupied positions (both `start` and `end`) in a `Set<Integer>` and reject any new jump whose start or end conflicts. |
| **Dice upper-bound off-by-one** | `ThreadLocalRandom.nextInt(1, 6)` rolls [1,5]. Changing to `nextInt(1, 7)` fixes it, but if `Dice.max` is ever wired to `nextInt(1, max)` without adjusting, the bug silently reappears. | Derive the bound from `max + 1` inside `throwDice()`: `nextInt(min, max + 1)`. |
| **Exact-win rule not enforced on snake** | A player landing on cell 99 via a ladder's `end` correctly wins. But if cell 99 itself holds a snake, the player is sent backwards — the win check runs after `updateStepsIfSnakeOrLadder`, so this is correct. However, placing a snake with `start == 99` during board setup is not prevented. | Exclude cell `size*size - 1` from valid snake `start` positions in `putSnakeAndLadder()`. |
| **No position bounds check** | If `totalSteps` somehow exceeds `size*size - 1` before `updateStepsIfSnakeOrLadder` is called (e.g., multi-dice variant), `board.getCells()[row][col]` throws `ArrayIndexOutOfBoundsException`. | The overshoot guard in `start()` must run before any array access, which it currently does — but this must be preserved if the loop is refactored. |

---

## Section 8 — Summary

Snake and Ladder demonstrates how an **Orchestrator** class (`Game`) can coordinate multiple independent value objects (`Board`, `Player`, `Dice`, `Cell`, `Jump`) through a clean turn loop without any entity owning another — a pattern that appears directly in workflow engines (Camunda), CI/CD pipelines (GitHub Actions), and multiplayer game servers (Zynga). The `Jump` abstraction — unifying snakes and ladders into a single directed teleport resolved by cell lookup — is the pattern's sharpest insight, and it recurs in production wherever a state machine node must redirect flow to a non-adjacent state.
