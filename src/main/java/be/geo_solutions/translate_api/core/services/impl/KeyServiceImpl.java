package be.geo_solutions.translate_api.core.services.impl;

import be.geo_solutions.translate_api.core.model.Language;
import be.geo_solutions.translate_api.core.model.Translation;
import be.geo_solutions.translate_api.core.services.api.KeyService;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.core.services.api.TranslationService;
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

    /**
     * @return  A distinct list of all the keys.
     */
    public List<String> getKeys() {
        Set<String> keys = new HashSet<>();
        languageService.findAll().forEach(language -> {
            language.getTranslations().forEach(translation -> {
                keys.add(translation.getKey());
            });
        });
        return keys.stream().sorted().collect(Collectors.toList());
    }

    /**
     * Returns
     * @LanguageNotFoundException No language is found with the locale. (languageService.findByLocale)
     * @param locale the specific language
     * @return A list of all the keys from a specific language.
     */
    @Override
    public List<String> getKeysByLocale(String locale) {
        return languageService.findByLocale(locale)
                .getTranslations()
                .stream().
                        map(Translation::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * @return A map with the languages as key and the missing keys in the list of values.
     */
    @Override
    public ConcurrentHashMap<String, List<String>> getMissingKeys() {
        ConcurrentHashMap<String, List<String>> missingKeys = new ConcurrentHashMap<>();
        languageService.findAll().forEach(language -> {
            Set<String> allKeys = new HashSet<>(getKeys());
            Set<String> keys = new HashSet<>(getKeysByLocale(language.getLocale()));
            allKeys.removeAll(keys);
            if (allKeys.size() > 0) {
                missingKeys.put(language.getLocale(), new ArrayList<>(allKeys));
            }
        });
        return missingKeys;
    }

    /**
     * For every language translations will be created for the missing keys. All new translations will have empty values
     * @return The languages that have been updated.
     */
    @Override
    public List<String> updateKeys() {
        List<String> updatedLanguages = new ArrayList<>();
        List<String> keys = getKeys();
        List<Language> languages = languageService.findAll();
        for (Language language : languages) {
            for (String key : keys) {
                boolean keyFound = false;
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

    /**
     * Translations will be created for the missing keys from a specific language. All new translations will have empty values
     * @LanguageNotFoundException No language is found with the locale. (languageService.findByLocale)
     * @param locale the specific language
     * @return The keys that have been generated for that language.
     */
    public List<String> updateKeysByLocale(String locale) {
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

    /**
     * If a key is not existing it will be ignored.
     * @param keys the keys that will be deleted
     * @return The keys that have been deleted.
     */
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
                        translationService.deleteById(translation.get().getId());
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

    /**
     * @KeyNotFoundException When there is no translation with that key. (translationService.findByKey)
     * @param key the key that will be deleted
     * @return The key that have been deleted.
     */
    @Override
    public String deleteKey(String key) {
        List<Translation> translations = translationService.findByKey(key.toUpperCase());
        for (Translation translation : translations) {
            translation.getLanguage().getTranslations().remove(translation);
            translationService.deleteById(translation.getId());
        }
        return key;
    }

    /**
     * @param key the key that will be created
     * @return A list of the languages for which a translation with the key has been created.
     */
    @Override
    public List<String> addKey(String key) {
        List<String> locales = new ArrayList<>();
        List<Language> languages = languageService.findAll();
        languages.forEach(l -> {
            if (!l.getTranslations()
                    .stream()
                    .map(Translation::getKey)
                    .collect(Collectors.toList())
                    .contains(key)) {
                locales.add(l.getLocale());
                translationService.save(new Translation(l, key.toUpperCase()));
            }
        });
        return locales;
    }
}
