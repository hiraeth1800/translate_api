package com.example.demo_translate_api.core.services.impl;

import com.example.demo_translate_api.core.model.Language;
import com.example.demo_translate_api.core.model.Translation;
import com.example.demo_translate_api.core.services.api.KeyService;
import com.example.demo_translate_api.core.services.api.LanguageService;
import com.example.demo_translate_api.core.services.api.TranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KeyServiceImpl implements KeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyServiceImpl.class);
    private final LanguageService languageService;
    private final TranslationService translationService;

    public KeyServiceImpl(LanguageService languageService, TranslationService translationService) {
        this.languageService = languageService;
        this.translationService = translationService;
    }

    @Override
    public List<String> getKeys() {
        Set<String> keys = new HashSet<>();
        languageService.findAll().forEach(language -> {
            language.getTranslations().forEach(translation -> {
                keys.add(translation.getKey());
            });
        });
        return keys.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public List<String> getKeys(String locale) {
        return languageService.findByLocale(locale)
                .getTranslations()
                .stream().
                        map(Translation::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public ConcurrentHashMap<String, List<String>> getMissingKeys() {
        ConcurrentHashMap<String, List<String>> missingKeys = new ConcurrentHashMap<>();
        languageService.findAll().forEach(language -> {
            Set<String> allKeys = new HashSet<>(getKeys());
            Set<String> keys = new HashSet<>(getKeys(language.getLocale()));
            allKeys.removeAll(keys);
            if (allKeys.size() > 0) {
                missingKeys.put(language.getLocale(), new ArrayList<>(allKeys));
            }
        });
        return missingKeys;
    }

    @Override
    public List<String> updateKeys() {
        List<String> updatedLanguages = new ArrayList<>();
        List<String> keys = getKeys();
        List<Language> languages = languageService.findAll();
        for (Language language : languages) {
            for (String key : keys) {
                boolean keyFound = false;
                // TODO while better than for with break?
                for (Translation translation : language.getTranslations()) {
                    if (translation.getKey().equals(key)) {
                        keyFound = true;
                        break;
                    }
                }
                if (!keyFound) {
                    updatedLanguages.add(language.getLocale());
                    language.getTranslations().add(new Translation(language, key, ""));
                }
            }
            languageService.save(language);
        }
        return updatedLanguages
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> updateKeys(String locale) {
        List<String> createdKeys = new ArrayList<>();
        Language language = languageService.findByLocale(locale);
        List<String> keys = getKeys();
        for (String key : keys) {
            boolean keyFound = false;
                for (Translation translation : language.getTranslations()) {
                    if (translation.getKey().equals(key)) {
                        keyFound = true;
                        break;
                    }
                }
            if (!keyFound) {
                createdKeys.add(key);
                language.getTranslations().add(new Translation(language, key, ""));
            }
        }
        languageService.save(language);
        return createdKeys;
    }

    @Override
    public List<String> deleteKeys(String[] keys) {
        List<String> deletedKeys = new ArrayList<>();
        Arrays.asList(keys).forEach(key -> {
            languageService.findAll().forEach(language -> {
                Optional<Translation> translation = language.getTranslations()
                        .stream()
                        .filter(t -> t.getKey().equals(key.toUpperCase()))
                        .findFirst();
                translation.ifPresent(value -> {
                    language.getTranslations().remove(value);
                    translationService.deleteById(translation.get().getTranslationId());
                    languageService.save(language);
                    deletedKeys.add(key);
                });
            });
        });
        return deletedKeys
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public String deleteKey(String key) {
        List<Translation> translations = translationService.findByKey(key);
        for (Translation translation : translations) {
            translation.getLanguage().getTranslations().remove(translation);
            translationService.deleteById(translation.getTranslationId());
        }
        return key;
    }

    @Override
    public List<String> addKey(String key) {
        List<String> locales = new ArrayList<>();
        List<Language> languages = languageService.findAll();
        languages.forEach(l -> {
            locales.add(l.getLocale());
            translationService.save(new Translation(l, key));
        });
        return locales;
    }
}
