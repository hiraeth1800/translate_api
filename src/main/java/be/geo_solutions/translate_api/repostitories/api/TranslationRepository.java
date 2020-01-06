package be.geo_solutions.translate_api.repostitories.api;

import be.geo_solutions.translate_api.core.gateways.TranslationGateway;
import be.geo_solutions.translate_api.core.model.Translation;
import org.springframework.data.repository.CrudRepository;

public interface TranslationRepository extends CrudRepository<Translation, Integer>, TranslationGateway {

}
