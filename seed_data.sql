-- seed_data.sql: premade puzzles for CoolAssCrossword
-- Run ONCE, after schema.sql.
USE crossword_db;

-- Animal Kingdom (EASY): 9 words
INSERT INTO puzzle (title, difficulty) VALUES ('Animal Kingdom', 'EASY');
INSERT INTO entry (puzzle_id, word, clue, start_row, start_col, direction) VALUES
    ((SELECT id FROM puzzle WHERE title = 'Animal Kingdom'), 'TIGER', 'Big striped cat', 1, 5, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Animal Kingdom'), 'DOG', 'Man''s best friend', 3, 1, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Animal Kingdom'), 'ELEPHANT', 'Largest land animal, has a trunk', 6, 2, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Animal Kingdom'), 'GIRAFFE', 'Tallest animal in the world', 9, 1, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Animal Kingdom'), 'LION', '"King of the jungle"', 0, 6, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Animal Kingdom'), 'RABBIT', 'Hops and has long ears', 1, 9, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Animal Kingdom'), 'MONKEY', 'Primate that loves bananas', 2, 2, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Animal Kingdom'), 'BEAR', 'Hibernates in winter', 4, 7, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Animal Kingdom'), 'ZEBRA', 'Black-and-white striped horse relative', 5, 4, 'DOWN');

-- Pinoy Pride (MEDIUM): 9 words
INSERT INTO puzzle (title, difficulty) VALUES ('Pinoy Pride', 'MEDIUM');
INSERT INTO entry (puzzle_id, word, clue, start_row, start_col, direction) VALUES
    ((SELECT id FROM puzzle WHERE title = 'Pinoy Pride'), 'CEBU', 'Island province known as the Queen City of the South', 1, 2, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Pinoy Pride'), 'HALOHALO', 'Cold dessert with shaved ice and sweet toppings', 4, 0, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Pinoy Pride'), 'ADOBO', 'Filipino dish stewed in vinegar and soy sauce', 6, 5, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Pinoy Pride'), 'MAYON', 'Volcano famous for its near-perfect cone', 8, 4, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Pinoy Pride'), 'LECHON', 'Roasted whole pig served at fiestas', 0, 3, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Pinoy Pride'), 'DAVAO', 'Largest city in Mindanao', 0, 7, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Pinoy Pride'), 'TARSIER', 'Tiny big-eyed primate found in Bohol', 3, 1, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Pinoy Pride'), 'PALAWAN', 'Island with the Underground River', 3, 5, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Pinoy Pride'), 'BOHOL', 'Home of the Chocolate Hills', 3, 9, 'DOWN');

-- Java Pro (HARD): 10 words
INSERT INTO puzzle (title, difficulty) VALUES ('Java Pro', 'HARD');
INSERT INTO entry (puzzle_id, word, clue, start_row, start_col, direction) VALUES
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'SERVICE', 'Layer that holds your business rules', 0, 0, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'THREAD', 'Unit of concurrent execution', 2, 4, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'ABSTRACT', 'Keyword for a class that can''t be instantiated', 4, 0, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'STATIC', 'Belongs to the class, not an instance', 6, 2, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'COLLECTION', 'Root interface of List and Set', 8, 0, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'INTERFACE', 'Contract of method signatures a class can implement', 0, 4, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'DAO', 'Pattern that isolates database access code', 1, 8, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'JDBC', 'API that connects Java to relational databases', 2, 1, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'STRING', 'Immutable sequence of characters', 4, 9, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Java Pro'), 'SQL', 'Language used to talk to MySQL', 6, 2, 'DOWN');
