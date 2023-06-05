package com.example.course.repository;

import com.example.course.entity.Dictionary;
import com.example.course.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
    List<Dictionary> findAllByWordsIn(Set<Word> words);
}
