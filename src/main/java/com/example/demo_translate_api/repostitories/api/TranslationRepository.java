package com.example.demo_translate_api.repostitories.api;

import com.example.demo_translate_api.core.gateways.TranslationGateway;
import com.example.demo_translate_api.core.model.Translation;
import org.springframework.data.repository.CrudRepository;

public interface TranslationRepository extends CrudRepository<Translation, Integer>, TranslationGateway {

}
