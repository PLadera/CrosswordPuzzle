package crossword.model;

import java.time.LocalDateTime;

public class Attempt {
    private int id;
    private int puzzleId;
    private String playerName;
    private int solveSeconds;
    private LocalDateTime finishedAt;

    public Attempt(int id, int puzzleId, String playerName,
                   int solveSeconds, LocalDateTime finishedAt) {
        this.id = id;
        this.puzzleId = puzzleId;
        this.playerName = playerName;
        this.solveSeconds = solveSeconds;
        this.finishedAt = finishedAt;
    }

    public int getId() { return id; }
    public int getPuzzleId() { return puzzleId; }
    public String getPlayerName() { return playerName; }
    public int getSolveSeconds() { return solveSeconds; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
}