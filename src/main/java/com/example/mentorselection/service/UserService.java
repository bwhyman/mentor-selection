package com.example.mentorselection.service;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.exception.XException;
import com.example.mentorselection.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    // 登录用
    public User getUser(String number) {
        return userRepository.find(number);
    }

    public User getUser(long uid) {
        return userRepository.findById(uid).orElse(null);
    }
    // 个人更新密码
    public void updatePassword(long uid, String pwd) {
        if (userRepository.updatePassword(uid, encoder.encode(pwd)) == 0) {
            throw new XException(400, "密码重置失败，账号不存在");
        }
    }
    // 基于不同角色列出用户
    public List<User> listUsers(int role) {
        return userRepository.list(role);
    }

    // 选导师
    @Transactional
    public void select(long sid, long tid) {
        // 如果学生不存在，sql加了for update
        User student = userRepository.findByIdForUpdate(sid);
        if (student == null) {
            throw new XException(400, "学生不存在");
        }
        // 如果已经选择过
        if (student.getTeacherId() != null) {
            throw new XException(400, "导师不可重复选择");
        }
        User teacher = userRepository.findById(tid).orElse(null);
        if (teacher == null) {
            throw new XException(400, "导师不存在");
        }
        if (teacher.getTotal() - teacher.getCount() == 0) {
            throw new XException(400, "导师数量已满，请重新选择");
        }
        // 数据库会锁行，从而完成并发同步
        int result = userRepository.updateTeacherCount(tid);
        if (result > 0) {
            student.setTeacherId(tid);
            student.setTeacherName(teacher.getName());
            student.setSelectTime(LocalDateTime.now());
            userRepository.save(student);
        } else {
            throw new XException(400, "导师数量已满，请重新选择");
        }
    }

}
