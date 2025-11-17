package com.codavert.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DatabaseMigration implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigration.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) {
        try {
            // Update users_role_check constraint to include STAFF
            updateUsersRoleConstraint();
            // Update job_applications_status_check constraint to include OFFER_ACCEPTED
            updateJobApplicationsStatusConstraint();
            logger.info("✅ Database migration completed successfully");
        } catch (Exception e) {
            logger.warn("Database migration warning: {}", e.getMessage());
            // Don't fail startup if constraint already exists or table doesn't exist yet
        }
    }
    
    private void updateUsersRoleConstraint() {
        try {
            // Check if constraint exists
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.table_constraints " +
                "WHERE table_name = 'users' AND constraint_name = 'users_role_check'",
                Integer.class
            );
            
            if (count != null && count > 0) {
                // Drop existing constraint
                jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");
                logger.info("Dropped existing users_role_check constraint");
            }
            
            // Create new constraint with STAFF included
            jdbcTemplate.execute(
                "ALTER TABLE users ADD CONSTRAINT users_role_check " +
                "CHECK (role IN ('ADMIN', 'USER', 'CLIENT', 'STAFF'))"
            );
            logger.info("Created users_role_check constraint with STAFF role");
            
        } catch (Exception e) {
            // Constraint might not exist yet or table might not exist
            // Try to create it anyway (using IF NOT EXISTS approach)
            try {
                // First try to drop if exists
                jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");
                
                // Then create new one
                jdbcTemplate.execute(
                    "ALTER TABLE users ADD CONSTRAINT users_role_check " +
                    "CHECK (role IN ('ADMIN', 'USER', 'CLIENT', 'STAFF'))"
                );
                logger.info("Created users_role_check constraint with STAFF role");
            } catch (Exception e2) {
                logger.debug("Could not update constraint (may already be correct or table doesn't exist yet): {}", e2.getMessage());
            }
        }
    }
    
    private void updateJobApplicationsStatusConstraint() {
        try {
            // Check if constraint exists
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.table_constraints " +
                "WHERE table_name = 'job_applications' AND constraint_name = 'job_applications_status_check'",
                Integer.class
            );
            
            if (count != null && count > 0) {
                // Drop existing constraint
                jdbcTemplate.execute("ALTER TABLE job_applications DROP CONSTRAINT IF EXISTS job_applications_status_check");
                logger.info("Dropped existing job_applications_status_check constraint");
            }
            
            // Create new constraint with OFFER_ACCEPTED included
            jdbcTemplate.execute(
                "ALTER TABLE job_applications ADD CONSTRAINT job_applications_status_check " +
                "CHECK (status IN ('NEW', 'REVIEWING', 'SHORTLISTED', 'INTERVIEWED', 'HIRED', 'OFFER_ACCEPTED', 'REJECTED', 'WITHDRAWN'))"
            );
            logger.info("Created job_applications_status_check constraint with OFFER_ACCEPTED status");
            
        } catch (Exception e) {
            // Constraint might not exist yet or table might not exist
            // Try to create it anyway
            try {
                // First try to drop if exists
                jdbcTemplate.execute("ALTER TABLE job_applications DROP CONSTRAINT IF EXISTS job_applications_status_check");
                
                // Then create new one
                jdbcTemplate.execute(
                    "ALTER TABLE job_applications ADD CONSTRAINT job_applications_status_check " +
                    "CHECK (status IN ('NEW', 'REVIEWING', 'SHORTLISTED', 'INTERVIEWED', 'HIRED', 'OFFER_ACCEPTED', 'REJECTED', 'WITHDRAWN'))"
                );
                logger.info("Created job_applications_status_check constraint with OFFER_ACCEPTED status");
            } catch (Exception e2) {
                logger.debug("Could not update job_applications constraint (may already be correct or table doesn't exist yet): {}", e2.getMessage());
            }
        }
    }
}


