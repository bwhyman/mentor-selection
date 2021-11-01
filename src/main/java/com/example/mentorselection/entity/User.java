package com.example.mentorselection.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    public static final int ROLE_STUDENT = 0;
    public static final int ROLE_TEACHER = 1;
    public static final int ROLE_ADMIN = 5;
    @Id
    @CreatedBy
    private Long id;
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String number;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonIgnore
    private int role;
    private Integer total;
    private Integer count;
    private Long teacherId;
    private String description;
    private LocalDateTime selectTime;
    private LocalDateTime insertTime;
}
