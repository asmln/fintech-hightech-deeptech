package com.github.asmln.fintech_app.tx_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TxProcessorApplication {
    static void main(String[] args) {
        SpringApplication.run(TxProcessorApplication.class, args);
    }
}
