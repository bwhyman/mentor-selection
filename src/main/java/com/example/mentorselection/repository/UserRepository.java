package com.example.mentorselection.repository;

import com.example.mentorselection.entity.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("select * from user u where u.number=:number;")
    User find(String number);

    @Query("select * from user u where u.role=:role;")
    List<User> list(int role);

    @Query("select * from user u where u.id=:id for update;")
    User findByIdForUpdate(long id);

    @Modifying
    @Query("update user u set u.count=u.count+1 where u.total-u.count>0 and id=:tid")
    int updateTeacherCount(long tid);

    @Query("select * from user u where u.role=0 and u.teacher_id is null;")
    List<User> listStudentsByUnselected();

    @Query("select * from user u where u.teacher_id=:tid and u.role=0;")
    List<User> listStudentsByTid(long tid);

    @Modifying
    @Query("update user u set u.password=:password where u.number=:number")
    int updatePassword(String number, String password);
    @Modifying
    @Query("update user u set u.password=:password where u.id=:uid")
    int updatePassword(long uid, String password);
}
