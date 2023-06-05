package com.example.course.repository;

import com.example.course.entity.Language;
import com.example.course.entity.Translation;
import com.example.course.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findAllByName(String name);

    @Query("SELECT fromTranslation.name, toTranslation.name, fromTranslation.word.id FROM Translation AS fromTranslation " +
            "INNER JOIN Translation AS toTranslation " +
            "ON fromTranslation.word=toTranslation.word " +
            "AND fromTranslation.word IN ?1 AND fromTranslation.language=?2 AND toTranslation.language=?3")
    String[][] getTranslations(List<Word> word, Language fromLanguage, Language toLanguage);

}
