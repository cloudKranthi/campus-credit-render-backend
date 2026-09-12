package com.college.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
@Getter
@Data
public class PinChangeResponse {
    @NotBlank
    @Size(min=6,max=6)
    private String oldpin;
    @NotBlank
    @Size(min=6,max=6)
    private String newpin;
}
