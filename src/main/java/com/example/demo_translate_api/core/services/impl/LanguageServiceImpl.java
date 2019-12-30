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
    //private List<Language> languages;

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
        return languageGateway
                .deleteById(findByLocale(locale).getLanguageId())
                .getLocale();
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

    /*
    public ConcurrentHashMap<String, String> getLanguage(String locale) {
        Language2 l = languageRepository.getLanguageByLocale(locale);
        List<Translation> translations = translateService.getTranslationsByLanguage(l);
        ConcurrentHashMap<String, String> language = new ConcurrentHashMap<>();
        translations.forEach(t -> {
            language.put(t.getKey().getKey(), t.getTranslation());
        });
        return language;
    }

    public Language2 getLanguageByLocale2(String locale) {
        return languageRepository.getLanguageByLocale(locale);
    }

    @Override
    public void addLanguage(String locale) {
        try {
            getLanguageByLocale(locale);
        } catch (LanguageServiceException e) {
            languages.add(new Language(locale));
        }
        LOGGER.warn("trying to add existing language.");
        throw new LanguageServiceException("Language " + locale + " already exists!");
    }

    public Language getLanguageByLocale(String locale) {
        Optional<Language> language = languages.stream()
                .filter(lang -> {
                    return lang.getLocale().equals(locale);
                })
                .findFirst();
        if (language.isPresent()) {
            return language.get();
        }
        LOGGER.warn("Language with locale " + locale + " not found!");
        throw new LanguageServiceException("Language with locale " + locale + " not found!");
    }

    @Override
    public List<String> getAllKeys() {
        List<String> keys = languages.stream()
                .map(x -> Collections.list(x.getTranslations().keys()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        LOGGER.info(keys.toString());
        return keys;
    }

    @Override
    public List<String> getKeys(String locale) {
        return Collections.list(getLanguageByLocale(locale).getTranslations().keys());
    }*/
}
