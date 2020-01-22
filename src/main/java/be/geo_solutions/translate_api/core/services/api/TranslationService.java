package be.geo_solutions.translate_api.core.services.api;

import be.geo_solutions.translate_api.core.dto.KeyDTO;
import be.geo_solutions.translate_api.core.dto.TranslationDTO;
import be.geo_solutions.translate_api.core.model.Translation;

import java.util.List;

public interface TranslationService {
    TranslationDTO getTranslation(KeyDTO keyDTO);
    TranslationDTO addTranslation(TranslationDTO translationDTO);
    TranslationDTO updateTranslation(TranslationDTO translationDTO);
    TranslationDTO deleteTranslation(KeyDTO translationDTO);

    Translation save(Translation translation);
    Translation deleteById(Long translationId);
    List<Translation> findByKey(String key);
}
