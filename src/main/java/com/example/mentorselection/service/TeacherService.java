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
public class TeacherService {
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
    @Transactional
    public void resetPassword(String number) {
        if(userRepository.updatePassword(number, encoder.encode(number)) == 0) {
            throw new XException(400, "密码重置失败，账号不存在");
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
