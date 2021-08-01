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
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;
    @NotNull
    private String firstName;
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private String phone;
    private String userStatus;
    private Date createdAt;
    private Date updatedAt;

}
