package com.example.mentorselection.service;

import com.example.mentorselection.entity.StartTime;
import com.example.mentorselection.entity.User;
import com.example.mentorselection.exception.XException;
import com.example.mentorselection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeacherService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private StartTime startTime;

    @Transactional
    public Mono<User> addUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public Mono<List<User>> addUsers(List<User> users) {
        return userRepository.saveAll(users).collectList();
    }

    @Transactional
    public Mono<Void> addStartTime(LocalDateTime time, long uid) {
        return userRepository.findById(uid).flatMap(admin -> {
            startTime.setStartTime(time);
            admin.setSelectTime(time);
            return userRepository.save(admin).then();
        });
    }

    @Transactional
    public Mono<Void> resetPassword(String number) {
        return userRepository.updatePassword(number, encoder.encode(number))
                .doOnSuccess(r -> {
                    if (r == 0) {
                        throw new XException(400, "密码重置失败，账号不存在");
                    }
                }).then();
    }

    // 未选导师学生
    public Mono<List<User>> listUnselected() {
        return userRepository.listStudentsByUnselected().collectList();
    }

    //获取指定教师下的学生
    public Mono<List<User>> listStudents(long tid) {
        return userRepository.listStudentsByTid(tid).collectList();
    }
}
