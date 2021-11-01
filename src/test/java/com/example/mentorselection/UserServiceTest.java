package com.example.mentorselection;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.repository.UserRepository;
import com.example.mentorselection.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void test4() {
        userService.select(904223442519678976L, 904219296412426240L);
    }
    @Test
    public void test5() throws InterruptedException {
        new Thread(() -> {
            userService.select(904223442519678976L, 904226120289181696L);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            userService.select(904231503397347328L, 904226120289181696L);
        }).start();

        Thread.sleep(2000);
    }
}
