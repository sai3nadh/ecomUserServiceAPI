package com.uh.herts.UserServiceAPI.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResDTO {

    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
