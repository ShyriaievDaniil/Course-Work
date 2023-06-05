package com.example.course.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;



@Entity
public class Word {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String wordInfo;
    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Translation> translations=new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWordInfo() {
        return wordInfo;
    }

    public void setWordInfo(String info) {
        wordInfo = info;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }

}
