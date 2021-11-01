package com.example.mentorselection.service;

import com.example.mentorselection.entity.StartTime;
import com.example.mentorselection.entity.User;
import com.example.mentorselection.exception.XException;
import com.example.mentorselection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private StartTime startTime;

    @Transactional
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public List<User> addUsers(List<User> users) {
        List<User> list = new ArrayList<>(users.size());
        userRepository.saveAll(users).forEach(list::add);
        return list;
    }

    @Transactional
    public void addStartTime(LocalDateTime time, long uid) {
        User admin = userRepository.findById(uid).get();
        admin.setSelectTime(time);
        userRepository.save(admin);
        startTime.setStartTime(time);
    }

    public void resetPassword(long tid) {
        User t = userRepository.findById(tid).get();
        t.setPassword(encoder.encode(t.getNumber()));
        userRepository.save(t);
    }

    @Transactional
    public void resetPassword(String number, String password) {
        if(userRepository.updatePassword(number, password) == 0) {
            throw new XException(400, "密码重置失败，账号不存在");
        }
    }
}
