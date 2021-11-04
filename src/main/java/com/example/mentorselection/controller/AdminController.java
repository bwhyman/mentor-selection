package com.example.mentorselection.controller;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.service.TeacherService;
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
    private TeacherService teacherService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder encoder;

    //管理员角色发一个请求，过拦截器校验一下
    @GetMapping("checkadmin")
    public ResultVO getCheckadmin() {
        return ResultVO.success(Map.of());
    }
    // 重置账号密码
    @PutMapping("password/{number}")
    public ResultVO putPwd2(@PathVariable String number) {
        teacherService.resetPassword(number);
        return ResultVO.success(Map.of());
    }
    // 添加教师
    @PostMapping("teachers")
    public ResultVO postTeacher(@RequestBody User user) {
        user.setRole(User.ROLE_TEACHER);
        user.setPassword(encoder.encode(user.getNumber()));
        user.setCount(0);
        user.setInsertTime(LocalDateTime.now());
        User u = teacherService.addUser(user);
        return ResultVO.success(Map.of("teachers", userService.listUsers(User.ROLE_TEACHER)));
    }
    // 添加学生列表
    @PostMapping("students")
    public ResultVO postStudents(@RequestBody List<User> users) {
        for (User u : users) {
            u.setPassword(encoder.encode(u.getNumber()));
            u.setRole(User.ROLE_STUDENT);
            u.setInsertTime(LocalDateTime.now());
        }
        List<User> users1 = teacherService.addUsers(users);
        return ResultVO.success(Map.of("users", users1));
    }

    // 更新开始时间
    @PutMapping("starttime/{time}")
    public ResultVO putStartTime(@PathVariable String time, @RequestAttribute("uid") long uid) {
        log.debug("" + time);
        LocalDateTime a = LocalDateTime.parse(time);
        log.debug("" + a);
        teacherService.addStartTime(a, uid);
        return ResultVO.success(Map.of());
    }
}
