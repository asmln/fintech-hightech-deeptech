package com.github.asmln.fintech_app.tx_processor;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TxProcessorApplication {
    static void main(String[] args) {
        SpringApplication.run(TxProcessorApplication.class, args);
    }

    @Bean
    public CommandLineRunner checkMigrations(Flyway flyway) {
        return args -> {
            // Информация о миграциях
            var info = flyway.info();
            System.out.println("=== FLYWAY MIGRATIONS ===");
            System.out.println("Current version: " + info.current().getVersion());
            System.out.println("Pending migrations: " + info.pending().length);
            System.out.println("Applied migrations: " + info.applied().length);
            System.out.println("=========================");
        };
    }
}
