package com.example.demo_translate_api.model;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class Language {
    private String locale;
    private ConcurrentHashMap<String, String> translations;

    public Language(String locale) {
        this.locale = locale;
        this.translations = new ConcurrentHashMap<>();
    }

    public Language() {
        this.translations = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, String> getTranslations() {
        return translations;
    }

    public void addOrUpdateTranslation(String key, String translation) {
        if (translations.containsKey(key)) {
            translations.compute(key, (k, value) -> translation);
        } else {
            translations.put(key, translation);
        }
    }

    public void removeTranslation(String key) {
        translations.remove(key);
    }

    public void setTranslations(ConcurrentHashMap<String, String> translations) {
        this.translations = translations;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "{" + locale + "=" + translations.toString() +  "}";
    }
}
