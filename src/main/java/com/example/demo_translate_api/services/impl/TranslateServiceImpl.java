package com.example.demo_translate_api.services.impl;
import com.example.demo_translate_api.dto.TranslationDTO;
import com.example.demo_translate_api.model.Language;
import com.example.demo_translate_api.services.api.TranslateService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TranslateServiceImpl implements TranslateService {

    private List<Language> languages;

    public TranslateServiceImpl() {
        languages = new ArrayList<>();
        languages.add(new Language("en"));
        languages.get(0).addOrUpdateTranslation("HOME", "Home");
        languages.get(0).addOrUpdateTranslation("EDIT", "Edit");
        languages.get(0).addOrUpdateTranslation("DESCRIPTION", "This text should be translated!");
        languages.get(0).addOrUpdateTranslation("LANGUAGE", "Language");
        languages.add(new Language("nl"));
        languages.get(1).addOrUpdateTranslation("HOME", "Start");
        languages.get(1).addOrUpdateTranslation("EDIT", "Wijzig");
        languages.get(1).addOrUpdateTranslation("DESCRIPTION", "Deze tekst is vertaald!");
        languages.get(1).addOrUpdateTranslation("LANGUAGE", "Taal");
        languages.add(new Language("fr"));
        languages.get(2).addOrUpdateTranslation("HOME", "Home");
        languages.get(2).addOrUpdateTranslation("EDIT", "Editer");
        languages.get(2).addOrUpdateTranslation("DESCRIPTION", "Ce texte est traduit!");
        languages.get(2).addOrUpdateTranslation("LANGUAGE", "Langue");
    }

    @Override
    public void addLanguage(String locale) {
        languages.add(new Language(locale));
    }

    @Override
    public ConcurrentHashMap<String, String> getLanguage(String locale) {
        return filterLanguageByLocale(locale)
                .getTranslations();
    }

    @Override
    public String addTranslation(TranslationDTO translationDTO) {
        System.out.println("adding");
        return filterLanguageByLocale(translationDTO.getLocale())
                .getTranslations()
                .putIfAbsent(translationDTO.getKey(), translationDTO.getTranslation());
    }

    @Override
    public void updateTranslation(TranslationDTO translationDTO) {

    }

    @Override
    public void deleteTranslation(TranslationDTO translationDTO) {
        System.out.println("deleting");
        filterLanguageByLocale(translationDTO.getLocale())
                .removeTranslation(translationDTO.getKey());
    }

    private Language filterLanguageByLocale(String locale) {
        return languages.stream()
                .filter(language -> {
                    return language.getLocale().equals(locale);
                })
                .findFirst()
                .get();
    }
}
