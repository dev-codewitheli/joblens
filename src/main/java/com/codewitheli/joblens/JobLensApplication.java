package com.codewitheli.joblens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * JobLens — an AI-powered job-application assistant.
 *
 * <p>Paste a job description and JobLens extracts its requirements, matches them
 * against a skills-only profile (no personal data), and generates a fit report
 * plus tailored, PII-free application material.
 */
@SpringBootApplication
public class JobLensApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobLensApplication.class, args);
    }
}
