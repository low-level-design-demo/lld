# Tic-Tac-Toe Design

## What It Is

This implementation models a turn-based two-player game using the **Template Method** and **Inheritance-based Polymorphism** patterns. The core problem it solves is representing multiple interchangeable game pieces (X and O) through a common abstraction, while keeping the game loop, board state, and win-detection logic cleanly separated into distinct responsibilities. `TicTacGame` orchestrates the game lifecycle — player turns, input, placement, and win-checking — without ever caring about which concrete piece type is being played.

---

## Code Structure (This Implementation)

### Class Hierarchy and Roles

```
PieceType (enum)
  └── X, O                          ── Piece identity values

PlayingPiece (abstract base)
  ├── PlayingPieceX                 ── ConcreteProduct: hard-wires PieceType.X
  └── PlayingPieceO                 ── ConcreteProduct: hard-wires PieceType.O

Player                              ── Value object: name + assigned PlayingPiece

Board                               ── Game state: PlayingPiece[size][size] grid
  ├── addPiece(x, y, piece)         ── Mutates grid; returns false if cell occupied
  ├── getFreeCellCount()            ── Tie detection
  ├── printBoard()                  ── Console rendering
  └── getBoard() / getSize()        ── Accessors for win-check logic

TicTacGame                          ── Game controller: owns Board + Deque<Player>
  ├── initializeBoard(size)         ── Wires players and board
  ├── startGame()                   ── Main game loop; returns winner name or "tie"
  └── isThereWinner(x, y, type)     ── O(n) win check: row, col, diag, anti-diag

TicTacDemo                          ── Entry point: constructs TicTacGame, prints result
```

### Runtime Call Flow

```
TicTacDemo.main()
  │
  └── new TicTacGame(3)
        └── initializeBoard(3)
              ├── new Player("Player 1", new PlayingPieceX())
              ├── new Player("Player 2", new PlayingPieceO())
              └── new Board(3)

  └── game.startGame()
        │
        └── [loop while !isGameOver]
              │
              ├── dq.removeFirst()              → currentPlayer
              ├── board.printBoard()            → prints current grid state
              ├── Scanner.nextLine()            → reads "x,y" from stdin
              ├── board.addPiece(x, y, piece)
              │     ├── if cell occupied → return false
              │     │     └── dq.addFirst(currentPlayer)  [retry same player]
              │     └── grid[x][y] = piece; return true
              ├── board.printBoard()            → prints updated grid state
              ├── dq.addLast(currentPlayer)     → re-queue at end
              ├── isThereWinner(x, y, pieceType)
              │     ├── scan row x
              │     ├── scan column y
              │     ├── scan main diagonal  (if x == y)
              │     └── scan anti-diagonal  (if x+y == size-1)
              │           └── if all match → return true → return winner name
              └── board.getFreeCellCount() == 0 → isGameOver = true → return "tie"

  └── prints "Winner is: <name>" or "it's a tie!"
```

---

## SOLID Principles Applied

| Principle | How |
|---|---|
| **S — Single Responsibility** | `Board` owns only grid state and rendering. `TicTacGame` owns only turn management and game flow. `Player` owns only identity and piece association. Each has one reason to change. |
| **O — Open/Closed** | Adding a new piece type (e.g., `PlayingPieceTriangle`) requires only a new subclass of `PlayingPiece` and a new `PieceType` enum value — `Board`, `TicTacGame`, and `Player` are untouched. |
| **L — Liskov Substitution** | `PlayingPieceX` and `PlayingPieceO` are fully interchangeable wherever `PlayingPiece` is expected. `board.addPiece()` and `isThereWinner()` receive `PlayingPiece` and `PieceType` respectively — no instanceof checks. |
| **I — Interface Segregation** | Not strongly demonstrated — no interfaces are declared. `PlayingPiece` is a concrete base class rather than an interface, which is a design gap but doesn't violate ISP (no fat interfaces exist). |
| **D — Dependency Inversion** | Partially applied. `TicTacGame` depends on `PlayingPiece` (the abstraction) rather than `PlayingPieceX`/`PlayingPieceO` directly. However, `initializeBoard` hard-wires `new PlayingPieceX()` and `new PlayingPieceO()` — injecting players via constructor would make this fully DIP-compliant. |

---

## When to Use This Pattern

### Use when
- You have a fixed set of entity variants (X, O; Red piece, Blue piece) that share a common interface but differ only in a type value.
- A central game loop / workflow must treat all variants uniformly without branching on type.
- The board (or any grid/matrix aggregate) is the single source of truth for state — all reads and writes go through it.
- Turn ordering must be dynamic (players skip turns, get retried, or are added/removed at runtime) — a `Deque` handles this cleanly.
- Win conditions are axis-aligned (row, column, diagonal) — checkable in O(n) per move rather than scanning the full board.

