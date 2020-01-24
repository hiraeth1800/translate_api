package be.geo_solutions.translate_api.entrypoints.controllers;

import be.geo_solutions.translate_api.core.dto.StringResponse;
import be.geo_solutions.translate_api.core.dto.TranslationDTO;
import be.geo_solutions.translate_api.core.services.impl.UploadServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "Upload translations from a file", notes = "Currently allows: csv/CSV")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the added translations", response = TranslationDTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Fault with the file"),
            @ApiResponse(code = 500, message = "Something went wrong")
    })
    @PostMapping("/{type}")
    public ResponseEntity createTranslationsFromFile(@PathVariable(value = "type") String type, @RequestParam(value = "file") MultipartFile file) {
        try {
            LOGGER.info("createTranslationsFromFile  @/api/upload/translations/{type}");
            Path tempPath = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename()).toPath();
            File tempFile = Files.write(tempPath, file.getBytes()).toFile();
            List<TranslationDTO> translations = uploadService.createTranslationsFromFile(type, tempFile);
            return ResponseEntity.status(HttpStatus.OK).body(translations);
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Upload keys from a file", notes = "currently allows: json (angular ng extract)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved {Ok}", response = StringResponse.class),
            @ApiResponse(code = 400, message = "Fault with the file"),
            @ApiResponse(code = 500, message = "Something went wrong")
    })
    @PostMapping("/keys/{type}")
    public ResponseEntity updateKeysFromFile(@PathVariable(value = "type") String type, @RequestParam(value = "file") MultipartFile file) {
        try {
            LOGGER.info("updateKeysFromFile  @/api/upload/keys/{type}");
            Path tempPath = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename()).toPath();
            File tempFile = Files.write(tempPath, file.getBytes()).toFile();
            uploadService.updateKeysFromFile(type, tempFile);
            return ResponseEntity.status(HttpStatus.OK).body(new StringResponse("Ok"));
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
