package com.example.course.service;

import com.example.course.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
@Service
public class UserValidationService implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user=(User) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"username","user.username.null", "Username is required");
        if(user.getUsername().length()<4 || user.getUsername().length()>32){
            errors.rejectValue("username", "user.username.length","Username must be 4-32 characters long");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password","user.password.null", "Password is required");
        if(user.getPassword().length()<8 || user.getPassword().length()>32){
            errors.rejectValue("password", "user.password.length","Password must be 8-32 characters long");
        }
        if (!user.getPassword().equals(user.getConfirmPassword())){
            errors.rejectValue("confirmPassword", "user.password.match", "Passwords don't match");
        }
    }

}
