package com.example.mentorselection.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.mentorselection.component.JWTComponent;
import com.example.mentorselection.exception.XException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@Order(1)
public class LoginFilter implements WebFilter {
    @Autowired
    private JWTComponent jwtComponent;

    private final List<String> excludes = List.of("/api/login");
    private final List<String> includes = List.of("/api/");
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        for (String p : includes) {
            if (!exchange.getRequest().getPath().pathWithinApplication().value().startsWith(p)) {
                return chain.filter(exchange);
            }
        }
        for (String excludeP : excludes) {
            if (request.getPath().pathWithinApplication().value().equals(excludeP)) {
                return chain.filter(exchange);
            }
        }
        String token = request.getHeaders().getFirst("token");
        if (token == null) {
            // WebExceptionHandler处理
            throw new XException(401, "未登录");
        }
        DecodedJWT decode = jwtComponent.decode(token);
        exchange.getAttributes().put("uid", decode.getClaim("uid").asLong());
        exchange.getAttributes().put("role", decode.getClaim("role").asInt());
        return chain.filter(exchange);
    }
}
