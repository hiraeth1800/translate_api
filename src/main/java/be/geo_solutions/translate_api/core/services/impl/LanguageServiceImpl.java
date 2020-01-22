package be.geo_solutions.translate_api.core.services.impl;

import be.geo_solutions.translate_api.exceptions.LanguageNotFoundException;
import be.geo_solutions.translate_api.core.gateways.LanguageGateway;
import be.geo_solutions.translate_api.core.model.Language;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.exceptions.DuplicateLanguageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LanguageServiceImpl implements LanguageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageServiceImpl.class);
    private final LanguageGateway languageGateway;

    public LanguageServiceImpl(LanguageGateway languageGateway) {
        this.languageGateway = languageGateway;
    }

    /**
     * @return A list of locales.
     */
    @Override
    public List<String> getLanguages() {
        return languageGateway
                .findAll()
                .stream()
                .map(Language::getLocale)
                .collect(Collectors.toList());
    }

    /**
     * @LanguageNotFoundException  No language is found with the locale. (findByLocale)
     * @param locale The locale from the requested language.
     * @return A map containing keys as keys and translations as values.
     */
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

    /**
     * @DuplicateLanguageException  A language with the locale already exists.
     * @param locale The locale from the requested language.
     * @return The language object that has been created.
     */
    @Override
    public Language addLanguage(String locale) {
        try {
            findByLocale(locale);
            throw new DuplicateLanguageException("Language with locale " + locale + " already exists.");
        } catch (LanguageNotFoundException e) {
            return languageGateway.save(new Language(locale));
        }

    }

    /**
     * @param locale The locale from the language to delete.
     * @return The locale from the deleted language.
     */
    @Override
    public String removeLanguage(String locale) {
        languageGateway
                .deleteById(findByLocale(locale).getId());
        return locale;
    }

    @Override
    public List<Language> findAll() {
        return languageGateway.findAll();
    }

    /**
     * @LanguageNotFoundException  No language is found with the locale.
     */
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
