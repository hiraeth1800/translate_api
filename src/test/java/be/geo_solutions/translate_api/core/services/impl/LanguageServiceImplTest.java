package be.geo_solutions.translate_api.core.services.impl;

import be.geo_solutions.translate_api.core.gateways.LanguageGateway;
import be.geo_solutions.translate_api.core.model.Language;
import be.geo_solutions.translate_api.core.model.Translation;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.exceptions.DuplicateLanguageException;
import be.geo_solutions.translate_api.exceptions.LanguageNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class LanguageServiceImplTest {

    @Mock
    LanguageGateway languageGateway;

    LanguageService languageService;

    @Test
    @Transactional
    void getLanguages() {
        List<Language> languages = new ArrayList<>();
        languages.add(new Language("en"));
        languages.add(new Language("nl"));
        languages.add(new Language("fr"));
        when(languageGateway.findAll())
                .thenReturn(languages);
        languageService = new LanguageServiceImpl(languageGateway);
        assertEquals(3, languageService.getLanguages().size());
        assertTrue(languageService.getLanguages().contains("en"));
        assertTrue(languageService.getLanguages().contains("nl"));
        assertTrue(languageService.getLanguages().contains("fr"));
        assertFalse(languageService.getLanguages().contains("de"));
    }

    @Test
    @Transactional
    void getLanguageByLocale() {
        LanguageGateway languageGatewayMock = mock(LanguageGateway.class);
        List<Language> languages = new ArrayList<>();

        Language en = new Language("en");
        List<Translation> enTranslations = new ArrayList<>();
        enTranslations.add(new Translation(en, "KEY1", "Translation 1"));
        enTranslations.add(new Translation(en, "KEY2", "Translation 2"));
        en.setTranslations(enTranslations);
        languages.add(en);

        when(languageGatewayMock.findByLocale("en"))
                .thenReturn(languages.get(0));

        languageService = new LanguageServiceImpl(languageGatewayMock);
        Map<String, String> actualEnTranslations = languageService.getLanguageByLocale("en");
        System.out.println(actualEnTranslations);
        assertEquals("Translation 1", actualEnTranslations.get("KEY1"));
        assertEquals("Translation 2", actualEnTranslations.get("KEY2"));
        assertEquals(2, actualEnTranslations.size());
    }

    @Test
    @Transactional
    void AddLanguage() {
        languageService = new LanguageServiceImpl(languageGateway);
        String testLocale = "en";
        Language existingLanguageWithLocale = new Language(testLocale);
        when(languageGateway.findByLocale(testLocale))
                .thenReturn(null);
        when(languageGateway.save(any()))
                .thenReturn(existingLanguageWithLocale);
        assertEquals(existingLanguageWithLocale, languageService.addLanguage(testLocale));
        verify(languageGateway).save(existingLanguageWithLocale);
    }

    @Test
    @Transactional
    void AddLanguageWithDuplicateLanguageException() {
        languageService = new LanguageServiceImpl(languageGateway);
        String testLocale = "en";
        Language existingLanguageWithLocale = new Language(testLocale);
        when(languageGateway.findByLocale(testLocale))
                .thenReturn(existingLanguageWithLocale);
        assertThrows(DuplicateLanguageException.class, () -> {
            languageService.addLanguage(testLocale);
        });
    }

    @Test
    @Transactional
    void deleteLanguage() {
        languageService = new LanguageServiceImpl(languageGateway);
        String testLocale = "en";
        List<Language> languages = new ArrayList<>();
        languages.add(new Language("en"));
        languages.get(0).setId(1L);
        languages.add(new Language("nl"));
        languages.add(new Language("fr"));
        when(languageGateway.findByLocale(testLocale))
                .thenReturn(languages.get(0));
        doAnswer(invocation -> languages.remove(0))
                .when(languageGateway).deleteById(anyLong());
        assertEquals(testLocale, languageService.deleteLanguage(testLocale));
        verify(languageGateway).deleteById(1L);
    }

    @Test
    @Transactional
    void findByLocale() {
        languageService = new LanguageServiceImpl(languageGateway);
        String testLocale = "en";
        when(languageGateway.findByLocale(testLocale)).thenReturn(new Language(testLocale));
        assertEquals(testLocale, languageService.findByLocale(testLocale).getLocale());
    }

    @Test
    @Transactional
    void findByLocaleWithLanguageNotFoundException() {
        languageService = new LanguageServiceImpl(languageGateway);
        String testLocale = "en";
        when(languageGateway.findByLocale(testLocale)).thenReturn(null);
        assertThrows(LanguageNotFoundException.class, () -> {
            languageService.findByLocale(testLocale);
        });
    }
}
