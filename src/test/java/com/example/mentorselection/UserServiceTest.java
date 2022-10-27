package com.example.mentorselection;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.repository.UserRepository;
import com.example.mentorselection.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Slf4j
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() {
        userService.select(904300024722579456L, 904219296412426240L)
                .block();
    }

    @Test
    public void test3() {
        User u = User.builder().name("赵磊").insertTime(LocalDateTime.now()).description("df").role(0).build();
        userRepository.save(u).block();
    }
    @Test
    public void test4() {
        userRepository.updateTeachersByCount().block();
    }
    @Test
    public void test5() {
        userRepository.updateStudentsByTeacherNull().block();
    }

    @Test
    public void test6() {
        List<User> users = userRepository.listStudentsByUnselected().collectList().block();
        assert users != null;
        for (User u : users) {
            log.debug(u.getName());
        }
    }

}
