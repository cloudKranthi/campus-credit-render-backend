package com.college.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
@Getter

public class pinChangeAdminResponse{
@NotBlank

private String  phonenumber;
@NotBlank(message="pin should not be empty")
@Size(min=6, max=6)
private String pin;
}
