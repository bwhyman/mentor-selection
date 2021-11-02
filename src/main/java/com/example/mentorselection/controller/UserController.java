package com.example.mentorselection.controller;

import com.example.mentorselection.component.JWTComponent;
import com.example.mentorselection.entity.StartTime;
import com.example.mentorselection.entity.User;
import com.example.mentorselection.exception.XException;
import com.example.mentorselection.service.UserService;
import com.example.mentorselection.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
    public ResultVO login(@RequestBody User user, HttpServletResponse response) {
        User u = userService.getUser(user.getNumber());
        if (u == null || !encoder.matches(user.getPassword(), u.getPassword())) {
            throw new XException(401, "账号密码错误");
        }
        String token = jwtComponent.encode(Map.of("uid", u.getId(), "role", u.getRole()));
        response.addHeader("token", token);
        String role = "";
        switch (u.getRole()) {
            case 0:
                role = "eyJ0";
                break;
            case 1:
                role = "eXAi";
                break;
            case 5:
                role = "OiJK";
                break;
        }
        response.addHeader("role", role);
        return ResultVO.success(Map.of());
    }

    @PutMapping("password/{pwd}")
    public ResultVO putPassword(@PathVariable String pwd, @RequestAttribute("uid") long uid) {
        userService.updatePassword(uid, pwd);
        return ResultVO.success(Map.of());
    }

    @GetMapping("info")
    public ResultVO getInfo(@RequestAttribute("uid") long uid) {
        User u = userService.getUser(uid);

        return ResultVO.success(Map.of("user", u,"starttime", startTime.getStartTime()));

    }
    // 全部教师信息
    @GetMapping("teachers")
    public ResultVO getTeachers(@RequestAttribute("role") int role) {
        if (role == User.ROLE_STUDENT && startTime.getStartTime().isAfter(LocalDateTime.now())) {
            return ResultVO.error(400, "开始时间：" + startTime.getStartTime().toString().replace("T", " "));
        }
        return ResultVO.success(Map.of("teachers", userService.listUsers(User.ROLE_TEACHER)));
    }

    // 选导师
    @PutMapping("teachers/{tid}")
    public ResultVO postSection(@PathVariable long tid, @RequestAttribute("uid") long uid) {
        if (startTime.getStartTime().isAfter(LocalDateTime.now())) {
            throw new XException(400, "未到开始时间");
        }
        userService.select(uid, tid);
        return ResultVO.success(Map.of("user", userService.getUser(uid),
                "teachers", userService.listUsers(User.ROLE_TEACHER)));
    }
}
