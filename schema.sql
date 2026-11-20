USE crossword_db;

DROP TABLE IF EXISTS attempt, entry, puzzle;

CREATE TABLE puzzle (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(100) NOT NULL UNIQUE,
    difficulty  VARCHAR(10) NOT NULL,
    CONSTRAINT chk_puzzle_difficulty CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD'))
);

CREATE TABLE entry (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    puzzle_id  INT NOT NULL,
    word       VARCHAR(10) NOT NULL,
    clue       VARCHAR(200) NOT NULL,
    start_row  INT NOT NULL,
    start_col  INT NOT NULL,
    direction  VARCHAR(6) NOT NULL,
    CONSTRAINT fk_entry_puzzle FOREIGN KEY (puzzle_id)
        REFERENCES puzzle(id) ON DELETE CASCADE,
    CONSTRAINT uq_entry_word UNIQUE (puzzle_id, word),
    CONSTRAINT chk_entry_word      CHECK (REGEXP_LIKE(word, '^[A-Z]{3,10}$', 'c')),
    CONSTRAINT chk_entry_clue      CHECK (CHAR_LENGTH(TRIM(clue)) > 0),
    CONSTRAINT chk_entry_start     CHECK (start_row BETWEEN 0 AND 9 AND start_col BETWEEN 0 AND 9),
    CONSTRAINT chk_entry_direction CHECK (direction IN ('ACROSS', 'DOWN'))
);
 
CREATE TABLE attempt (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    puzzle_id      INT NOT NULL,
    player_name    VARCHAR(30) NOT NULL,
    solve_seconds  INT NOT NULL,
    finished_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attempt_puzzle FOREIGN KEY (puzzle_id) REFERENCES puzzle(id),
    CONSTRAINT chk_attempt_name   CHECK (CHAR_LENGTH(TRIM(player_name)) > 0),
    CONSTRAINT chk_attempt_time   CHECK (solve_seconds >= 0)
);