package crossword.model;

import java.util.ArrayList;
import java.util.List;

public class Puzzle {
    private int id;
    private String title;
    private Difficulty difficulty;
    private final List<Entry> entries = new ArrayList<>();

    public Puzzle(int id, String title, Difficulty difficulty) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public Difficulty getDifficulty() { return difficulty; }
    public List<Entry> getEntries() { return entries; }

    public void addEntry(Entry entry) { entries.add(entry); }

    @Override
    public String toString() { return title + " (" + difficulty + ")"; }
}