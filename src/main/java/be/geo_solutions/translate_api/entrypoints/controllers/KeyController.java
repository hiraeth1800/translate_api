package be.geo_solutions.translate_api.entrypoints.controllers;

import be.geo_solutions.translate_api.core.dto.StringResponse;
import be.geo_solutions.translate_api.exceptions.LanguageNotFoundException;
import be.geo_solutions.translate_api.core.services.api.KeyService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/keys")
public class KeyController {

    private final KeyService keyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyController.class);

    public KeyController(KeyService keyService) {
        this.keyService = keyService;
    }

    @ApiOperation(value = "Get all the keys")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all the distinct keys", response = String.class, responseContainer = "List")
    })
    @GetMapping("")
    public ResponseEntity getKeys() {
        LOGGER.info("getKeys  @/api/keys");
        return ResponseEntity.ok(keyService.getKeys());
    }

    @ApiOperation(value = "Get a list of keys from a language")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved a list with keys", response = String.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "No language with the given locale found")
    })
    @GetMapping("/{locale}")
    public ResponseEntity getKeys(@PathVariable String locale) {
        LOGGER.info("getKeys  @/api/keys/{locale}");
        try {
            return ResponseEntity.ok(keyService.getKeysByLocale(locale));
        } catch (LanguageNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Get all the missing keys", notes = "This only covers missing keys in the backend. It is no guarantee that every key frontend the frontend is added!")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved object with missing keys for each language", response = ConcurrentHashMap.class),
    })
    @GetMapping("/missing")
    public ResponseEntity getMissingKeys() {
        LOGGER.info("getMissingKeys  @/api/keys/missing");
        return ResponseEntity.ok(keyService.getMissingKeys());
    }

    @ApiOperation(value = "Update all the missing keys", notes = "This only updates missing keys in the backend. This is no guarantee that keys from frontend are all saved!")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved locales from updated languages", response = String.class, responseContainer = "List")
    })
    @PostMapping("/update")
    public ResponseEntity updateKeys() {
        LOGGER.info("updateKeys  @/api/keys/update");
        return ResponseEntity.ok(keyService.updateKeys());
    }

    @ApiOperation(value = "Update the keys from a language", notes = "The keys from the language are checked against all the keys distinct.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the created keys for the language", response = String.class, responseContainer = "List")
    })
    @PostMapping("/update/{locale}")
    public ResponseEntity updateKeys(@PathVariable String locale) {
        LOGGER.info("updateKeys  @/api/keys/update/{locale}");
        return ResponseEntity.ok(keyService.updateKeys(locale));
    }

    @ApiOperation(value = "Delete a key from all languages", notes = "If a key doesn't exist it will be ignored.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the deleted key", response = StringResponse.class),
    })
    @PostMapping("/delete/{key}")
    public ResponseEntity deleteKey(@PathVariable String key) {
        LOGGER.info("deleteKey  @/api/keys/delete/{key}");
        StringResponse response = new StringResponse(keyService.deleteKey(key));
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Delete a list of keys", notes = "If a key doesn't exist it will be ignored.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the keys that are deleted", response = String.class, responseContainer = "List"),
    })
    @PostMapping("/delete")
    public ResponseEntity deleteKeys(@RequestBody String[] keys) {
        LOGGER.info("deleteKey  @/api/keys/delete");
        return ResponseEntity.ok(keyService.deleteKeys(keys));
    }

    @ApiOperation(value = "Add a key")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the languages where the key is added", response = String.class, responseContainer = "List"),
    })
    @PostMapping("/add")
    public ResponseEntity addKey(@RequestBody String key) {
        LOGGER.info("addKey  @/api/keys/add");
        return ResponseEntity.ok(keyService.addKey(key));
    }
}
