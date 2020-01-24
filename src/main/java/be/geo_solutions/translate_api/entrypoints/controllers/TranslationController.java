package be.geo_solutions.translate_api.entrypoints.controllers;


import be.geo_solutions.translate_api.exceptions.LanguageNotFoundException;
import be.geo_solutions.translate_api.core.dto.KeyDTO;
import be.geo_solutions.translate_api.core.dto.TranslationDTO;
import be.geo_solutions.translate_api.core.services.api.TranslationService;
import be.geo_solutions.translate_api.core.services.impl.TranslationServiceImpl;
import be.geo_solutions.translate_api.exceptions.DuplicateTranslationException;
import be.geo_solutions.translate_api.exceptions.EmptyTranslationException;
import be.geo_solutions.translate_api.exceptions.KeyNotFoundException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "Get a translation by locale and key")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the translation", response = TranslationDTO.class),
            @ApiResponse(code = 500, message = "No language with the given locale found / No translation found with the given key")
    })
    @GetMapping("/get")
    public ResponseEntity getTranslation(@RequestBody KeyDTO keyDTO) {
        LOGGER.info("getTranslation  @/api/translation/");
        try {
            return ResponseEntity.ok(translationService.getTranslation(keyDTO));
        } catch (LanguageNotFoundException | KeyNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Add a translation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the created translation", response = TranslationDTO.class),
            @ApiResponse(code = 500, message = "No language with the given locale found / A translation with the key already exist for this locale")
    })
    @PostMapping("/add")
    public ResponseEntity addTranslation(@RequestBody TranslationDTO translationDTO) {
        LOGGER.info("addTranslation  @/api/translation/add");
        try {
            return ResponseEntity.ok(translationService.addTranslation(translationDTO));
        } catch (LanguageNotFoundException | DuplicateTranslationException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Update a translation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the updated translation", response = TranslationDTO.class),
            @ApiResponse(code = 500, message = "No language with the given locale found / No translation found with the given key and locale")
    })
    @PutMapping("/update")
    public ResponseEntity updateTranslation(@RequestBody TranslationDTO translationDTO) {
        LOGGER.info("updateTranslation  @/api/translation/update");
        try {
            return ResponseEntity.ok(translationService.updateTranslation(translationDTO));
        } catch (LanguageNotFoundException | KeyNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Delete a translation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the deleted translation", response = TranslationDTO.class),
            @ApiResponse(code = 500, message = "No language with the given locale found / A translation with the key already exist for this locale")
    })
    @PostMapping("/delete")
    public ResponseEntity deleteTranslation(@RequestBody KeyDTO keyDTO) {
        LOGGER.info("deleteTranslation  @/api/translation/delete");
        try {
            return ResponseEntity.ok(translationService.deleteTranslation(keyDTO));
        } catch (LanguageNotFoundException | KeyNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
