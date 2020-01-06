package com.example.demo_translate_api.entrypoints.controllers;

import com.example.demo_translate_api.core.dto.StringResponse;
import com.example.demo_translate_api.core.services.api.KeyService;
import com.example.demo_translate_api.exceptions.LanguageNotFoundException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/keys")
public class KeyController {

    private final KeyService keyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyController.class);

    public KeyController(KeyService keyService) {
        this.keyService = keyService;
    }

    @GetMapping("")
    public ResponseEntity getKeys() {
        LOGGER.info("getKeys  @/api/keys");
        return ResponseEntity.ok(keyService.getKeys());
        //Should return String[] with all the distinct keys
    }

    @GetMapping("/{locale}")
    public ResponseEntity getKeys(@PathVariable String locale) {
        LOGGER.info("getKeys  @/api/keys/{locale}");
        try {
            return ResponseEntity.ok(keyService.getKeys(locale));
        } catch (LanguageNotFoundException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/missing")
    public ResponseEntity getMissingKeys() {
        LOGGER.info("getMissingKeys  @/api/keys/missing");
        return ResponseEntity.ok(keyService.getMissingKeys());
        //Should return ConcurrentHashMap with for each locale the missing keys List<String>
    }

    @PostMapping("/update")
    public ResponseEntity updateKeys() {
        LOGGER.info("updateKeys  @/api/keys/update");
        return ResponseEntity.ok(keyService.updateKeys());
        //Should return String[] with all the locales that have been updated
    }

    @PostMapping("/update/{locale}")
    public ResponseEntity updateKeys(@PathVariable String locale) {
        LOGGER.info("updateKeys  @/api/keys/update/{locale}");
        return ResponseEntity.ok(keyService.updateKeys(locale));
        //Should return String[] with all the keys that have been created for this locale
    }

    @PostMapping("/delete/{key}")
    public ResponseEntity deleteKey(@PathVariable String key) {
        LOGGER.info("deleteKey  @/api/keys/delete/{key}");
        try {
            StringResponse response = new StringResponse(keyService.deleteKey(key));
            return ResponseEntity.ok(response);
        } catch (NullPointerException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        // Should return the key that has been deleted
    }

    @PostMapping("/delete")
    public ResponseEntity deleteKeys(@RequestBody String[] keys) {
        LOGGER.info("deleteKey  @/api/keys/delete");
        try {
            return ResponseEntity.ok(keyService.deleteKeys(keys));
        } catch (NullPointerException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        // Should return String[] with all the keys that have been deleted
    }

    @PostMapping("/add")
    public ResponseEntity addKey(@RequestBody String key) {
        LOGGER.info("addKey  @/api/keys/add");
        return ResponseEntity.ok(keyService.addKey(key));
    }
}
