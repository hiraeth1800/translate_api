package com.example.demo_translate_api.entrypoints.controllers;


import com.example.demo_translate_api.core.dto.KeyDTO;
import com.example.demo_translate_api.core.dto.TranslationDTO;
import com.example.demo_translate_api.core.services.api.TranslationService;
import com.example.demo_translate_api.core.services.impl.TranslationServiceImpl;
import com.example.demo_translate_api.exceptions.DuplicateTranslationException;
import com.example.demo_translate_api.exceptions.EmptyTranslationException;
import com.example.demo_translate_api.exceptions.KeyNotFoundException;
import com.example.demo_translate_api.exceptions.LanguageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/translations")
public class TranslationController {

    private final TranslationService translationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationController.class);

    public TranslationController(TranslationServiceImpl translateServiceImpl) {
        this.translationService = translateServiceImpl;
    }

    @GetMapping("/get")
    public ResponseEntity getTranslation(@RequestBody KeyDTO keyDTO) {
        LOGGER.info("getTranslation  @/api/translation/");
        try {
            return ResponseEntity.ok(translationService.getTranslation(keyDTO));
        } catch (LanguageNotFoundException | KeyNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        //Should return a DTO with locale, key and value
    }

    @PostMapping("/add")
    public ResponseEntity addTranslation(@RequestBody TranslationDTO translationDTO) {
        LOGGER.info("addTranslation  @/api/translation/add");
        try {
            return ResponseEntity.ok(translationService.addTranslation(translationDTO));
        } catch (EmptyTranslationException | LanguageNotFoundException | DuplicateTranslationException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        //Should return the created DTO with locale, key and value
    }

    @PutMapping("/update")
    public ResponseEntity updateTranslation(@RequestBody TranslationDTO translationDTO) {
        LOGGER.info("updateTranslation  @/api/translation/update");
        try {
            return ResponseEntity.ok(translationService.updateTranslation(translationDTO));
        } catch (LanguageNotFoundException | KeyNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        // Should return the changed DTO with locale, key and value
    }


    @PostMapping("/delete")
    public ResponseEntity deleteTranslation(@RequestBody KeyDTO keyDTO) {
        LOGGER.info("deleteTranslation  @/api/translation/delete");
        try {
            return ResponseEntity.ok(translationService.deleteTranslation(keyDTO));
        } catch (LanguageNotFoundException | KeyNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        // Should return the removed DTO with locale, key and value
    }






    @PostMapping("/import")
    public ResponseEntity bulkAddTranslation(@RequestBody String locale) {
        // TODO add translations in bulk with files for example
        return ResponseEntity.ok("ok");
    }

    // TODO export language
    // TODO import language(s)
}
