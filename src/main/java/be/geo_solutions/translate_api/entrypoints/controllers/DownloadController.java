package be.geo_solutions.translate_api.entrypoints.controllers;

import be.geo_solutions.translate_api.core.dto.StringResponse;
import be.geo_solutions.translate_api.core.services.api.DownloadService;
import be.geo_solutions.translate_api.exceptions.FileExtensionException;
import be.geo_solutions.translate_api.exceptions.FileFormatException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/download")
public class DownloadController {

    private final DownloadService downloadService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @ApiOperation(value = "Download a file with translations")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved translations file", response = Resource.class),
            @ApiResponse(code = 400, message = "Accepted file type is /csv, /xls or /xlsx"),
            @ApiResponse(code = 500, message = "An error occurred while creating the file")
    })
    @GetMapping(value = "/translations/{type}", produces = {"application/vnd.ms-excel", "text/csv; charset=utf-8"})
    public ResponseEntity downloadTranslations(@PathVariable(value = "type") String type, HttpServletResponse response){
        LOGGER.info("downloadTranslations  @/api/download/translations/" + type);
        try {
            if (type.toLowerCase().equals("csv")) {
                return ResponseEntity.ok(downloadService.downloadTranslationsCsv(response));
            } else if (type.toLowerCase().equals("xls") || type.toLowerCase().equals("xlsx")) {
                return ResponseEntity.ok(downloadService.downloadTranslationsExcel(response));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Translations can be downloaded in csv, xlsx (/xls gives xlsx)");
            }
        } catch (FileExtensionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (FileFormatException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("!!! Unexpected error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("An unexpected error occurred"));
        }
    }
}
