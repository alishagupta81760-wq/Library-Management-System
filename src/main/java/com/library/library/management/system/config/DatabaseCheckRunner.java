package com.library.library.management.system.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCheckRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCheckRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {

        try {

            String databaseName = jdbcTemplate.queryForObject(
                "SELECT DATABASE()",
                String.class
            );

            System.out.println("================================");
            System.out.println("LIBRARY MANAGEMENT SYSTEM");
            System.out.println("MySQL Connection Successful!");
            System.out.println("Database: " + databaseName);
            System.out.println("================================");

        } catch (Exception e) {

            System.out.println("MySQL Connection Failed!");
            e.printStackTrace();

        }
    }
}