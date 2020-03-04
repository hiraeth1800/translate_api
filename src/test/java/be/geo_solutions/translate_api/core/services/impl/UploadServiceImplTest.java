package be.geo_solutions.translate_api.core.services.impl;

import be.geo_solutions.translate_api.core.model.Language;
import be.geo_solutions.translate_api.core.model.Translation;
import be.geo_solutions.translate_api.core.services.api.KeyService;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.core.services.api.TranslationService;
import be.geo_solutions.translate_api.exceptions.FileExtensionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class UploadServiceImplTest {
    @Mock
    private LanguageService languageService;
    @Mock
    private TranslationService translationService;
    @Mock
    private KeyService keyService;

    private UploadServiceImpl uploadService;


    @BeforeEach
    void setup() {
        uploadService = new UploadServiceImpl(languageService, translationService, keyService);
        List<Language> languages = new ArrayList<>();
        Language en = new Language("en");
        en.getTranslations().add(new Translation(en, "EXISTING.KEY", "should be updated"));
        List<String> locales = new ArrayList<>();
        locales.add("en");
        when(languageService.getLanguages()).thenReturn(locales);
        when(languageService.findByLocale(en.getLocale())).thenReturn(en);
        when(languageService.addLanguage(en.getLocale())).thenReturn(en);
        when(languageService.save(any())).thenReturn(en);
    }

    @Test
    @Transactional
    void createTranslationsFromFileWrongExtension() {
        File file = new File("src/test/resources/test_wrong_extension.txt");
        assertThrows(FileExtensionException.class, () -> uploadService.createTranslationsFromFile(file));
    }

    @Test
    @Transactional
    void createTranslationsFromJsonFile() {
        File file = new File("src/test/resources/en.json");
        try {
            assertEquals(0, uploadService.createTranslationsFromFile(file).size());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    @Transactional
    void createTranslationsFromCsvFile() {
        File file = new File("src/test/resources/en.csv");
        try {
            assertEquals(0, uploadService.createTranslationsFromFile(file).size());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    @Transactional
    void createTranslationsFromXlsFile() {
        File file = new File("src/test/resources/en.xls");
        try {
            assertEquals(0, uploadService.createTranslationsFromFile(file).size());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    @Transactional
    void createTranslationsFromXlsxFile() {
        File file = new File("src/test/resources/en.xlsx");
        try {
            assertEquals(0, uploadService.createTranslationsFromFile(file).size());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    @Transactional
    void updateKeysFromFileWrongExtension() {
        File file = new File("src/test/resources/test_wrong_extension.txt");
        assertThrows(FileExtensionException.class, () -> uploadService.updateKeysFromFile(file));
    }
}
