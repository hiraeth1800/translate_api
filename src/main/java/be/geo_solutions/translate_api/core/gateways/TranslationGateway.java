package be.geo_solutions.translate_api.core.gateways;

import be.geo_solutions.translate_api.core.model.Language;
import be.geo_solutions.translate_api.core.model.Translation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TranslationGateway extends Gateway<Translation> {
    Translation findByLanguageAndKey(Language language, String key);
    List<Translation> findByKey(String key);
}
