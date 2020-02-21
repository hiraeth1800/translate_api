package be.geo_solutions.translate_api.core.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "locale")
    private String locale;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = Translation.class, cascade = CascadeType.ALL, mappedBy = "language")
    private List<Translation> translations;

    public Language(String locale) {
        this.locale = locale;
        this.translations = new ArrayList<>();
    }

    public Language() {
        this.translations = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return locale.equals(language.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locale);
    }

    @Override
    public String toString() {
        StringBuilder translationsString = new StringBuilder();
        translations.forEach(translation -> translationsString.append(String.format(", %s", translation)));
        return String.format("{'locale': '%s', 'translations': '%s'}", locale, translationsString.toString());
    }
}
