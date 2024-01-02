package com.moneyyummy.coreservice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserModel {
    private Long id;
    private String nickname;
    private String email;
    private String job;
    private String role;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private String provider;
    private String providerId;
}
