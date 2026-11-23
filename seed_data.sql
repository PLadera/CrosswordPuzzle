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
-- Human Body (HARD): 8 words
INSERT INTO puzzle (title, difficulty) VALUES ('Human Body', 'HARD');
INSERT INTO entry (puzzle_id, word, clue, start_row, start_col, direction) VALUES
    ((SELECT id FROM puzzle WHERE title = 'Human Body'), 'TENDON', 'Which tough band of tissue connects a muscle to a bone?', 0, 8, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Human Body'), 'ARTERY', 'What type of blood vessel carries blood away from the heart?', 1, 0, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Human Body'), 'KIDNEY', 'Which organ removes waste and excess water from the blood to make urine?', 1, 4, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Human Body'), 'DIAPHRAGM', 'Which muscle below the lungs contracts and moves downward when you breathe in?', 1, 6, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Human Body'), 'PANCREAS', 'Which organ produces both insulin and enzymes that help digest food?', 2, 2, 'DOWN'),
    ((SELECT id FROM puzzle WHERE title = 'Human Body'), 'TRACHEA', 'Which tube carries air from the voice box toward the lungs?', 3, 0, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Human Body'), 'RETINA', 'Which light-sensitive layer at the back of the eye converts light into electrical signals?', 7, 1, 'ACROSS'),
    ((SELECT id FROM puzzle WHERE title = 'Human Body'), 'FEMUR', 'Which bone in the thigh is the longest bone in the human body?', 9, 4, 'ACROSS');
