package com.example.demo_translate_api.entrypoints.controllers;

import com.example.demo_translate_api.core.services.api.KeyService;
import com.example.demo_translate_api.exceptions.DuplicateLanguageException;
import com.example.demo_translate_api.exceptions.LanguageNotFoundException;
import com.example.demo_translate_api.exceptions.LanguageServiceException;
import com.example.demo_translate_api.core.services.api.LanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    private final LanguageService languageService;
    private final KeyService keyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageController.class);

    public LanguageController(LanguageService languageService, KeyService keyService) {
        this.languageService = languageService;
        this.keyService = keyService;
    }

    @GetMapping("")
    public ResponseEntity getLanguages() {
        LOGGER.warn("getLanguages  @/api/languages/  NOT IMPLEMENTED");
        return ResponseEntity.ok(languageService.getLanguages());
        //
        //return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented yet. Should return List with languages");
    }

    @GetMapping("/{locale}")
    public ResponseEntity getLanguage(@PathVariable String locale) {
        LOGGER.info("getLanguage  @/api/languages/{locale}");
        try {
            return ResponseEntity.ok(languageService.getLanguageByLocale(locale));
        } catch (LanguageNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        //LOGGER.warn("getLanguage  @/api/language/{locale}  NOT IMPLEMENTED");
        //return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented yet. Should return a concurrentHashMap with the translations");
    }

    @PostMapping("/add/{locale}")
    public ResponseEntity addLanguage(@PathVariable String locale) {
        LOGGER.info("addLanguage  @/api/languages/add/{locale}");
        try {
            languageService.addLanguage(locale);
            keyService.updateKeys(locale);
            return ResponseEntity.ok(languageService.getLanguageByLocale(locale));
        } catch (DuplicateLanguageException | LanguageNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        // Should return a ConcurrentHashMap with the (empty) translations
    }

    @PostMapping("/delete")
    public ResponseEntity removeLanguage(@RequestBody String locale) {
        LOGGER.info("removeLanguage  @/api/languages/delete");
        try {
            languageService.removeLanguage(locale);
            return ResponseEntity.ok(locale);
        } catch (NullPointerException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        // a concurrentHashMap with the translations that are deleted
    }
}
