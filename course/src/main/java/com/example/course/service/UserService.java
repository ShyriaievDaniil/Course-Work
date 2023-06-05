package com.example.course.service;

import com.example.course.entity.User;
import com.example.course.entity.UserRole;
import com.example.course.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
    }
    public void createUser(User user){
        if(userRepository.findByEmail(user.getEmail()).isEmpty()){
            user.setActive(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.getRoles().add(UserRole.USER);
            userRepository.save(user);
        }
    }
    public User getUser(Principal principal){
        if(principal==null){
            return null;
        } else {
            return  userRepository.findByUsername(principal.getName()).get();
        }
    }
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }
    public void autoLogin(HttpServletRequest request, String username, String password){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        request.getSession();
        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authentication=authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

