package be.geo_solutions.translate_api.entrypoints.controllers;

import be.geo_solutions.translate_api.core.dto.TranslationDTO;
import be.geo_solutions.translate_api.core.services.impl.UploadServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final UploadServiceImpl uploadService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    public UploadController(UploadServiceImpl uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/{type}")
    public ResponseEntity createTranslationsFrom(@PathVariable(value = "type") String type, @RequestParam(value = "file") MultipartFile file) {
        try {
            LOGGER.info("createAreasFrom  @/api/upload/");
            Path tempPath = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename()).toPath();
            File tempFile = Files.write(tempPath, file.getBytes()).toFile();
            List<TranslationDTO> areas = uploadService.createTranslationsFromFile(type, tempFile);
            return ResponseEntity.status(HttpStatus.OK).body(areas);
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
