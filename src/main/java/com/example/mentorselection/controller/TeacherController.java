package com.example.mentorselection.controller;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.service.TeacherService;
import com.example.mentorselection.service.UserService;
import com.example.mentorselection.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher/")
@Slf4j
@CrossOrigin
public class TeacherController {
    @Autowired
    private UserService userService;

    @Autowired
    private TeacherService teacherService;

    // 查看未选择学生
    @GetMapping("unselected")
    public Mono<ResultVO> getUnSelected() {
        return teacherService.listUnselected()
                .map(users -> ResultVO.success(Map.of("students", users)));
    }

    // 教师下学生
    @GetMapping("students")
    public Mono<ResultVO> getStudents(@RequestAttribute("uid") long tid) {
        return teacherService.listStudents(tid)
                .map(users -> ResultVO.success(Map.of("students", users)));
    }

    // 全部学生指导教师，导出表格用
    @GetMapping("allstudents")
    public Mono<ResultVO> getAllStudents() {
        return userService.listUsers(User.ROLE_STUDENT)
                .map(users -> ResultVO.success(Map.of("students", users)));
    }
}
