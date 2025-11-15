-- Add 'types' column to projects table to support multiple project types
ALTER TABLE projects ADD COLUMN IF NOT EXISTS types VARCHAR(255);

-- Verify the column was added
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'projects' 
AND column_name = 'types';






