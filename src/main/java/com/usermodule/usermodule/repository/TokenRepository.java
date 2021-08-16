package com.usermodule.usermodule.repository;

import com.usermodule.usermodule.dto.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("SELECT u FROM Token u WHERE u.user_id = :user_id")
    List<Token> findByUserId(@Param("user_id") int user_id);
}
