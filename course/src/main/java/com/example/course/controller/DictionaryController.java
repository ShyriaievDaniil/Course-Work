package com.example.course.controller;

import com.example.course.entity.Dictionary;
import com.example.course.entity.User;
import com.example.course.repository.DictionaryRepository;
import com.example.course.service.UserService;
import com.example.course.service.WordService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/dictionary")
public class DictionaryController {
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private WordService wordService;
    @Autowired
    private UserService userService;

    @GetMapping({"/new", "/edit/{id}"})
    public String editDictionary(@PathVariable(required = false) Long id, Model model, Principal principal) {
        User user=userService.getUser(principal);
        if(Objects.isNull(user)){
            return "redirect:/login";
        }
        Dictionary dictionary;
        if (id==null) {
            dictionary = new Dictionary();
            dictionary.setOwner(user);
        } else {
            Optional<Dictionary> dictionaryOptional = dictionaryRepository.findById(id);
            if (dictionaryOptional.isPresent()) {
                dictionary = dictionaryOptional.get();
                if(!dictionary.getOwner().equals(user)){
                    model.addAttribute("message", "Only owner can edit this dictionary");
                    return "error";
                }
            } else {
                model.addAttribute("message", "This dictionary isn't exist");
                return "error";
            }
        }
        model.addAttribute(dictionary);
        return "dictionary_edit";
    }
    @GetMapping("/my")
    public String myDictionaries(Model model, Principal principal){
        User user=userService.getUser(principal);
        if(user==null){
            return "redirect:/login";
        }
        else{
            model.addAttribute("dictionaries", user.getDictionaries());
            return "dictionary_list";
        }
    }

    @PostMapping("/save")
    public String saveDictionary(@ModelAttribute Dictionary dictionary) {
        if (!Objects.isNull(dictionary.getId())) {
            dictionary.setWords(dictionaryRepository.findById(dictionary.getId()).get().getWords());
        }
        dictionary.setName(dictionary.getName().replace(" ", "_"));
        dictionary.setOwner(userService.getUserById(dictionary.getOwner().getId()).get());
        dictionaryRepository.save(dictionary);
        return "redirect:/" + dictionary.getId();
    }

    @PostMapping("/save/{word_id}")
    public String saveWord(@PathVariable Long word_id, @RequestParam Long dictionary_id,
                                 HttpServletResponse response, HttpServletRequest request) {
        Dictionary dictionary = dictionaryRepository.findById(dictionary_id).get();
        System.out.println(dictionary.getName());
        Cookie cookie = new Cookie("selected", dictionary.getName());
        System.out.println(dictionary.getName());
        cookie.setMaxAge(1800);
        cookie.setPath("/");
        response.addCookie(cookie);
        dictionary.getWords().add(wordService.getWord(word_id).get());
        dictionaryRepository.save(dictionary);
        return "redirect:"+request.getHeader("Referer");
    }

    @PostMapping("/delete/{id}")
    public String deleteDictionary(@PathVariable Long id, Principal principal, Model model) {
        Optional<Dictionary> dictionaryOptional=dictionaryRepository.findById(id);
        User user=userService.getUser(principal);
        if(dictionaryOptional.isPresent()){
            if(!dictionaryOptional.get().getOwner().equals(user)){
                model.addAttribute("message", "Only owner can edit this dictionary");
                return "error";
            }
            else{
                dictionaryRepository.deleteById(id);
                return "redirect:/";
            }
        }
        else{
            model.addAttribute("message", "This dictionary isn't exist");
            return "error";
        }
    }

    @PostMapping("/{dictionary_id}/delete/{word_id}")
    public String deleteWord(@PathVariable Long dictionary_id, @PathVariable Long word_id, HttpServletRequest request) {
        Dictionary dictionary = dictionaryRepository.findById(dictionary_id).get();
        dictionary.getWords().remove(wordService.getWord(word_id).get());
        dictionaryRepository.save(dictionary);
        return "redirect:"+request.getHeader("Referer");
    }
}
