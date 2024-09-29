package com.uh.herts.UserServiceAPI.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
}
