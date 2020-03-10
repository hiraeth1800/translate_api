package be.geo_solutions.translate_api.core.services.impl;

import be.geo_solutions.translate_api.core.services.api.DownloadService;
import be.geo_solutions.translate_api.core.services.api.KeyService;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.exceptions.FileFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Service
public class DownloadServiceImpl implements DownloadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadServiceImpl.class);
    private final LanguageService languageService;
    private final KeyService keyService;

    public DownloadServiceImpl(LanguageService languageService, KeyService keyService) {
        this.languageService = languageService;
        this.keyService = keyService;
    }

    @Override
    public Resource downloadTranslationsCsv(HttpServletResponse response) {
        String filename = "translations.csv";

        Path tempPath = new File(System.getProperty("java.io.tmpdir"), filename).toPath();
        response.setContentType("text/csv; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setHeader("filename", filename);

        try {
            Files.write(tempPath, createCSVContent());

        } catch (IOException e) {
            LOGGER.error("Error while creating the file.");
            e.printStackTrace();
            throw new FileFormatException("Error while creating the file.");
        }
        return new FileSystemResource(tempPath.toString());
    }

    private byte[] createCSVContent() {
        StringBuilder builder = new StringBuilder();
        List<ConcurrentHashMap<String, String>> allTranslations = new ArrayList<>();
        List<String> languages = languageService.getLanguages();
        languages.forEach(l -> {
            allTranslations.add(languageService.getLanguageByLocale(l));
        });
        List<String> keys = keyService.getKeys();
        keys.forEach(key -> {
            builder.append(key);
            allTranslations.forEach(translations -> {
                builder.append(",");
                translations.computeIfPresent(key, (k, v)-> {
                   builder.append(v);
                   return v;
                });
            });
            builder.append("\n");
        });
        builder.deleteCharAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString().getBytes();
    }

    @Override
    public byte[] downloadTranslationsExcel(HttpServletResponse response) {
        String filename = "translations.xlsx";
        Path tempPath = new File(System.getProperty("java.io.tmpdir"), filename).toPath();
        return createExcel();
    }

    private byte[] createExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("translations");
        List<ConcurrentHashMap<String, String>> allTranslations = new ArrayList<>();
        List<String> languages = languageService.getLanguages();
        languages.forEach(l -> {
            allTranslations.add(languageService.getLanguageByLocale(l));
        });
        List<String> keys = keyService.getKeys();
        IntStream.range(0, keys.size() - 1).forEach(i -> {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(keys.get(i));
            IntStream.range(0, allTranslations.size() - 1).forEach(j -> {
                row.createCell(j + 1)
                        .setCellValue(allTranslations.get(j).get(keys.get(i)));
            });
        });
        for (int i = 0; i <= languages.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            os.close();
            workbook.close();
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
