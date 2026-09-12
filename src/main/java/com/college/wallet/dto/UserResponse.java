package com.college.wallet.dto;
import java.util.UUID;
public record UserResponse(UUID id,String username,String email,String PhoneNumber){}