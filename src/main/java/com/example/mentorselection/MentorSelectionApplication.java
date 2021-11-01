package com.example.mentorselection;

import com.example.mentorselection.entity.StartTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MentorSelectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentorSelectionApplication.class, args);
    }
    @Bean
    public StartTime create() {
        return StartTime.builder().build();
    }
}
