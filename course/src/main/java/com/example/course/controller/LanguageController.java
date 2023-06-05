package com.example.course.controller;

import com.example.course.entity.Language;
import com.example.course.entity.User;
import com.example.course.repository.LanguageRepository;
import com.example.course.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/language")
public class LanguageController {
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private UserService userService;
    @GetMapping({"/edit/{id}", "/new"})
    private String editLanguage(@PathVariable(required = false) Long id, Model model) {
        Language language;
        if (Objects.isNull(id)) {
            language = new Language();
        } else {
            Optional<Language> languageOptional = languageRepository.findById(id);
            if (languageOptional.isPresent()) {
                language = languageOptional.get();
            } else {
                model.addAttribute("message", "This language isn't exist");
                return "error";
            }
        }
        model.addAttribute(language);
        return "language_edit";
    }
    @GetMapping("/info/{name}")
    private String languageInfo(@PathVariable String name, Model model){
        Language language=languageRepository.findByName(name);
        if (Objects.isNull(language)){
            model.addAttribute("message", "This language isn't exist");
            return "error";
        }
        else{
            model.addAttribute(language);
            return "language_info";
        }
    }
    @GetMapping("/all")
    private String allLanguages(Model model, Principal principal){
        User user=userService.getUser(principal);
        if(user!=null){
            model.addAttribute(user);
        }
        model.addAttribute(languageRepository.findAll());
        return "language_list";
    }
    @PostMapping("/save")
    public String saveLanguage(@ModelAttribute Language language, HttpServletRequest request){
        if(language.getName().isEmpty() || language.getInfo().isEmpty()){
            return "redirect:"+request.getHeader("Referer");
        }
        languageRepository.save(language);
        return "redirect:/language/info/"+language.getName();
    }
}
