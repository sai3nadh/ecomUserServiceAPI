package com.uh.herts.UserServiceAPI.payload;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
}
