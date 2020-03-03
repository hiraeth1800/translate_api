package be.geo_solutions.translate_api.core.dto;

import javax.validation.constraints.NotNull;

public class TranslationDTO {
    @NotNull
    private String locale;
    @NotNull
    private String key;
    @NotNull
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

    @Override
    public String toString() {
        return "TranslationDTO{" +
                "locale='" + locale + '\'' +
                ", key='" + key + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }
}
