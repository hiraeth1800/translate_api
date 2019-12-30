package com.example.demo_translate_api.core.services.api;

import com.example.demo_translate_api.core.model.Language;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface LanguageService {
    List<String> getLanguages();
    ConcurrentHashMap<String, String> getLanguageByLocale(String locale);
    Language addLanguage(String locale);
    String removeLanguage(String locale);

    List<Language> findAll();
    Language findByLocale(String locale);
    Language save(Language language);
}
