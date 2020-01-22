package be.geo_solutions.translate_api.core.model;

import javax.persistence.*;

@Entity
@Table
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long translationId;
    private String key;
    private String value;
    @ManyToOne(targetEntity = Language.class, fetch = FetchType.EAGER, optional = false)
    private Language language;

    public Translation() {
    }

    public Translation(Language language, String key) {
        this(language, key, "");
    }

    public Translation(Language language, String key, String value) {
        this.language = language;
        this.key = key;
        this.value = value;
    }

    public Long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(Long translationId) {
        this.translationId = translationId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return String.format("{\"key\": \"%s\", \"value\": \"%s\"}", key, value);
    }
}
