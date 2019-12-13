package com.example.demo_translate_api.controllers;

import com.example.demo_translate_api.dto.TranslationDTO;
import com.example.demo_translate_api.services.api.TranslateService;
import com.example.demo_translate_api.services.impl.TranslateServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TranslateController {

    private final TranslateService translateService;

    public TranslateController(TranslateServiceImpl translateServiceImpl) {
        this.translateService = translateServiceImpl;
    }

    @GetMapping("/language/{locale}")
    public ResponseEntity getLanguage(@PathVariable String locale) {
        try {
            return ResponseEntity.ok(translateService.getLanguage(locale));
        } catch (NullPointerException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/language/add")
    public ResponseEntity addLanguage(@RequestBody String locale) {
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/language/translation/add")
    public ResponseEntity addTranslation(@RequestBody TranslationDTO translationDTO) {
        return ResponseEntity.ok(translateService.addTranslation(translationDTO));
    }

    @PutMapping("/language/translation/update")
    public ResponseEntity updateTranslation(@RequestBody TranslationDTO translationDTO) {
        // TODO update a translation
        return ResponseEntity.ok("ok");
    }


    @PostMapping("/language/translation/delete")
    public ResponseEntity deleteTranslation(@RequestBody TranslationDTO translationDTO) {
        // Does not delete the key in other languages
        translateService.deleteTranslation(translationDTO);
        return ResponseEntity.ok(translationDTO);
    }

    @PostMapping("/language/translations/add")
    public ResponseEntity bulkAddTranslation(@RequestBody String locale) {
        // TODO add translations in bulk with files for example
        return ResponseEntity.ok("ok");
    }

    // TODO get all the keys from a language
    // TODO post To add all the keys to every language if the key is not there
    // TODO export language
    // TODO import language(s)
    // TODO update translation
    // TODO get missing keys for each language (example: english misses these keys (compared to other languages), dutch misses this key

}
