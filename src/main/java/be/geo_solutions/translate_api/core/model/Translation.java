package be.geo_solutions.translate_api.core.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "key")
    private String key;
    @Column(name = "value")
    private String value;
    @ManyToOne(targetEntity = Language.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Translation that = (Translation) o;
        return key.equals(that.key) &&
                language.equals(that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, language);
    }

    @Override
    public String toString() {
        return String.format("{\"key\": \"%s\", \"value\": \"%s\"}", key, value);
    }
}
