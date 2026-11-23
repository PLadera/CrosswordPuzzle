# Crossword Puzzle

A Java Swing crossword game with a MySQL database (JDBC).
Made by Paul and Karl for CS0070 Java Programming.

Pick a premade puzzle (Easy, Medium or Hard), solve it against the clock,
save your score, and see the leaderboard.

## What you need

| Tool | Version |
|---|---|
| Java JDK | 24 (Oracle OpenJDK) |
| MySQL Server | 8.4 LTS |
| MySQL Workbench | any recent version |
| IntelliJ IDEA | Community Edition |

The MySQL driver (Connector/J) is already in the `lib` folder, so there is nothing to download for it.

## Setup, step by step

### 1. Get the project
Clone it from GitHub, then open the folder in IntelliJ (**File -> Open**).

In IntelliJ, check **File -> Project Structure -> Project** and set the **SDK** to JDK 24.

### 2. Create the database
1. Open MySQL Workbench and connect to your local server.
2. Run this once:
   ```sql
   CREATE DATABASE IF NOT EXISTS crossword_db;
   ```
3. Open `schema.sql` (in the project folder) in Workbench and run it. This creates the tables.
4. Open `seed_data.sql` and run it. This adds the 3 puzzles.
5. Check it worked:
   ```sql
   USE crossword_db;
   SELECT title, difficulty FROM puzzle;
   ```
   You should see Animal Kingdom, Pinoy Pride and Human Body.

### 3. Add your database password
1. In the project folder, copy `db.properties.example` and name the copy **`db.properties`**.
2. Open `db.properties` and replace `YOUR_PASSWORD_HERE` with your MySQL root password:
   ```
   db.url=jdbc:mysql://localhost:3306/crossword_db
   db.user=root
   db.password=YOUR_PASSWORD_HERE
   ```
`db.properties` is private and is never uploaded to GitHub.

### 4. Check the driver
In IntelliJ, open **File -> Project Structure -> Libraries**.
`mysql-connector-j-26.10.0.jar` from the `lib` folder should be listed.
If it is missing: press **+ -> Java**, pick the jar inside `lib`, and tick the module.

### 5. Run it
Open `src/crossword/app/Main.java` and click the green run button next to `main`.

The window should show the 3 puzzles.

## How to play
1. Pick a puzzle and click **Play**.
2. Click a white cell and type a letter. Arrow keys move between cells.
3. **Check** marks wrong letters in red. **Restart** clears the grid.
4. When every letter is right, the timer stops. Type your name and click **Save score**.
5. Click **Leaderboard** on the puzzle list to see the best times. You can delete a score there.

## Troubleshooting

| Message | What to do |
|---|---|
| "Cannot connect to the database. Is MySQL running?" | Start the MySQL service, then try again. |
| "Cannot read db.properties..." | You skipped step 3. Copy `db.properties.example` to `db.properties`. |
| Red errors about `com.mysql` | Redo step 4. |
| "No database selected" in Workbench | Run `USE crossword_db;` first. |

## Project layout

```
src/crossword/
  model/      plain data classes (Puzzle, Entry, Attempt)
  dao/        database code (JDBC)
  service/    rules (GameSession, PuzzleService, AttemptService)
  exception/  our own exceptions
  ui/         Swing screens
  app/        Main (starts the program) and test runners
```

The flow is: **screen -> service -> DAO -> MySQL**.
