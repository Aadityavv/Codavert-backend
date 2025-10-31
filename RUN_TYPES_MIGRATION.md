# Running the Types Column Migration

This migration adds the `types` column to the `projects` table to support multiple project types.

## Steps to Run

1. **Log into Aiven Console**
   - Go to https://console.aiven.io/
   - Navigate to your PostgreSQL service for Codavert

2. **Open Query Editor**
   - Click on your PostgreSQL service
   - Go to "Query" tab or "SQL Console"
   - Open the SQL query editor

3. **Execute the Migration**
   ```sql
   -- Add 'types' column to projects table to support multiple project types
   ALTER TABLE projects ADD COLUMN IF NOT EXISTS types VARCHAR(255);

   -- Verify the column was added
   SELECT column_name, data_type, character_maximum_length 
   FROM information_schema.columns 
   WHERE table_name = 'projects' 
   AND column_name = 'types';
   ```

4. **Verify Results**
   - You should see the `types` column listed with `character_maximum_length` of `255`

## Restart Backend

After running the migration:
1. Restart your backend application
2. Test creating a project with multiple types selected
3. Verify that the types are saved and displayed correctly

## Notes

- This migration is **safe** and will **NOT** cause data loss
- Existing projects will have `NULL` for the `types` column
- The migration uses `ADD COLUMN IF NOT EXISTS` to prevent errors if the column already exists

