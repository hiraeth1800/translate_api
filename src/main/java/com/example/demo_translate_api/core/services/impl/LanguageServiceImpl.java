package com.example.demo_translate_api.core.services.impl;

import com.example.demo_translate_api.core.gateways.LanguageGateway;
import com.example.demo_translate_api.core.model.Language;
import com.example.demo_translate_api.core.services.api.KeyService;
import com.example.demo_translate_api.core.services.api.LanguageService;
import com.example.demo_translate_api.exceptions.DuplicateLanguageException;
import com.example.demo_translate_api.exceptions.LanguageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@Service
public class LanguageServiceImpl implements LanguageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageServiceImpl.class);
    private final LanguageGateway languageGateway;

    public LanguageServiceImpl(LanguageGateway languageGateway) {
        this.languageGateway = languageGateway;
    }

    @Override
    public List<String> getLanguages() {
        return languageGateway
                .findAll()
                .stream()
                .map(Language::getLocale)
                .collect(Collectors.toList());
    }

    @Override
    public ConcurrentHashMap<String, String> getLanguageByLocale(String locale) {
        ConcurrentHashMap<String, String> translations = new ConcurrentHashMap<>();
        findByLocale(locale)
                .getTranslations()
                .forEach(translation -> {
                    translations.put(translation.getKey(), translation.getValue());
                });
        return translations;
    }

    @Override
    public Language addLanguage(String locale) {
        try {
            findByLocale(locale);
            throw new DuplicateLanguageException("Language with locale " + locale + " already exists.");
        } catch (LanguageNotFoundException e) {
            return languageGateway.save(new Language(locale));
        }

    }

    @Override
    public String removeLanguage(String locale) {
        languageGateway
                .deleteById(findByLocale(locale).getLanguageId());
        return locale;
    }

    @Override
    public List<Language> findAll() {
        return languageGateway.findAll();
    }

    @Override
    public Language findByLocale(String locale) {
        Language language = languageGateway.findByLocale(locale);
        if (language == null) {
            LOGGER.warn("No language found with locale " + locale);
            throw new LanguageNotFoundException("No language found with locale " + locale);
        }
        return language;
    }

    @Override
    public Language save(Language language) {
        return languageGateway.save(language);
    }

}
