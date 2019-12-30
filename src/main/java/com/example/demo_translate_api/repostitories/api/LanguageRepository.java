package com.example.demo_translate_api.repostitories.api;

import com.example.demo_translate_api.core.gateways.LanguageGateway;
import com.example.demo_translate_api.core.model.Language;
import org.springframework.data.repository.CrudRepository;

public interface LanguageRepository extends CrudRepository<Language, Integer>, LanguageGateway {

}
