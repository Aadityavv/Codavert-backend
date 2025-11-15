-- Add STAFF role to users table constraint
-- This script updates the CHECK constraint on the users.role column to include 'STAFF'

-- First, drop the existing constraint
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Recreate the constraint with STAFF included
ALTER TABLE users ADD CONSTRAINT users_role_check 
    CHECK (role IN ('ADMIN', 'USER', 'CLIENT', 'STAFF'));

-- Add OFFER_ACCEPTED status to job_applications table constraint
-- This script updates the CHECK constraint on the job_applications.status column to include 'OFFER_ACCEPTED'

-- First, drop the existing constraint
ALTER TABLE job_applications DROP CONSTRAINT IF EXISTS job_applications_status_check;

-- Recreate the constraint with OFFER_ACCEPTED included
ALTER TABLE job_applications ADD CONSTRAINT job_applications_status_check 
    CHECK (status IN ('NEW', 'REVIEWING', 'SHORTLISTED', 'INTERVIEWED', 'HIRED', 'OFFER_ACCEPTED', 'REJECTED', 'WITHDRAWN'));

