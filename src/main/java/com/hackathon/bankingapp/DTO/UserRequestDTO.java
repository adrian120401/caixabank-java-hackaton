package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Size(max = 127, message = "Password must be less than 128 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*", 
            message = "Password must contain at least one digit and one special character")
    @Pattern(regexp = "^\\S*$", message = "Password cannot contain whitespace")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email: ${validatedValue}")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}
