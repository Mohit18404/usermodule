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
@Table(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotNull
    private int userId;
    @NotNull
    private String otp;
    @NotNull
    private Date createdAt;
    @NotNull
    private Date updatedAt;
    @NotNull
    private int retry;
    @NotNull
    private String status;
}
