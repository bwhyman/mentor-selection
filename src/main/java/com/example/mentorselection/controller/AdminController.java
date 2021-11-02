package com.example.mentorselection.controller;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.service.AdminService;
import com.example.mentorselection.service.UserService;
import com.example.mentorselection.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/")
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder encoder;

    @PutMapping("password/{tid}")
    public ResultVO putPwd(@PathVariable long tid) {
        adminService.resetPassword(tid);
        return ResultVO.success(Map.of());
    }
    @PutMapping("passwordstudent/{number}")
    public ResultVO putPwd2(@PathVariable String number) {
        adminService.resetPassword(number, encoder.encode(number));
        return ResultVO.success(Map.of());
    }


    @PostMapping("teachers")
    public ResultVO postTeacher(@RequestBody User user) {
        user.setRole(User.ROLE_TEACHER);
        user.setPassword(encoder.encode(user.getNumber()));
        user.setCount(0);
        user.setInsertTime(LocalDateTime.now());
        User u = adminService.addUser(user);

        return ResultVO.success(Map.of("teachers", userService.listUsers(User.ROLE_TEACHER)));
    }

    @PostMapping("students")
    public ResultVO postStudents(@RequestBody List<User> users) {
        for (User u : users) {
            u.setPassword(encoder.encode(u.getNumber()));
            u.setRole(User.ROLE_STUDENT);
            u.setInsertTime(LocalDateTime.now());
        }
        List<User> users1 = adminService.addUsers(users);
        return ResultVO.success(Map.of("users", users1));
    }

    @GetMapping("{tid}/students")
    public ResultVO getTeachersByTid(@PathVariable long tid) {
        return ResultVO.success(Map.of("students",  userService.listStudents(tid)));
    }

    @PutMapping("starttime/{time}")
    public ResultVO putStartTime(@PathVariable String time, @RequestAttribute("uid") long uid) {
        log.debug("" + time);
        LocalDateTime a = LocalDateTime.parse(time);
        log.debug("" + a);
        adminService.addStartTime(a, uid);
        return ResultVO.success(Map.of());
    }
    @GetMapping("allstudents")
    public ResultVO getAllStudents() {
        return ResultVO.success(Map.of("students",userService.listUsers(User.ROLE_STUDENT)));
    }
}
