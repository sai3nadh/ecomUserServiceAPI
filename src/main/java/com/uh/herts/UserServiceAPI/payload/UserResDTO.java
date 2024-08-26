package com.uh.herts.UserServiceAPI.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResDTO {

    private int userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
}
