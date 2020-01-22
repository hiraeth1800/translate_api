package be.geo_solutions.translate_api.core.services.impl;

import be.geo_solutions.translate_api.core.dto.TranslationDTO;
import be.geo_solutions.translate_api.core.model.Language;
import be.geo_solutions.translate_api.core.model.Translation;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.core.services.api.TranslationService;
import be.geo_solutions.translate_api.core.services.api.UploadService;
import be.geo_solutions.translate_api.exceptions.FileExtensionException;
import be.geo_solutions.translate_api.exceptions.FileFormatException;
import be.geo_solutions.translate_api.exceptions.LanguageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadServiceImpl implements UploadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);
    private final LanguageService languageService;
    private final TranslationService translationService;

    public UploadServiceImpl(LanguageService languageService, TranslationService translationService) {
        this.languageService = languageService;
        this.translationService = translationService;
    }

    @Override
    public List<TranslationDTO> createTranslationsFromFile(String type, File tempFile)  {
        // if csv go csv create method
        // read content try to parse into translation and save - > if saved add to list return list (filename is locale)
        String ext = this.getExtension(tempFile);
        switch (ext.toLowerCase()) {
            /*case "xls":
            case "xlsx":
                return this.processExcelFile(file, type);*/
            case "csv":
                return this.createOrUpdateTranslations(this.processCsvFile(tempFile));
            default:
                throw new FileExtensionException("Wrong extension. File should be a an excel or csv file.");
        }
    }

    private List<TranslationDTO> createOrUpdateTranslations(ConcurrentHashMap<String, List<TranslationDTO>> sortedByLocales) {
        List<TranslationDTO> failedToCreate = new ArrayList<>();
        Iterator<Map.Entry<String, List<TranslationDTO>>> iterator = sortedByLocales.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<TranslationDTO>> next = iterator.next();
            List<String> locales = languageService.getLanguages();
            if (!locales.contains(next.getKey()) && next.getKey().length() != 2) {
                failedToCreate.addAll(next.getValue());
            } else {
                Language l = null;
                try {
                     l = languageService.findByLocale(next.getKey());
                } catch (LanguageNotFoundException e) {
                    l = languageService.addLanguage(next.getKey());
                }
                for (TranslationDTO dto : next.getValue()) {
                    Optional<Translation> optTranslation = l.getTranslations().stream().filter(t -> t.getKey().equals(dto.getKey())).findFirst();
                    if (optTranslation.isPresent()) {
                        optTranslation.get().setValue(dto.getTranslation());
                    } else {
                        l.getTranslations().add(new Translation(l, dto.getKey(), dto.getTranslation()));
                    }
                }
                languageService.save(l);
            }
        }
        return failedToCreate;
    }

    private String getExtension(File file) {
        String fileName = file.getName();
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) return "";
        return fileName.substring(lastIndexOf + 1);
    }

    private ConcurrentHashMap<String, List<TranslationDTO>> processCsvFile(File file) {
        ConcurrentHashMap<String, List<TranslationDTO>> sortedByLocales = new ConcurrentHashMap<>();
        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("[\\n]");
            while (scanner.hasNext()) {
                String line = scanner.next()
                        .replace("\r", "")
                        .replace("\"", "")
                        .replace("'", "");
                String[] translation = line.split("[,;\t]");
                String locale = translation[0];
                TranslationDTO dto = new TranslationDTO();
                switch (translation.length) {
                    case 3:
                        if (!translation[2].isEmpty()) {
                            dto = new TranslationDTO(translation[0], translation[1].toUpperCase(), translation[2]);
                            break;
                        }
                    case 2:
                        locale = file.getName().substring(0, file.getName().lastIndexOf("."));
                        dto = new TranslationDTO(file.getName().substring(0, file.getName().lastIndexOf(".")), translation[0].toUpperCase(), translation[1]);
                        break;
                    default: throw new FileFormatException("File is in wrong format at '" + line + "'. Either use locale,key,value or key,value with locale as filename");
                }
                if (sortedByLocales.get(locale) == null) {
                    sortedByLocales.put(locale, new ArrayList<>(Collections.singletonList(dto)));
                } else {
                    sortedByLocales.get(locale).add(dto);
                }
                System.out.println(translation.length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sortedByLocales;
    }
}
