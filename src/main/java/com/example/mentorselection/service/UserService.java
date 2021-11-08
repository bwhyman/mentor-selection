package com.example.mentorselection.service;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.exception.XException;
import com.example.mentorselection.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    // 登录用
    public Mono<User> getUser(String number) {
        return userRepository.find(number);
    }

    public Mono<User> getUser(long uid) {
        return userRepository.findById(uid);
    }

    // 个人更新密码
    public Mono<Void> updatePassword(long uid, String pwd) {
        return userRepository.updatePassword(uid, encoder.encode(pwd))
                .doOnSuccess(r -> {
                    if (r == 0) {
                        throw new XException(400, "密码重置失败，账号不存在");
                    }
                }).then();
    }


    // 基于不同角色列出用户
    public Mono<List<User>> listUsers(int role) {
        return userRepository.list(role).collectList();
    }

    // 选导师
    @Transactional
    public Mono<User> select(long sid, long tid) {
        // 如果学生不存在，sql加了for update
        Mono<User> studentM = userRepository.findByIdForUpdate(sid)
                .doOnSuccess(s -> {
                    if (s == null) {
                        throw new XException(400, "学生不存在");
                    }
                    if (s.getTeacherId() != null) {
                        throw new XException(400, "导师不可重复选择");
                    }
                });

        Mono<User> teacherM = userRepository.findById(tid)
                .doOnSuccess(t -> {
                    if (t == null) {
                        throw new XException(400, "导师不存在");
                    }
                    if (t.getTotal() - t.getCount() <= 0) {
                        throw new XException(400, "导师数量已满，请重新选择");
                    }
                });

        Mono<Integer> resultM = userRepository.updateTeacherCount(tid)
                .doOnSuccess(r -> {
                    if (r == 0) {
                        throw new XException(400, "导师数量已满，请重新选择");
                    }
                });

        return studentM.flatMap(s ->
                teacherM.flatMap(t ->
                                resultM.flatMap(r -> Mono.just(t.getName())))
                        .flatMap(name -> {
                            s.setTeacherName(name);
                            s.setTeacherId(tid);
                            s.setSelectTime(LocalDateTime.now());
                            return userRepository.save(s);
                        }));

        /*return userRepository.findByIdForUpdate(sid)
                .doOnSuccess(s -> {
                    if (s == null) {
                        throw new XException(400, "学生不存在");
                    }
                    if (s.getTeacherId() != null) {
                        throw new XException(400, "导师不可重复选择");
                    }
                }).flatMap(s -> userRepository.findById(tid)
                        .doOnSuccess(t -> {
                            if (t == null) {
                                throw new XException(400, "导师不存在");
                            }
                            if (t.getTotal() - t.getCount() <= 0) {
                                throw new XException(400, "导师数量已满，请重新选择");
                            }
                        }).flatMap(t -> userRepository.updateTeacherCount(tid)
                                .doOnSuccess(r -> {
                                    if (r == 0) {
                                        throw new XException(400, "导师数量已满，请重新选择");
                                    }
                                })
                                .flatMap(r -> Mono.just(t.getName()))
                        ).flatMap(name -> {
                            s.setTeacherName(name);
                            s.setTeacherId(tid);
                            return userRepository.save(s);
                        }));*/
    }
}
