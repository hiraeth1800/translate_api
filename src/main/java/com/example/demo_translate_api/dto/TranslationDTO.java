package com.example.demo_translate_api.dto;

public class TranslationDTO {
    private String locale;
    private String key;
    private String translation;

    public TranslationDTO() {
    }

    public TranslationDTO(String locale, String key, String translation) {
        this.locale = locale;
        this.key = key;
        this.translation = translation;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
