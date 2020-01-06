package com.example.demo_translate_api.core.services.impl;

import com.example.demo_translate_api.core.dto.KeyDTO;
import com.example.demo_translate_api.core.dto.TranslationDTO;
import com.example.demo_translate_api.core.gateways.TranslationGateway;
import com.example.demo_translate_api.core.model.Language;
import com.example.demo_translate_api.core.model.Translation;
import com.example.demo_translate_api.core.services.api.LanguageService;
import com.example.demo_translate_api.core.services.api.TranslationService;

import com.example.demo_translate_api.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TranslationServiceImpl implements TranslationService {

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
            LOGGER.warn("No key " + keyDTO.getKey() + " found");
            throw new KeyNotFoundException("No key " + keyDTO.getKey() + " found");
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
                LOGGER.warn("Translation with key " + translationDTO.getKey() + " already exists");
                throw new DuplicateTranslationException("Translation with key " + translationDTO.getKey() + " already exists");
            }
        });
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
                translation.setValue(translationDTO.getTranslation());
                translationGateway.save(translation);
                return translationDTO;
            }
        }
        LOGGER.warn("No key " + translationDTO.getKey() + " found");
        throw new KeyNotFoundException("No key " + translationDTO.getKey() + " found");
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
            translationGateway.deleteById(trans.get().getTranslationId());
            return new TranslationDTO(keyDTO.getLocale(), keyDTO.getKey(), trans.get().getValue());
        }
        LOGGER.warn("No key " + keyDTO.getLocale() + " found");
        throw new KeyNotFoundException("No key " + keyDTO.getKey() + " found");
    }

    @Override
    public Translation save(Translation translation) {
        return translationGateway.save(translation);
    }

    @Override
    public Translation deleteById(int translationId) {
        return translationGateway.deleteById(translationId);
    }

    @Override
    public List<Translation> findByKey(String key) {
        return translationGateway.findByKey(key);
    }
}
