package com.example.mentorselection.controller;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.service.UserService;
import com.example.mentorselection.vo.ResultVO;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher/")
@Slf4j
public class TeacherController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("unselected")
    public ResultVO getUnSelected() {
        return ResultVO.success(Map.of("students",  userService.listUnselected()));
    }

    @GetMapping("students")
    public ResultVO getStudents(@RequestAttribute("uid") long tid) {
        return ResultVO.success(Map.of("students",  userService.listStudents(tid)));
    }

    @PostMapping("students")
    public ResultVO postStudent(@RequestBody User student, @RequestAttribute("uid") long tid) {
        userService.addStudent(tid, student);
        User t = userService.getUser(tid);
        return ResultVO.success(Map.of("students",  userService.listStudents(tid), "teacher", t));
    }
}
