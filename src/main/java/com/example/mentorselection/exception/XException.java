package com.example.mentorselection.exception;


import lombok.*;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XException extends RuntimeException{
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    private int code;
    public XException(int code, String message) {
        super(message);
        this.code = code;
    }
}
