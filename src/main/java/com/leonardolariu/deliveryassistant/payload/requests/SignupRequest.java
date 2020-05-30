package com.leonardolariu.deliveryassistant.payload.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "Username cannot be blank.")
    private String username;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters long.")
    private String password;

    private Set<String> role;
}