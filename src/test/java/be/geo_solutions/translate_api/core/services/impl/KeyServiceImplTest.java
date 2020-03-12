package be.geo_solutions.translate_api.core.services.impl;

import be.geo_solutions.translate_api.core.model.Language;
import be.geo_solutions.translate_api.core.model.Translation;
import be.geo_solutions.translate_api.core.services.api.KeyService;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.core.services.api.TranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class KeyServiceImplTest {

    @Mock
    LanguageService languageService;
    @Mock
    TranslationService translationService;

    KeyService keyService;
    KeyService keyServiceSpy;

    List<Language> languages;
    String testLocale;

    @BeforeEach
    void setup() {
        keyService = new KeyServiceImpl(languageService, translationService);
        keyServiceSpy = spy(keyService);
        testLocale = "en";
        languages = new ArrayList<>();

        languages.add(new Language("en"));
        List<Translation> enTranslations = new ArrayList<>();
        enTranslations.add(new Translation(languages.get(0), "KEY2", "translation 2"));
        enTranslations.get(0).setId(1L);
        enTranslations.add(new Translation(languages.get(0), "KEY1", "translation 1"));
        enTranslations.get(1).setId(2L);
        languages.get(0).setTranslations(enTranslations);

        languages.add(new Language("nl"));
        List<Translation> nlTranslations = new ArrayList<>();
        nlTranslations.add(new Translation(languages.get(1), "KEY2", "vertaling 2"));
        nlTranslations.get(0).setId(3L);
        nlTranslations.add(new Translation(languages.get(1), "KEY1", "vertaling 1"));
        nlTranslations.get(1).setId(4L);
        nlTranslations.add(new Translation(languages.get(1), "KEY3", "vertaling 3"));
        nlTranslations.get(2).setId(5L);
        languages.get(1).setTranslations(nlTranslations);

        languages.add(new Language("fr"));
        List<Translation> frTranslations = new ArrayList<>();
        frTranslations.add(new Translation(languages.get(0), "KEY3", "traduction 2"));
        frTranslations.get(0).setId(6L);

        languages.get(2).setTranslations(frTranslations);

        when(languageService.findAll()).thenReturn(languages);
        when(languageService.findByLocale(testLocale)).thenReturn(languages.get(0));
        when(languageService.findByLocale("nl")).thenReturn(languages.get(1));
        when(languageService.findByLocale("fr")).thenReturn(languages.get(2));
    }

    @Test
    @Transactional
    void getKeys() {
        List<String> keys = new ArrayList<>();
        keys.add("KEY1");
        keys.add("KEY2");
        keys.add("KEY3");
        // to test sorted
        assertEquals(keys, keyService.getKeys());
        assertEquals(3, keyService.getKeys().size());
        assertTrue(keyService.getKeys().contains("KEY1"));
        assertTrue(keyService.getKeys().contains("KEY2"));
        assertTrue(keyService.getKeys().contains("KEY3"));
    }

    @Test
    @Transactional
    void getKeysByLocale() {
        List<String> keys = new ArrayList<>();
        keys.add("KEY1");
        keys.add("KEY2");
        // to test sorted
        assertEquals(keys, keyService.getKeysByLocale("en"));
        assertEquals(2, keyService.getKeysByLocale("en").size());
        assertTrue(keyService.getKeysByLocale("en").contains("KEY1"));
        assertTrue(keyService.getKeysByLocale("en").contains("KEY2"));
    }

    @Test
    @Transactional
    void getMissingKeys() {
        Map<String, List<String>> expectedMissingKeys = new HashMap<>();
        expectedMissingKeys.put("en", Arrays.asList("KEY3"));
        String array[] = { "KEY2", "KEY1" };
        expectedMissingKeys.put("fr", Arrays.asList(array));

        doReturn(languages.get(0).getTranslations()
                .stream()
                .map(Translation::getKey)
                .sorted()
                .collect(Collectors.toList()))
                .when(keyServiceSpy)
                .getKeysByLocale(testLocale);
        ConcurrentHashMap<String, List<String>> missingKeys = keyServiceSpy.getMissingKeys();
        assertEquals(expectedMissingKeys.size(), missingKeys.size());
        assertEquals(expectedMissingKeys.get("en"), missingKeys.get("en"));
        assertEquals(expectedMissingKeys.get("fr"), missingKeys.get("fr"));

    }

    @Test
    @Transactional
    void updateKeys() {
        when(languageService.save(languages.get(0))).thenReturn(languages.get(0));
        List<String> updatedLocales = keyService.updateKeys();
        assertEquals(2, updatedLocales.size());
        assertTrue(updatedLocales.contains("en"));
        assertTrue(updatedLocales.contains("fr"));
        assertEquals(0, keyService.updateKeys().size());
    }

    @Test
    @Transactional
    void updateKeysByLocale() {
        List<String> keysThatShouldBeAdded = new ArrayList<>();
        keysThatShouldBeAdded.add("KEY3");
        when(languageService.save(languages.get(0))).thenReturn(languages.get(0));
        List<String> addedKeys = keyService.updateKeysByLocale(testLocale);
        assertEquals(keysThatShouldBeAdded, addedKeys);
        assertEquals(0, keyService.updateKeysByLocale(testLocale).size());
    }

    @Test
    @Transactional
    void deleteKeys() {
        String[] keysThatShouldBeDeleted = new String[2];
        keysThatShouldBeDeleted[0] = "KEY1";
        keysThatShouldBeDeleted[1] = "KEY3";
        when(languageService.save(languages.get(0))).thenReturn(languages.get(0));
        List<String> deletedKeys = keyService.deleteKeys(keysThatShouldBeDeleted);
        System.out.println();
        System.out.println(deletedKeys);
        System.out.println();
        assertEquals(Arrays.asList(keysThatShouldBeDeleted), deletedKeys);
        assertEquals(1, keyService.getKeysByLocale(testLocale).size());
        verify(translationService, never()).deleteById(1L);
        verify(translationService).deleteById(2L);
        verify(translationService, never()).deleteById(3L);
        verify(translationService).deleteById(4L);
        verify(translationService).deleteById(5L);
        verify(translationService).deleteById(6L);
    }

    @Test
    @Transactional
    void deleteKey() {
        String keyThatShouldBeDeleted = "KEY2";
        when(languageService.save(languages.get(0))).thenReturn(languages.get(0));
        when(translationService.findByKey("KEY2"))
                .thenReturn(
                        Arrays.asList(
                                languages.get(0).getTranslations().get(0),
                                languages.get(1).getTranslations().get(0)
                        ));
        String deletedKey = keyService.deleteKey(keyThatShouldBeDeleted);
        assertEquals(keyThatShouldBeDeleted, deletedKey);
        verify(translationService).deleteById(1L);
        verify(translationService, never()).deleteById(2L);
        verify(translationService).deleteById(3L);
        verify(translationService, never()).deleteById(4L);
        verify(translationService, never()).deleteById(5L);
        verify(translationService, never()).deleteById(6L);
        verify(translationService, never()).deleteById(7L);
        verify(translationService, never()).deleteById(7L);
    }

    @Test
    @Transactional
    void addKey() {
        List<String> updatedLocalesWithNewKey = Arrays.asList("en", "nl", "fr");
        List<String> updatedLocaleWithExistingKey = Arrays.asList("fr");
        assertEquals(updatedLocalesWithNewKey, keyService.addKey("KEY4"));
        assertEquals(updatedLocaleWithExistingKey, keyService.addKey("KEY2"));
    }
}
