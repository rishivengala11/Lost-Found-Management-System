USE lost_found_db;

-- 1. Clear old data without user_id
DELETE FROM lost_items;
DELETE FROM found_items;

-- 2. Add columns to lost_items
ALTER TABLE lost_items ADD COLUMN user_id INT NOT NULL;
ALTER TABLE lost_items ADD FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE lost_items ADD COLUMN claim_requested BOOLEAN DEFAULT FALSE;

-- 3. Add columns to found_items
ALTER TABLE found_items ADD COLUMN user_id INT NOT NULL;
ALTER TABLE found_items ADD FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE found_items ADD COLUMN return_requested BOOLEAN DEFAULT FALSE;
