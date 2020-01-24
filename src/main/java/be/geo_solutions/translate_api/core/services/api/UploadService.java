package be.geo_solutions.translate_api.core.services.api;

import be.geo_solutions.translate_api.core.dto.TranslationDTO;
import be.geo_solutions.translate_api.core.services.impl.TranslationServiceImpl;
import be.geo_solutions.translate_api.core.services.impl.UploadServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public interface UploadService {
    List<TranslationDTO> createTranslationsFromFile(String type, File tempFile);

    void updateKeysFromFile(String type, File tempFile);
}
