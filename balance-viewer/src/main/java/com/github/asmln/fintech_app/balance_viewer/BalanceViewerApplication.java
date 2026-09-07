package com.github.asmln.fintech_app.balance_viewer;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class BalanceViewerApplication {
    static void main(String[] args) {
        SpringApplication.run(BalanceViewerApplication.class, args);
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
