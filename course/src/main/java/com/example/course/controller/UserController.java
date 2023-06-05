package com.example.course.controller;

import com.example.course.entity.User;
import com.example.course.repository.UserRepository;
import com.example.course.service.UserService;
import com.example.course.service.UserValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserValidationService userValidationService;

    @GetMapping("/new")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/new")
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, HttpServletRequest request) {
        userValidationService.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        userService.createUser(user);
        userService.autoLogin(request, user.getUsername(), user.getConfirmPassword());
        return "main_page";
    }
}
