package be.geo_solutions.translate_api.repostitories.api;

import be.geo_solutions.translate_api.core.gateways.LanguageGateway;
import be.geo_solutions.translate_api.core.model.Language;
import org.springframework.data.repository.CrudRepository;

public interface LanguageRepository extends CrudRepository<Language, Long>, LanguageGateway {
}
