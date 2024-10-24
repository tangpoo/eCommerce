package com.potato.ecommerce.domain.store.dto;

import static com.potato.ecommerce.global.exception.ExceptionMessage.PASSWORD_NOT_MATCH;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 15)
    @Pattern(regexp = "^[a-zA-Z0-9.!@#$]*$")
    private String password;

    @NotBlank
    @Size(min = 8, max = 15)
    private String validatePassword;

    @NotBlank
    @Size(max = 15)
    private String name;

    @Size(max = 50)
    private String description;

    @NotBlank
    @Pattern(regexp = "^01(0)([0-9]{4})([0-9]{4})$")
    private String phone;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}+$")
    private String businessNumber;

    public void validatePassword(){
        if(!password.equals(validatePassword)){
            throw new ValidationException(PASSWORD_NOT_MATCH.toString());
        }
    }
}
