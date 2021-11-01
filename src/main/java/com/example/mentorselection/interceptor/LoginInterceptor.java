package com.example.mentorselection.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.mentorselection.component.JWTComponent;
import com.example.mentorselection.exception.XException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JWTComponent jwtComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token == null) {
            throw new XException(401, "未登录");
        }
        DecodedJWT decodedJWT = jwtComponent.decode(token);
        long uid = decodedJWT.getClaim("uid").asLong();
        int role = decodedJWT.getClaim("role").asInt();
        request.setAttribute("uid", uid);
        request.setAttribute("role", role);
        return true;
    }
}
