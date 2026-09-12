package com.college.wallet.dto;
import com.college.wallet.model.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String phoneNumber;
    @NotBlank(message="Password must not be null")
    @Size(min=6,message="Password must be atleast 6 charcters")
    private String password;
    private Role role;
    @NotBlank(message="Password must not be null")
    @Size(min=6,message="Password must be atleast 6 charcters")
    private String transactionPin;
}
