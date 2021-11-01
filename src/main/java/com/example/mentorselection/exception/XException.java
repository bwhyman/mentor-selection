package com.example.mentorselection.exception;


import lombok.*;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XException extends RuntimeException{
    private int code;
    public XException(int code, String message) {
        super(message);
        this.code = code;
    }
}
