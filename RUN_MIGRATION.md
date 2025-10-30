# How to Run the Database Migration

## Problem
You're getting this error when trying to generate MOU, Proposal, or Invoice documents:
```
ERROR: value too long for type character varying(255)
```

This means some database columns need to be changed from `VARCHAR(255)` to `TEXT`.

## Quick Solution

### Step 1: Access Aiven Console
1. Go to https://console.aiven.io/
2. Log in to your account
3. Find your PostgreSQL service "pg-273d1178-litigate"

### Step 2: Run the Migration SQL
1. In the Aiven console, click on your PostgreSQL service
2. Click on the **"Services"** tab at the top
3. Click on **"Query Console"** (or "SQL Editor" depending on your Aiven version)
4. A SQL editor window will open

### Step 3: Execute the Migration
Copy and paste the following SQL commands into the query console:

```sql
-- Fix MOU table
ALTER TABLE mous ALTER COLUMN project_description TYPE TEXT;

-- Fix Proposal table  
ALTER TABLE proposals ALTER COLUMN payment_terms TYPE TEXT;

-- Fix Invoice table
ALTER TABLE invoices ALTER COLUMN notes TYPE TEXT;
ALTER TABLE invoices ALTER COLUMN payment_terms TYPE TEXT;

-- Verify the changes
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name IN ('mous', 'proposals', 'invoices') 
AND column_name IN ('project_description', 'payment_terms', 'notes');
```

5. Click **"Execute"** or **"Run"** button
6. You should see a success message like "ALTER TABLE" for each command

### Step 4: Verify
The last SELECT query should show:
- `data_type = 'text'`
- `character_maximum_length = null`

### Step 5: Restart Your Backend
After running the migration, restart your Spring Boot backend:
```bash
# Stop your running backend (Ctrl+C)
# Then start it again
cd codavert-backend
mvn spring-boot:run
```

## That's It!
Now try generating an MOU, Proposal, or Invoice again. It should work without any length constraint errors.

## Alternative: Use psql Command Line
If you have `psql` installed and prefer command line:

```bash
psql "host=pg-273d1178-litigate.f.aivencloud.com port=13172 dbname=defaultdb user=avnadmin password=AVNS_sIk-N6bdYFKnzFRgPLp sslmode=require" -c "ALTER TABLE mous ALTER COLUMN project_description TYPE TEXT;"
psql "host=pg-273d1178-litigate.f.aivencloud.com port=13172 dbname=defaultdb user=avnadmin password=AVNS_sIk-N6bdYFKnzFRgPLp sslmode=require" -c "ALTER TABLE proposals ALTER COLUMN payment_terms TYPE TEXT;"
psql "host=pg-273d1178-litigate.f.aivencloud.com port=13172 dbname=defaultdb user=avnadmin password=AVNS_sIk-N6bdYFKnzFRgPLp sslmode=require" -c "ALTER TABLE invoices ALTER COLUMN notes TYPE TEXT;"
psql "host=pg-273d1178-litigate.f.aivencloud.com port=13172 dbname=defaultdb user=avnadmin password=AVNS_sIk-N6bdYFKnzFRgPLp sslmode=require" -c "ALTER TABLE invoices ALTER COLUMN payment_terms TYPE TEXT;"
```

