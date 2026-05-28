package com.melodypharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MelodyPharmacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MelodyPharmacyApplication.class, args);
    }
}
