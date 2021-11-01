package com.example.mentorselection.interceptor;

import com.example.mentorselection.exception.XException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class TeacherInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int role = (int) request.getAttribute("role");
        if (role == 0) {
            throw new XException(403, "无权限");
        }
        return true;
    }
}