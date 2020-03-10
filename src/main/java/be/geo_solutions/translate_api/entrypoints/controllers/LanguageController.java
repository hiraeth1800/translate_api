package be.geo_solutions.translate_api.entrypoints.controllers;

import be.geo_solutions.translate_api.core.dto.StringResponse;
import be.geo_solutions.translate_api.core.services.api.KeyService;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.exceptions.DuplicateLanguageException;
import be.geo_solutions.translate_api.exceptions.LanguageNotFoundException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

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

    @ApiOperation(value = "Get a list of languages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of languages", response = String.class, responseContainer = "List")
    })
    @GetMapping("")
    public ResponseEntity getLanguages() {
        LOGGER.info("getLanguages  @/api/languages/");
        try {
            return ResponseEntity.ok(languageService.getLanguages());
        } catch (Exception e) {
            LOGGER.error("!!! Unexpected error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("An unexpected error occurred"));
        }
    }

    @ApiOperation(value = "Get translations from a language")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved object with translations", response = ConcurrentHashMap.class),
            @ApiResponse(code = 500, message = "No language with the given locale found")
    })
    @GetMapping("/{locale}")
    public ResponseEntity getLanguage(@PathVariable String locale) {
        LOGGER.info("getLanguage  @/api/languages/{locale}");
        try {
            return ResponseEntity.ok(languageService.getLanguageByLocale(locale));
        } catch (LanguageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("!!! Unexpected error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("An unexpected error occurred"));
        }
    }

    @ApiOperation(value = "Add a language")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved object with translations from the created language", response = ConcurrentHashMap.class),
            @ApiResponse(code = 500, message = "No language with the given locale found / Language with the given locale already exists")
    })
    @PostMapping("/add")
    public ResponseEntity addLanguage(@RequestBody String locale) {
        LOGGER.info("addLanguage  @/api/languages/add");
        try {
            languageService.addLanguage(locale);
            //not adding all the keys cause the values will be "" leaving the texts blank by initiating the language
            //keyService.updateKeysByLocale(locale);
            return ResponseEntity.ok(languageService.getLanguageByLocale(locale));
        } catch (DuplicateLanguageException | LanguageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("!!! Unexpected error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("An unexpected error occurred"));
        }
    }

    @ApiOperation(value = "Delete a language")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved deleted language locale", response = String.class),
            @ApiResponse(code = 500, message = "No language with the given locale found")
    })
    @PostMapping("/delete")
    public ResponseEntity deleteLanguage(@RequestBody String locale) {
        LOGGER.info("removeLanguage  @/api/languages/delete");
        try {
            StringResponse response = new StringResponse(languageService.deleteLanguage(locale));
            return ResponseEntity.ok(response);
        } catch (LanguageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("!!! Unexpected error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("An unexpected error occurred"));
        }
    }
}
