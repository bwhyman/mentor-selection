package com.example.mentorselection.controller;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.service.TeacherService;
import com.example.mentorselection.service.UserService;
import com.example.mentorselection.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/")
@Slf4j
@CrossOrigin
public class AdminController {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder encoder;

    //管理员角色发一个请求，过拦截器校验一下
    @GetMapping("checkadmin")
    public Mono<ResultVO> getCheckadmin() {
        return Mono.just(ResultVO.success(Map.of()));
    }

    // 重置账号密码
    @PutMapping("password/{number}")
    public Mono<ResultVO> putPwd2(@PathVariable String number) {
        return teacherService.resetPassword(number)
                .then(Mono.just(ResultVO.success(Map.of())));
    }

    // 添加教师
    /*@PostMapping("teachers")
    public Mono<ResultVO> postTeacher(@RequestBody User user) {
        user.setRole(User.ROLE_TEACHER);
        user.setPassword(encoder.encode(user.getNumber()));
        user.setCount(0);
        user.setInsertTime(LocalDateTime.now());
        return teacherService.addUser(user)
                .flatMap(u -> userService.listUsers(User.ROLE_TEACHER)
                        .flatMap(users -> Mono.just(ResultVO.success(Map.of("teachers", users)))));
    }*/

    @PostMapping("teachers")
    public Mono<ResultVO> postTeachers(@RequestBody List<User> users) {
        for (User u : users) {
            u.setPassword(encoder.encode(u.getNumber()));
            u.setRole(User.ROLE_TEACHER);
            u.setCount(0);
            u.setInsertTime(LocalDateTime.now());
        }
        return teacherService.addUsers(users)
                .map(users1 -> ResultVO.success(Map.of("teachers", users1)));
    }

    // 添加学生列表
    @PostMapping("students")
    public Mono<ResultVO> postStudents(@RequestBody List<User> users) {
        for (User u : users) {
            u.setPassword(encoder.encode(u.getNumber()));
            u.setRole(User.ROLE_STUDENT);
            u.setInsertTime(LocalDateTime.now());
        }
        return teacherService.addUsers(users)
                .map(users1 -> ResultVO.success(Map.of("users", users1)));
    }

    // 更新开始时间
    @PutMapping("starttime/{time}")
    public Mono<ResultVO> putStartTime(@PathVariable String time, @RequestAttribute("uid") long uid) {
        LocalDateTime startTime = LocalDateTime.parse(time);
        return teacherService.addStartTime(startTime, uid)
                .then(Mono.just(ResultVO.success(Map.of("time", startTime))));
    }
}
