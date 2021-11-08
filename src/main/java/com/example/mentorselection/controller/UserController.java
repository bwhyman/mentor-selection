package com.example.mentorselection.controller;

import com.example.mentorselection.component.JWTComponent;
import com.example.mentorselection.entity.StartTime;
import com.example.mentorselection.entity.User;
import com.example.mentorselection.exception.XException;
import com.example.mentorselection.service.UserService;
import com.example.mentorselection.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JWTComponent jwtComponent;
    @Autowired
    private StartTime startTime;

    @PostMapping("login")
    public Mono<ResultVO> login(@RequestBody User user, ServerHttpResponse response) {
        return userService.getUser(user.getNumber())
                .doOnSuccess(u -> {
                    if (u == null || !encoder.matches(user.getPassword(), u.getPassword())) {
                        throw new XException(401, "账号密码错误");
                    }
                }).map(u -> {
                    String token = jwtComponent.encode(Map.of("uid", u.getId(), "role", u.getRole()));
                    response.getHeaders().add("token", token);
                    String role = "";
                    switch (u.getRole()) {
                        case User.ROLE_STUDENT:
                            role = "Yo87M";
                            break;
                        case User.ROLE_TEACHER:
                            role = "nU0vt";
                            break;
                        case User.ROLE_ADMIN:
                            role = "ppYMg";
                            break;
                    }
                    response.getHeaders().add("role", role);
                    return ResultVO.success(Map.of());
                });
    }

    @PutMapping("password/{pwd}")
    public Mono<Void> putPassword(@PathVariable String pwd, @RequestAttribute("uid") long uid) {
        return userService.updatePassword(uid, pwd);
    }

    @GetMapping("info")
    public Mono<ResultVO> getInfo(@RequestAttribute("uid") long uid) {
        return userService.getUser(uid)
                .map(user -> ResultVO.success(Map.of("user", user,"starttime", startTime.getStartTime())));
    }


    // 全部教师信息
    @GetMapping("teachers")
    public Mono<ResultVO> getTeachers(@RequestAttribute("role") int role) {
        return role == User.ROLE_STUDENT && startTime.getStartTime().isAfter(LocalDateTime.now())
                ? Mono.just(ResultVO.error(400, "开始时间：" + startTime.getStartTime().toString().replace("T", " ")))
                : userService.listUsers(User.ROLE_TEACHER)
                .map(users -> ResultVO.success(Map.of("teachers", users)));
    }

    // 选导师
    @PutMapping("teachers/{tid}")
    public Mono<ResultVO> postSection(@PathVariable long tid, @RequestAttribute("uid") long uid) {
        if (startTime.getStartTime().isAfter(LocalDateTime.now())) {
            throw new XException(400, "未到开始时间");
        }
        return userService.select(uid, tid)
                .map(u -> ResultVO.success(Map.of("user", u)));
    }
}
