package com.usermodule.usermodule.repository;

import com.usermodule.usermodule.dto.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {

    @Query("SELECT u FROM Otp u WHERE u.user_id = :user_id")
    List<Otp> findByUserId(@Param("user_id") int user_id);
}