### Avoid when
- The game has more than 2 players with asymmetric rules — a `Deque` + single-winner check becomes awkward; use a proper game-state machine instead.
- Piece placement has complex validity rules beyond "cell is empty" — the logic inside `addPiece` would balloon; extract a `MoveValidator`.
- The board needs to support arbitrary shapes (hexagonal grid, toroidal wrap) — the flat `PlayingPiece[][]` array and diagonal checks by index arithmetic won't generalise.
- Concurrent players (online multiplayer) — the `Scanner` + synchronous loop is single-threaded; an event-driven model is required.

---

## Real Production Implementations

### 1. Chess Engine Turn Management — Java, LibGDX / custom game loop

```java
Deque<ChessPlayer> turnQueue = new LinkedList<>();
turnQueue.add(new ChessPlayer("White", new KingPiece(Color.WHITE)));
turnQueue.add(new ChessPlayer("Black", new KingPiece(Color.BLACK)));

ChessPlayer current = turnQueue.removeFirst();
boolean moved = board.applyMove(current.selectMove());
if (moved) turnQueue.addLast(current);
```

The `Deque`-based turn queue from `TicTacGame` maps directly to chess's alternating-turn model. Stockfish and open-source Java engines like Chesspresso use equivalent player-queue structures.
**Real usage:** Chesspresso (Java chess library)

---

### 2. Connect Four / Gomoku Generalisation — Java, standard collections

```java
public class ConnectFourBoard extends Board {
    @Override
    public boolean addPiece(int x, int y, PlayingPiece piece) {
        int bottom = lowestFreeRow(y);
        if (bottom < 0) return false;
        return super.addPiece(bottom, y, piece);
    }
}
```

`Board` is subclassed to override piece placement gravity — the rest of `TicTacGame` (turn loop, win check, tie detection) is reused unchanged. This is the Open/Closed principle in action.
**Real usage:** LeetCode's board-game design problems; game tutorial frameworks

---

### 3. Online Multiplayer Session — Spring Boot, WebSocket

```java
@MessageMapping("/move")
public void handleMove(MovePayload move, SimpMessageHeaderAccessor header) {
    String sessionId = header.getSessionId();
    GameSession session = sessionRegistry.get(move.getGameId());
    boolean placed = session.getBoard().addPiece(move.getX(), move.getY(),
                         session.currentPiece());
    if (placed) {
        simpMessagingTemplate.convertAndSend("/topic/game/" + move.getGameId(),
                                              session.getBoardState());
    }
}
```

`Board.addPiece()` is called inside a WebSocket message handler. The `Deque<Player>` becomes a server-side session object keyed by game ID. Spring's `SimpMessagingTemplate` broadcasts `printBoard()`-equivalent state to all subscribers.
**Real usage:** Spring Boot WebSocket game tutorials; Lichess (Scala equivalent)

---

### 4. Android Turn-Based Game — Java / Kotlin, Android SDK

```java
public class TicTacActivity extends AppCompatActivity {
    private TicTacGame game = new TicTacGame(3);

    public void onCellClick(View cell) {
        int x = (int) cell.getTag(R.id.row);
        int y = (int) cell.getTag(R.id.col);
        boolean placed = game.getBoard().addPiece(x, y, game.currentPiece());
        if (placed) {
            updateGrid();
            String result = game.checkEndCondition(x, y);
            if (result != null) showResult(result);
        }
    }
}
```

The `Board` and piece model map directly to Android UI: each `View` cell corresponds to `board[x][y]`. `addPiece` drives the click handler; `isThereWinner` drives the end-of-turn check.
**Real usage:** Google Play Store casual game apps; Android game dev courses

---

### 5. Unit-Testable Win Detection — JUnit 5, Java

```java
@Test
void columnWinDetected() {
    Board board = new Board(3);
    PlayingPiece x = new PlayingPieceX();
    board.addPiece(0, 1, x);
    board.addPiece(1, 1, x);
    board.addPiece(2, 1, x);
    TicTacGame game = new TicTacGame(3);
    assertTrue(game.isThereWinner(2, 1, PieceType.X));
}
```

The O(n) win-check design (row, col, diagonal by index arithmetic) is independently testable because it takes only `(x, y, pieceType)` — no Scanner, no Deque involved. This separation makes every win condition a one-line assertion.
**Real usage:** Standard LLD interview test suites; game engine CI pipelines

---

## Trade-offs vs Other Design Patterns

### TicTacGame (Turn Loop) vs State Machine Pattern

| | TicTacGame loop | State Machine |
|---|---|---|
| **Communication** | Single `while` loop; player is re-queued or advanced | Explicit states (WAITING_INPUT, PIECE_PLACED, GAME_OVER); transitions are events |
| **Coupling** | Game logic, input, and state check are all inside `startGame()` | Each state is isolated; transitions are declarative |
| **When to use** | 2-player, simple alternating turns, no mid-game state branching | Games with complex states (pause, reconnect, spectator mode, timer expiry) |
| **Risk** | `startGame()` grows as rules get added | State explosion if transitions are not carefully bounded |

