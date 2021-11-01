package com.example.mentorselection.controller;


import com.example.mentorselection.exception.XException;
import com.example.mentorselection.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(XException.class)
    public ResultVO handleValidException(XException exception) {
        return ResultVO.error(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultVO handleException(Exception exception) {
        return ResultVO.error(400, "请求错误");
    }
}
