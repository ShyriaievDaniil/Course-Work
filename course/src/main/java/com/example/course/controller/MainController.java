package com.example.course.controller;

import com.example.course.entity.Dictionary;
import com.example.course.entity.User;
import com.example.course.entity.Word;
import com.example.course.repository.*;
import com.example.course.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;


@Controller
@RequestMapping("/")
public class MainController {
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private TranslationRepository translationRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private UserService userService;

    @GetMapping({"/", "/{id}"})
    public String mainPage(@RequestParam(required = false) String from, @RequestParam(required = false) String to,
                        @CookieValue(value = "from", required = false) String from_cookie,
                        @CookieValue(value = "to", required = false) String to_cookie,
                        @CookieValue(value = "selected", required = false) String selected,
                        @PathVariable(required = false) Long id,
                        Model model, HttpServletResponse response, HttpServletRequest request, Principal principal) {
        if (from == null || to == null) {
            if (from_cookie == null || to_cookie == null) {
                from = "Ukrainian";
                to = "English";
            } else {
                from = from_cookie;
                to = to_cookie;
            }
        }
        Cookie cookie1 = new Cookie("from", from);
        Cookie cookie2 = new Cookie("to", to);
        cookie1.setMaxAge(1800);
        cookie2.setMaxAge(1800);
        response.addCookie(cookie1);
        response.addCookie(cookie2);

        User user = userService.getUser(principal);
        model.addAttribute("user", user);

        List<Word> words;
        if (id == null) {
            words = wordRepository.findAll();
        } else {
            Optional<Dictionary> dictionary = dictionaryRepository.findById(id);
            if (dictionary.isPresent()) {
                if (dictionary.get().getPersonal() && (Objects.isNull(user) || !dictionary.get().getOwner().equals(user))) {
                    model.addAttribute("message", "This dictionary is private");
                    return "error";
                } else {
                    words = new ArrayList<>(dictionary.get().getWords());
                    model.addAttribute("name", dictionary.get().getName());
                    model.addAttribute("owner", dictionary.get().getOwner().getUsername());
                    model.addAttribute("dictionary_id", dictionary.get().getId());
                }
            } else {
                model.addAttribute("message", "This dictionary isn't exist");
                return "error";
            }
        }
        String[][] dictionary = translationRepository.getTranslations(words,
                languageRepository.findByName(from), languageRepository.findByName(to));
        Arrays.sort(dictionary, Comparator.comparing(t -> t[0]));
        if (dictionary.length > 0) {
            model.addAttribute("dictionary", dictionary);
        }
        model.addAttribute("selected", selected);
        model.addAttribute("url", request.getRequestURL().toString());
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("languages", languageRepository.findAll());

        return "main_page";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            request.getSession().invalidate();
        }
        return "redirect:/";
    }
}
