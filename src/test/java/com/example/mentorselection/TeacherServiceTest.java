package com.example.mentorselection;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootTest
public class TeacherServiceTest {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private PasswordEncoder encoder;
    @Test
    public void test() {
        String number = "2003";
        User u = User.builder()
                .name("BO")
                .number(number)
                .total(5)
                .count(0)
                .password(encoder.encode(number))
                .role(User.ROLE_TEACHER)
                .insertTime(LocalDateTime.now())
                .build();
        teacherService.addUser(u).block();
    }
}
