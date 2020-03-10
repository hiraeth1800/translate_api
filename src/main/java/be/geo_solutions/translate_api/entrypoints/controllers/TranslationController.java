package be.geo_solutions.translate_api.entrypoints.controllers;


import be.geo_solutions.translate_api.core.dto.KeyDTO;
import be.geo_solutions.translate_api.core.dto.StringResponse;
import be.geo_solutions.translate_api.core.dto.TranslationDTO;
import be.geo_solutions.translate_api.core.services.api.TranslationService;
import be.geo_solutions.translate_api.core.services.impl.TranslationServiceImpl;
import be.geo_solutions.translate_api.exceptions.DuplicateTranslationException;
import be.geo_solutions.translate_api.exceptions.KeyNotFoundException;
import be.geo_solutions.translate_api.exceptions.LanguageNotFoundException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ResponseEntity getTranslation(@Valid @RequestBody KeyDTO keyDTO) {
        LOGGER.info("getTranslation  @/api/translation/");
        try {
            return ResponseEntity.ok(translationService.getTranslation(keyDTO));
        } catch (LanguageNotFoundException | KeyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("!!! Unexpected error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("An unexpected error occurred"));
        }
    }

    @ApiOperation(value = "Add a translation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the created translation", response = TranslationDTO.class),
            @ApiResponse(code = 500, message = "No language with the given locale found / A translation with the key already exist for this locale")
    })
    @PostMapping("/add")
    public ResponseEntity addTranslation(@Valid @RequestBody TranslationDTO translationDTO) {
        LOGGER.info("addTranslation  @/api/translation/add");
        try {
            return ResponseEntity.ok(translationService.addTranslation(translationDTO));
        } catch (LanguageNotFoundException | DuplicateTranslationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("!!! Unexpected error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("An unexpected error occurred"));
        }
    }

    @ApiOperation(value = "Update a translation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the updated translation", response = TranslationDTO.class),
            @ApiResponse(code = 500, message = "No language with the given locale found / No translation found with the given key and locale")
    })
    @PutMapping("/update")
    public ResponseEntity updateTranslation(@Valid @RequestBody TranslationDTO translationDTO) {
        LOGGER.info("updateTranslation  @/api/translation/update");
        try {
            return ResponseEntity.ok(translationService.updateTranslation(translationDTO));
        } catch (LanguageNotFoundException | KeyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("!!! Unexpected error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("An unexpected error occurred"));
        }
    }

    @ApiOperation(value = "Delete a translation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the deleted translation", response = TranslationDTO.class),
            @ApiResponse(code = 500, message = "No language with the given locale found / A translation with the key already exist for this locale")
    })
    @PostMapping("/delete")
    public ResponseEntity deleteTranslation(@Valid @RequestBody KeyDTO keyDTO) {
        LOGGER.info("deleteTranslation  @/api/translation/delete");
        try {
            return ResponseEntity.ok(translationService.deleteTranslation(keyDTO));
        } catch (LanguageNotFoundException | KeyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("!!! Unexpected error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("An unexpected error occurred"));
        }
    }
}
