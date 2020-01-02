package com.example.demo_translate_api.core.services.api;

import com.example.demo_translate_api.core.dto.KeyDTO;
import com.example.demo_translate_api.core.dto.TranslationDTO;
import com.example.demo_translate_api.core.model.Translation;

import java.util.List;

public interface TranslationService {
    TranslationDTO getTranslation(KeyDTO keyDTO);
    TranslationDTO addTranslation(TranslationDTO translationDTO);
    TranslationDTO updateTranslation(TranslationDTO translationDTO);
    TranslationDTO deleteTranslation(KeyDTO translationDTO);

    Translation save(Translation translation);
    Translation deleteById(int translationId);
    List<Translation> findByKey(String key);
}
