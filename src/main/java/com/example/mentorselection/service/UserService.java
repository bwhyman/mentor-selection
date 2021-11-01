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
        User user = userRepository.findById(uid).get();
        user.setPassword(encoder.encode(pwd));
        userRepository.save(user);
    }
    // 基于不同角色列出用户
    public List<User> listUsers(int role) {
        return userRepository.list(role);
    }

    // 选导师
    @Transactional
    public void select(long sid, long tid) {
        // 如果学生不存在
        User student = userRepository.findById(sid).orElse(null);
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
            student.setSelectTime(LocalDateTime.now());
            userRepository.save(student);
        }
    }
    // 未选导师学生
    public List<User> listUnselected() {
        return userRepository.listStudentsByUnselected();
    }
    //获取指定教师下的学生
    public List<User> listStudents(long tid) {
        return userRepository.listStudentsByTid(tid);
    }

    public void addStudent(long tid, User student) {
        User stu = userRepository.find(student.getNumber());
        User teacher = userRepository.findById(tid).get();
        if (stu == null || !stu.getName().equals(student.getName())) {
            throw new XException(400, "学生不存在");
        }
        if (stu.getTeacherId() != null) {
            User t = userRepository.findById(stu.getTeacherId()).get();
            throw new XException(400, "学生已被 " + t.getName() + " 老师录入，请与相关学生教师确认");
        }
        int result = userRepository.updateTeacherCount(tid);
        if (result == 0) {
            throw new XException(400, "录入学生已达总数，不可录入");
        }
        stu.setTeacherId(tid);
        stu.setSelectTime(LocalDateTime.now());
        userRepository.save(stu);
    }
}
