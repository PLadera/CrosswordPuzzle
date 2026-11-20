package crossword.model;

public class Entry {
    private int id;
    private int puzzleId;
    private String word;
    private String clue;
    private int startRow;
    private int startCol;
    private Direction direction;

    public Entry(int id, int puzzleId, String word, String clue,
                 int startRow, int startCol, Direction direction) {
        this.id = id;
        this.puzzleId = puzzleId;
        this.word = word;
        this.clue = clue;
        this.startRow = startRow;
        this.startCol = startCol;
        this.direction = direction;
    }

    public int getId() { return id; }
    public int getPuzzleId() { return puzzleId; }
    public String getWord() { return word; }
    public String getClue() { return clue; }
    public int getStartRow() { return startRow; }
    public int getStartCol() { return startCol; }
    public Direction getDirection() { return direction; }

    /** Row of the i-th letter (0 = first letter). */
    public int rowAt(int i) {
        return direction == Direction.DOWN ? startRow + i : startRow;
    }

    /** Column of the i-th letter (0 = first letter). */
    public int colAt(int i) {
        return direction == Direction.ACROSS ? startCol + i : startCol;
    }
}