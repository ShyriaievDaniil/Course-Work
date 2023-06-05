package com.example.course.controller;

import com.example.course.entity.Translation;
import com.example.course.entity.User;
import com.example.course.entity.Word;
import com.example.course.repository.LanguageRepository;
import com.example.course.repository.TranslationRepository;
import com.example.course.service.UserService;
import com.example.course.service.WordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/word")
public class WordController {
    @Autowired
    private WordService wordService;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private TranslationRepository translationRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    private String getWord(@PathVariable Long id, Model model, Principal principal) {
        Optional<Word> word = wordService.getWord(id);
        if (word.isPresent()) {
            User user=userService.getUser(principal);
            if(user!=null){
                model.addAttribute(user);
            }
            model.addAttribute("word", word.get());
            return "word_info";
        } else {
            model.addAttribute("message", "The word isn't exist");
            return "error";
        }
    }
    @GetMapping({"/edit/{id}", "/new"})
    private String editWord(@PathVariable(required = false) Long id, Model model) {
        Word word;
        if (Objects.isNull(id)) {
            word = new Word();
        } else {
            Optional<Word> wordOptional = wordService.getWord(id);
            if (wordOptional.isPresent()) {
                word = wordOptional.get();
            } else {
                model.addAttribute("message", "The word isn't exist");
                return "error";
            }
        }
        word.setTranslations(wordService.newTranslations(word));
        model.addAttribute(word);
        model.addAttribute("languages", languageRepository.findAll());
        return "word_edit";
    }
    @GetMapping("/delete/{id}")
    private String deleteWord(@PathVariable Long id, HttpServletRequest request){
        wordService.deleteWord(id);
        return "redirect:"+request.getHeader("Referer");
    }
    @GetMapping("/search")
    private String searchWord(@RequestParam String name, Model model){
        List<Translation> translations=translationRepository.findAllByName(name);
        if(translations.size()==0){
            model.addAttribute("message", "No words found");
            return "error";
        }
        else if(translations.size()==1){
            return "redirect:/word/"+translations.get(0).getWord().getId();
        }
        else{
            model.addAttribute(translations);
            return "word_list";
        }
    }
    @PostMapping("/save")
    private String saveWord(@ModelAttribute Word word, Model model, HttpServletRequest request) {
        List<Translation> remove = new ArrayList<>();
        for (Translation translation : word.getTranslations()) {
            if (translation.getName().equals("")) {
                remove.add(translation);
            } else {
                translation.setWord(word);
                translation.setLanguage(languageRepository.findByName(translation.getLanguage().getName()));
            }
        }
        for (Translation translation : remove) {
            word.getTranslations().remove(translation);
        }
        if(Objects.isNull(word.getWordInfo()) || word.getTranslations().size()==0){
            model.addAttribute("error");
            return "redirect:"+request.getHeader("Referer");
        }
        wordService.saveWord(word);
        model.addAttribute(word);
        return "word_info";
    }
}
