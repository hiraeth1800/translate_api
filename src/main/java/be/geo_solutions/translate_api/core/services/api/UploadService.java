package be.geo_solutions.translate_api.core.services.api;

import be.geo_solutions.translate_api.core.dto.TranslationDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface UploadService {
    List<TranslationDTO> createTranslationsFromFile(File tempFile) throws IOException;

    void updateKeysFromFile(File tempFile);
}
