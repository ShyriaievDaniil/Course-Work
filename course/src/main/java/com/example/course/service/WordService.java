package com.example.course.service;

import com.example.course.entity.Dictionary;
import com.example.course.entity.Language;
import com.example.course.entity.Translation;
import com.example.course.entity.Word;
import com.example.course.repository.DictionaryRepository;
import com.example.course.repository.LanguageRepository;
import com.example.course.repository.TranslationRepository;
import com.example.course.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WordService {
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    public void saveWord(Word word){
        wordRepository.save(word);
    }
    public void deleteWord(Long id){
        Optional<Word> wordOptional=getWord(id);
        if(wordOptional.isPresent()){
            Word word=wordOptional.get();
            List<Dictionary> dictionaries=dictionaryRepository.findAllByWordsIn(
                    Stream.of(word).collect(Collectors.toSet()));
            for(Dictionary dictionary:dictionaries){
                dictionary.getWords().remove(word);
                dictionaryRepository.save(dictionary);
            }
            wordRepository.delete(word);
        }
    }
    public Optional<Word> getWord(Long id){
        return wordRepository.findById(id);
    }
    public List<Translation> newTranslations(Word word){
        List<Translation> translations=word.getTranslations();
        List<Language> languages=languageRepository.findAll();
        languages.removeAll(translations.stream().map(Translation::getLanguage).toList());
        translations.addAll(languages.stream().map(Translation::new).toList());
        return translations;
    }
}
