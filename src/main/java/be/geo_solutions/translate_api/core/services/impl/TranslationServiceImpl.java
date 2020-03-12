package be.geo_solutions.translate_api.core.services.impl;

import be.geo_solutions.translate_api.core.dto.KeyDTO;
import be.geo_solutions.translate_api.core.dto.TranslationDTO;
import be.geo_solutions.translate_api.core.gateways.TranslationGateway;
import be.geo_solutions.translate_api.core.model.Language;
import be.geo_solutions.translate_api.core.model.Translation;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.core.services.api.TranslationService;
import be.geo_solutions.translate_api.exceptions.DuplicateTranslationException;
import be.geo_solutions.translate_api.exceptions.KeyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TranslationServiceImpl implements TranslationService {

    /**
     * Use properties via getters for tests to succeed.
     * Mocks can't instantiate attributes from application.properties
     * Workaround: use via getters and let mock return property value in getter
     */
    @Value("${message.error.key-not-found}")
    private String keyNotFoundMessage;

    @Value("${message.error.duplicate-translation}")
    private String duplicateTranslationMessage;

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationServiceImpl.class);
    private final TranslationGateway translationGateway;
    private final LanguageService languageService;

    public TranslationServiceImpl(TranslationGateway translationGateway, LanguageService languageService) {
        this.translationGateway = translationGateway;
        this.languageService = languageService;
    }

    /**
     * @KeyNotFoundException No language is found with the locale. (languageService.findByLocale)
     * @param keyDTO A dto object containing a locale and a key.
     * @return A dto object containing the locale, key and translation.
     */
    @Override
    public TranslationDTO getTranslation(KeyDTO keyDTO) {
        Language language = languageService.findByLocale(keyDTO.getLocale());
        Optional<Translation> translation = language.getTranslations()
                .stream()
                .filter(t -> {
                    return t.getKey().equals(keyDTO.getKey().toUpperCase());
                })
                .findFirst();
        if (translation.isPresent()) {
            return new TranslationDTO(translation.get().getLanguage().getLocale(),
                    translation.get().getKey(), translation.get().getValue());
        } else {
            String message = "No key " + keyDTO.getKey() + " found for locale " + keyDTO.getLocale();
            LOGGER.warn(message);
            throw new KeyNotFoundException(message);
        }
    }

    /**
     * @DuplicateTranslationException A translation already exists with the key.
     * @LanguageNotFoundException  No language is found with the locale. (languageService.findByLocale)
     * @param translationDTO A dto object containing the locale, key and translation.
     * @return A dto object for the created translation containing the locale, key and translation.
     */
    @Override
    public TranslationDTO addTranslation(TranslationDTO translationDTO) {
        Language language = languageService.findByLocale(translationDTO.getLocale());
        language.getTranslations().forEach(translation -> {
            if (translation.getKey().equals(translationDTO.getKey().toUpperCase())) {
                String message = "Translation with key " + translationDTO.getKey() + " already exists";
                LOGGER.warn(message);
                throw new DuplicateTranslationException(message);
            }
        });
        if (translationDTO.getTranslation() == null) {
            translationDTO.setTranslation("");
        }
        Translation translation = new Translation(language, translationDTO.getKey().toUpperCase(), translationDTO.getTranslation());
        translationGateway.save(translation);
        return translationDTO;
    }

    /**
     * @KeyNotFoundException  The language doesn't have a translation with the given key.
     * @LanguageNotFoundException  No language is found with the locale. (languageService.findByLocale)
     * @param translationDTO A dto object containing the locale, key and translation.
     * @return A dto object for the updated translation containing the locale, key and translation.
     */
    @Override
    public TranslationDTO updateTranslation(TranslationDTO translationDTO) {
        Language language = languageService.findByLocale(translationDTO.getLocale());
        for (Translation translation : language.getTranslations()) {
            if (translation.getKey().equals(translationDTO.getKey().toUpperCase())) {
                if (translationDTO.getTranslation() == null) {
                    translation.setValue("");
                } else {
                    translation.setValue(translationDTO.getTranslation());
                }
                translationGateway.save(translation);
                return translationDTO;
            }
        }
        String message = "No key " + translationDTO.getKey() + " found for locale " + translationDTO.getLocale();
        LOGGER.warn(message);
        throw new KeyNotFoundException(message);
    }

    /**
     * @KeyNotFoundException  The language doesn't have a translation with the given key.
     * @LanguageNotFoundException  No language is found with the locale. (languageService.findByLocale)
     * @param keyDTO A dto object containing the locale and key.
     * @return A dto object for the deleted translation containing the locale, key and translation.
     */
    @Override
    public TranslationDTO deleteTranslation(KeyDTO keyDTO) {
        Language language = languageService.findByLocale(keyDTO.getLocale());
        Optional<Translation> trans = language.getTranslations()
                .stream()
                .filter(translation -> translation.getKey().equals(keyDTO.getKey().toUpperCase()))
                .findFirst();
        if (trans.isPresent()) {
            language.getTranslations().remove(trans.get());
            translationGateway.deleteById(trans.get().getId());
            return new TranslationDTO(keyDTO.getLocale(), keyDTO.getKey(), trans.get().getValue());
        }
        String message = "No key " + keyDTO.getKey() + " found for locale " + keyDTO.getLocale();
        LOGGER.warn(message);
        throw new KeyNotFoundException(message);
    }

    @Override
    public Translation save(Translation translation) {
        return translationGateway.save(translation);
    }

    @Override
    public Translation deleteById(Long translationId) {
        Optional<Translation> byId = translationGateway.findById(translationId);
        if (byId.isPresent()) {
            translationGateway.deleteById(translationId);
            return byId.get();
        }
        return null;
    }

    @Override
    public List<Translation> findByKey(String key) {
        return translationGateway.findByKey(key);
    }

}
