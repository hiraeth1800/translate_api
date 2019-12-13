package com.example.demo_translate_api.services.api;

import com.example.demo_translate_api.dto.TranslationDTO;
import com.example.demo_translate_api.model.Language;

import java.util.concurrent.ConcurrentHashMap;

public interface TranslateService {
    ConcurrentHashMap<String, String> getLanguage(String locale);
    void addLanguage(String locale);

    String addTranslation(TranslationDTO translationDTO);
    void updateTranslation(TranslationDTO translationDTO);
    void deleteTranslation(TranslationDTO translationDTO);
}
