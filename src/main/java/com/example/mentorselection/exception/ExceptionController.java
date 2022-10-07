package com.example.mentorselection.exception;


import com.example.mentorselection.vo.ResultVO;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(XException.class)
    public Mono<ResultVO> handleValidException(XException exception) {
        return Mono.just(ResultVO.error(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResultVO> handleException(Exception exception) {
        return Mono.just(ResultVO.error(400, "请求错误"));
    }

    /**
     * 唯一约束冲突
     * @param exception
     * @return
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResultVO> handelDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return Mono.just(ResultVO.error(400, "唯一约束冲突！" + exception.getMessage()));
    }
}
