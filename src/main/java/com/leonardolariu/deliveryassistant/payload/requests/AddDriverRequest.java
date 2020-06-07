package com.leonardolariu.deliveryassistant.payload.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AddDriverRequest {
    @NotBlank(message = "Name cannot be blank.")
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Invalid email format.")
    private String email;
}
