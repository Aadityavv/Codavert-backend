# Database Fix Instructions

## Problem
The database tables `mous`, `proposals`, and `invoices` have some columns with a `VARCHAR(255)` constraint that need to be changed to `TEXT` to support longer content.

## Solution
Run the SQL script `DATABASE_MIGRATION_FIX.sql` manually on your PostgreSQL database.

### Option 1: Using psql Command Line (Recommended)
```bash
# Connect to your PostgreSQL database
psql "host=pg-273d1178-litigate.f.aivencloud.com port=13172 dbname=defaultdb user=avnadmin sslmode=require"

# Run the migration script
\i DATABASE_MIGRATION_FIX.sql
```

### Option 2: Using Aiven Console
1. Go to your Aiven console
2. Navigate to your PostgreSQL service
3. Go to the "Services" tab and click on "Query Console"
4. Paste the contents of `DATABASE_MIGRATION_FIX.sql`
5. Execute the commands

### Option 3: Using pgAdmin or other GUI
1. Connect to your database using pgAdmin or your preferred PostgreSQL client
2. Open a query window
3. Paste the contents of `DATABASE_MIGRATION_FIX.sql`
4. Execute the commands

## After Running the Migration
After running the migration, restart your Spring Boot application. The JPA entities are already configured correctly with `columnDefinition = "TEXT"`, so from that point forward, the application will work correctly.

## Verification
The migration script includes a verification query at the end that will show you the data types of the affected columns. All affected columns should show `data_type = 'text'` and `character_maximum_length = null`.

