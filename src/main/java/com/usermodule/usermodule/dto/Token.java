package com.usermodule.usermodule.dto;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotNull
    private int userId;
    @NotNull
    private String token;
    @NotNull
    private String status;
    @NotNull
    private Date createdAt;
    @NotNull
    private Date updatedAt;
}
