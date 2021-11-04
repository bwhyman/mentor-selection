package com.example.mentorselection;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
public class AdminServiceTest {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private PasswordEncoder encoder;

    @Test
    public void test() {
        String number = "2002";
        User u = User.builder()
                .name("t2")
                .number(number)
                .count(0)
                .total(10)
                .password(encoder.encode(number))
                .role(User.ROLE_TEACHER)
                .insertTime(LocalDateTime.now())
                .build();
        teacherService.addUser(u);
    }

    @Test
    public void test2() {
        String number = "201804";
        User u = User.builder()
                .name("s4")
                .number(number)
                .password(encoder.encode(number))
                .role(User.ROLE_STUDENT)
                .insertTime(LocalDateTime.now())
                .build();
        teacherService.addUser(u);
    }
}
