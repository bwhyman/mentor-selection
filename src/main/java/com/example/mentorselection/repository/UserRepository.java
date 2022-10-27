package com.example.mentorselection.repository;

import com.example.mentorselection.entity.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    @Query("select * from user u where u.number=:number;")
    Mono<User> find(String number);

    @Query("select * from user u where u.role=:role;")
    Flux<User> list(int role);

    @Query("select * from user u where u.id=:id for update;")
    Mono<User> findByIdForUpdate(long id);

    @Modifying
    @Query("update user u set u.count=u.count+1 where u.total-u.count>0 and id=:tid")
    Mono<Integer> updateTeacherCount(long tid);

    @Query("select * from user u where u.role=0 and isnull(u.teacher_id);")
    Flux<User> listStudentsByUnselected();

    @Query("select * from user u where u.teacher_id=:tid and u.role=0;")
    Flux<User> listStudentsByTid(long tid);

    @Modifying
    @Query("update user u set u.password=:password where u.number=:number")
    Mono<Integer> updatePassword(String number, String password);

    @Modifying
    @Query("update user u set u.password=:password where u.id=:uid")
    Mono<Integer> updatePassword(long uid, String password);

    @Modifying
    @Query("update user u set u.teacher_id = null, u.teacher_name = null, u.select_time = null where u.role = 0 " +
            "and !isnull(u.teacher_id)")
    Mono<Integer> updateStudentsByTeacherNull();

    @Modifying
    @Query("update user u set u.count = 0 where u.role = 1 and u.count != 0")
    Mono<Integer> updateTeachersByCount();
}