**Rule of thumb:** Use the loop for games with 2 states (playing / over); use a state machine when a third state (paused, waiting for opponent, bonus round) appears.

---

### PlayingPiece Inheritance vs Strategy Pattern

| | Inheritance (`PlayingPieceX` / `PlayingPieceO`) | Strategy |
|---|---|---|
| **Communication** | Subtype carries fixed type value set at construction | Context holds a swappable strategy reference; behaviour is injected |
| **Coupling** | Subclass is tightly coupled to its `PieceType` value | Context is decoupled; strategy can change at runtime |
| **When to use** | Piece identity never changes; only one behaviour difference (type value) | Piece behaviour varies at runtime (power-ups, handicap pieces, AI vs human) |
| **Risk** | Adding new piece-specific behaviour (e.g., special moves) causes subclass explosion | Over-engineering for simple type tagging where an enum field suffices |

**Rule of thumb:** If the only difference between subtypes is a constant field value, use a field — not a subclass.

---

### Board (Direct Array Access) vs Repository Pattern

| | Board (array) | Repository |
|---|---|---|
| **Communication** | `board[x][y]` accessed directly by `TicTacGame` via `getBoard()` | All reads/writes go through `BoardRepository.save()` / `find()` |
| **Coupling** | `TicTacGame` is aware of the internal array structure | `TicTacGame` is decoupled from storage — could be in-memory, DB, or remote |
| **When to use** | In-process, single-session game; no persistence needed | Online game with save/resume, leaderboards, or distributed state |
| **Risk** | `getBoard()` exposes the raw array — callers can mutate state outside `addPiece()` | Adds indirection and serialisation cost for a simple in-memory grid |

**Rule of thumb:** Expose `addPiece()` / `getFreeCellCount()` as the public API; make `getBoard()` package-private or return a defensive copy to prevent external mutation.

---

### Deque Turn Queue vs Iterator Pattern

| | Deque\<Player\> | Iterator |
|---|---|---|
| **Communication** | `removeFirst()` + `addLast()` / `addFirst()` for retry | `hasNext()` + `next()` cycles through players |
| **Coupling** | Queue order is mutated in-place; retry is a natural `addFirst` | Iterator is stateless about retries; retry logic sits outside |
| **When to use** | Turn order can change (retry, skip, insert extra turn) | Turn order is fixed and never mutated |
| **Risk** | Unbounded retry loop if board is full and addPiece always fails | Can't represent retry or skip without wrapping the iterator |

**Rule of thumb:** Use `Deque` when turn order is dynamic; use an index-based cycle (`i % players.size()`) when turn order is strictly fixed.

---

## Production Pitfalls

| Pitfall | Problem | Fix |
|---|---|---|
| **`getBoard()` exposes mutable array** | Any caller can write `board.getBoard()[0][0] = piece` directly, bypassing `addPiece()` validation and corrupting game state | Return a deep copy from `getBoard()`, or make it package-private and expose only `addPiece()` / `findPiece()` as public API |
| **Scanner created inside the loop** | `new Scanner(System.in)` is instantiated on every iteration — the previous instance is never closed, leaking a wrapper around the same `InputStream` | Create one `Scanner` outside the loop (as a field or constructor parameter) and reuse it |
| **No input validation** | `Integer.valueOf(values[0])` throws `NumberFormatException` on non-numeric input; `values[1]` throws `ArrayIndexOutOfBoundsException` if the user omits the comma | Wrap input parsing in a try-catch; re-prompt on invalid format before calling `addPiece` |
| **Hard-wired player construction** | `initializeBoard()` hard-codes `new PlayingPieceX()` and `new PlayingPieceO()` — impossible to inject custom players, AI players, or more than 2 players without modifying the method | Accept `List<Player>` as a constructor parameter; remove player construction from `initializeBoard` |
| **Win check requires `getBoard()` access** | `isThereWinner` calls `board.getBoard()` to access the raw array — coupling game logic to Board's internal representation | Move `isThereWinner` logic into `Board` as `checkWin(int x, int y, PieceType type)` — Board already owns the grid and knows its size |
| **Tie declared only when `getFreeCellCount() == 0`** | If the last move is a winning move, `getFreeCellCount()` reaches 0 but the winner check runs first — this is correct. However, if `isThereWinner` is ever restructured to run after the free-cell check, a win on the last cell would be declared a tie | Keep winner check strictly before free-cell check; add a comment on the ordering invariant |

---

## Summary

The Tic-Tac-Toe design excels at cleanly separating the board (state), the piece hierarchy (identity), and the game controller (flow) into three non-overlapping responsibilities. In production, this same structure appears in every turn-based game engine — from Android casual games to Spring Boot WebSocket game servers — wherever a loop, a queue of participants, and an end-condition check are the core primitives.
