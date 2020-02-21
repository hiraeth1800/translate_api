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
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class TranslationServiceImplTest {

    @Mock
    TranslationGateway translationGateway;
    @Mock
    LanguageService languageService;

    TranslationService translationService;

    String testLocale;
    String testKey;
    String unknownKey;
    String testTranslation;
    KeyDTO keyDTO;
    TranslationDTO translationDTO;

    @BeforeEach
    void setup() {
        translationService = new TranslationServiceImpl(translationGateway, languageService);
        testLocale = "en";
        testKey = "KEY1";
        unknownKey = "UNKNOWN.KEY";
        testTranslation = "translation 1";
        Language l = new Language(testLocale);
        List<Translation> translations = new ArrayList<>();
        translations.add(new Translation(l, testKey, testTranslation));
        translations.get(0).setId(1L);
        l.setTranslations(translations);
        when(languageService.findByLocale(testLocale))
                .thenReturn(l);
        keyDTO = new KeyDTO(testLocale, testKey);
        translationDTO = new TranslationDTO(testLocale, testKey, "new translation");
    }

    @Test
    void getTranslation() {
        assertEquals(TranslationDTO.class, translationService.getTranslation(keyDTO).getClass());
        assertEquals(testLocale, translationService.getTranslation(keyDTO).getLocale());
        assertEquals(testKey, translationService.getTranslation(keyDTO).getKey());
        assertEquals(testTranslation, translationService.getTranslation(keyDTO).getTranslation());
    }

    @Test
    void getTranslationWithKeyNotFoundException() {
        keyDTO.setKey(unknownKey);
        assertThrows(KeyNotFoundException.class, () -> {
            translationService.getTranslation(keyDTO);
        });
    }

    @Test
    void addTranslation() {
        when(translationGateway.save(any()))
                .thenReturn(any());
        TranslationDTO translationDTO = new TranslationDTO(testLocale, "NEW.KEY", "new translation");

        assertEquals(translationDTO.getClass(), translationService.addTranslation(translationDTO).getClass());
        assertEquals(translationDTO.getLocale(), translationService.addTranslation(translationDTO).getLocale());
        assertEquals(translationDTO.getKey(), translationService.addTranslation(translationDTO).getKey());
        assertEquals(translationDTO.getTranslation(), translationService.addTranslation(translationDTO).getTranslation());
        // TODO test if size is changed but how?
        assertEquals(1, languageService.findByLocale(testLocale).getTranslations().size());
    }

    @Test
    void addTranslationWithDuplicateTranslationException() {
        when(translationGateway.save(any()))
                .thenReturn(any());
        assertThrows(DuplicateTranslationException.class, () -> {
            translationService.addTranslation(translationDTO);
        });
    }

    @Test
    void updateTranslation() {

        Language l = new Language(testLocale);
        l.setTranslations(Arrays.asList(new Translation(l, testKey, "new translation")));
        when(translationGateway.save(l.getTranslations().get(0)))
                .thenReturn(l.getTranslations().get(0));
        translationService.updateTranslation(translationDTO);
        verify(translationGateway).save(l.getTranslations().get(0));
    }

    @Test
    void updateTranslationWithKeyNotFoundException() {
        when(translationGateway.save(any()))
                .thenReturn(any());
        translationDTO.setKey(unknownKey);
        assertThrows(KeyNotFoundException.class, () -> {
            translationService.updateTranslation(translationDTO);
        });
    }

    @Test
    void deleteTranslation() {
        TranslationDTO deletedTranslationDTO = translationService.deleteTranslation(keyDTO);
        assertEquals(testLocale, deletedTranslationDTO.getLocale());
        assertEquals(testKey, deletedTranslationDTO.getKey());
        assertEquals(testTranslation, deletedTranslationDTO.getTranslation());
        verify(translationGateway).deleteById(1L);
    }

    @Test
    void deleteTranslationKeyNotFoundException() {
        keyDTO.setKey(unknownKey);
        assertThrows(KeyNotFoundException.class, () -> {
            translationService.deleteTranslation(keyDTO);
        });
    }
}
