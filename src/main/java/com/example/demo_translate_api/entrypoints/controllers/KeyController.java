package com.example.demo_translate_api.entrypoints.controllers;

import com.example.demo_translate_api.core.services.api.KeyService;
import com.example.demo_translate_api.exceptions.LanguageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;

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
            return ResponseEntity.ok(keyService.removeKey(key));
        } catch (NullPointerException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        // Should return the key that has been deleted
    }

    @PostMapping("/delete")
    public ResponseEntity deleteKeys(@RequestBody String[] keys) {
        LOGGER.info("deleteKey  @/api/keys/delete  NOT IMPLEMENTED");
        try {
            return ResponseEntity.ok(keyService.removeKeys(keys));
        } catch (NullPointerException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        // Should return String[] with all the keys that have been deleted
    }
}
