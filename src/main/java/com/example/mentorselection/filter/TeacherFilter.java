package com.example.mentorselection.filter;

import com.example.mentorselection.exception.XException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@Order(2)
public class TeacherFilter implements WebFilter {
    private final List<String> includes = List.of("/api/teacher/", "/api/admin/");
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        for (String p : includes) {
            if (exchange.getRequest().getPath().pathWithinApplication().value().startsWith(p)) {
                int role = (int) exchange.getAttributes().get("role");
                if (role == 0) {
                    // WebExceptionHandler处理
                    throw new XException(403, "无权限");
                }
            }
        }
        return chain.filter(exchange);
    }
}
