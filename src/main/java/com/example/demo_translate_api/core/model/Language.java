package com.example.demo_translate_api.core.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Language {
    @Id
    @SequenceGenerator(name="language_seq",
            sequenceName="language_seq",
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator="language_seq")
    private int languageId;
    private String locale;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = Translation.class, cascade = CascadeType.ALL, mappedBy = "language")
    private List<Translation> translations;

    public Language(String locale) {
        this.locale = locale;
        this.translations = new ArrayList<>();
    }

    public Language() {
        this.translations = new ArrayList<>();
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }

    @Override
    public String toString() {
        StringBuilder translationsString = new StringBuilder();
        translations.forEach(translation -> translationsString.append(String.format(", %s", translation)));
        return String.format("{'locale': '%s', 'translations': '%s'}", locale, translationsString.toString());
    }
}
