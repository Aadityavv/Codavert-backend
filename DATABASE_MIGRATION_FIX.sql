-- Database Migration to Fix Column Length Issues
-- This script should be run on your PostgreSQL database to fix the character varying(255) constraints

-- Fix MOU table
ALTER TABLE mous ALTER COLUMN project_description TYPE TEXT;

-- Fix Proposal table  
ALTER TABLE proposals ALTER COLUMN payment_terms TYPE TEXT;

-- Fix Invoice table
ALTER TABLE invoices ALTER COLUMN notes TYPE TEXT;
ALTER TABLE invoices ALTER COLUMN payment_terms TYPE TEXT;

-- Verify the changes were applied
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name IN ('mous', 'proposals', 'invoices') 
AND column_name IN ('project_description', 'payment_terms', 'notes');

