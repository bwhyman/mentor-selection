package com.example.mentorselection.service;

import com.example.mentorselection.entity.StartTime;
import com.example.mentorselection.entity.User;
import com.example.mentorselection.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class InitService implements InitializingBean {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StartTime startTime;
    @Transactional
    @Override
    public void afterPropertiesSet() throws Exception {
        String number = "2046";
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .name("admin")
                    .number(number)
                    .password(encoder.encode(number))
                    .role(User.ROLE_ADMIN)
                    .insertTime(LocalDateTime.now())
                    .selectTime(LocalDateTime.now().plusMonths(5))
                    .build();
            userRepository.save(admin);
            return;
        }
        LocalDateTime time = userRepository.find(number).getSelectTime();
        startTime.setStartTime(time);
    }
}
