package be.geo_solutions.translate_api.core.dto;

public class KeyDTO {
    private String locale;
    private String key;

    public KeyDTO() {
    }

    public KeyDTO(String locale, String key) {
        this.locale = locale;
        this.key = key;
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
}
