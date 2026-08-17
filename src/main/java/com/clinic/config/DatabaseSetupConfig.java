package com.clinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Component
public class DatabaseSetupConfig implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(DatabaseSetupConfig.class.getName());
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseSetupConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            String driverClassName = jdbcTemplate.getDataSource().getConnection().getMetaData().getDriverName();
            if (driverClassName.toLowerCase().contains("h2")) {
                LOGGER.info("H2 Database detected. Skipping MySQL stored procedure and trigger creation.");
                return;
            }

            LOGGER.info("MySQL Database detected. Initializing Stored Procedures and Triggers...");

            // Create Stored Procedure
            jdbcTemplate.execute("DROP PROCEDURE IF EXISTS CalculateAppointmentBill");
            jdbcTemplate.execute(
                "CREATE PROCEDURE CalculateAppointmentBill(IN app_id INT)\n" +
                "BEGIN\n" +
                "    DECLARE treat_type VARCHAR(100);\n" +
                "    DECLARE consult_fee DECIMAL(10,2);\n" +
                "    DECLARE treat_cost DECIMAL(10,2);\n" +
                "    DECLARE tot_cost DECIMAL(10,2);\n" +
                "    DECLARE tx DECIMAL(10,2);\n" +
                "    DECLARE grnd_tot DECIMAL(10,2);\n" +
                "    \n" +
                "    SELECT treatment_type, consultation_fee INTO treat_type, consult_fee FROM appointments WHERE appointment_id = app_id;\n" +
                "    \n" +
                "    IF treat_type = 'Cleaning' THEN\n" +
                "        SET treat_cost = 50.00;\n" +
                "    ELSEIF treat_type = 'Filling' THEN\n" +
                "        SET treat_cost = 80.00;\n" +
                "    ELSEIF treat_type = 'Extraction' THEN\n" +
                "        SET treat_cost = 120.00;\n" +
                "    ELSEIF treat_type = 'Root Canal' THEN\n" +
                "        SET treat_cost = 300.00;\n" +
                "    ELSE\n" +
                "        SET treat_cost = 50.00;\n" +
                "    END IF;\n" +
                "    \n" +
                "    SET tot_cost = treat_cost + consult_fee;\n" +
                "    SET tx = tot_cost * 0.10;\n" +
                "    SET grnd_tot = tot_cost + tx;\n" +
                "    \n" +
                "    INSERT INTO bills (appointment_id, treatment_cost, consultation_fee, total_cost, tax, grand_total, payment_status)\n" +
                "    VALUES (app_id, treat_cost, consult_fee, tot_cost, tx, grnd_tot, 'UNPAID')\n" +
                "    ON DUPLICATE KEY UPDATE\n" +
                "        treatment_cost = treat_cost,\n" +
                "        consultation_fee = consult_fee,\n" +
                "        total_cost = tot_cost,\n" +
                "        tax = tx,\n" +
                "        grand_total = grnd_tot;\n" +
                "END"
            );
            LOGGER.info("Stored Procedure [CalculateAppointmentBill] registered successfully.");

            // Create Trigger
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS after_bill_payment");
            jdbcTemplate.execute(
                "CREATE TRIGGER after_bill_payment\n" +
                "AFTER UPDATE ON bills\n" +
                "FOR EACH ROW\n" +
                "BEGIN\n" +
                "    IF NEW.payment_status = 'PAID' THEN\n" +
                "        UPDATE appointments SET status = 'COMPLETED' WHERE appointment_id = NEW.appointment_id;\n" +
                "    END IF;\n" +
                "END"
            );
            LOGGER.info("Trigger [after_bill_payment] registered successfully.");

        } catch (Exception e) {
            LOGGER.warning("Failed to initialize MySQL stored procedures / triggers: " + e.getMessage() + 
                           ". This is expected if the target database is not MySQL.");
        }
    }
}
