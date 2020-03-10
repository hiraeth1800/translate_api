package be.geo_solutions.translate_api.core.services.impl;

import be.geo_solutions.translate_api.core.dto.TranslationDTO;
import be.geo_solutions.translate_api.core.model.Language;
import be.geo_solutions.translate_api.core.model.Translation;
import be.geo_solutions.translate_api.core.services.api.KeyService;
import be.geo_solutions.translate_api.core.services.api.LanguageService;
import be.geo_solutions.translate_api.core.services.api.TranslationService;
import be.geo_solutions.translate_api.core.services.api.UploadService;
import be.geo_solutions.translate_api.exceptions.FileExtensionException;
import be.geo_solutions.translate_api.exceptions.FileFormatException;
import be.geo_solutions.translate_api.exceptions.LanguageNotFoundException;
import be.geo_solutions.translate_api.exceptions.ProcessFileException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Service
public class UploadServiceImpl implements UploadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);
    private final LanguageService languageService;
    private final TranslationService translationService;
    private final KeyService keyService;

    public UploadServiceImpl(LanguageService languageService, TranslationService translationService, KeyService keyService) {
        this.languageService = languageService;
        this.translationService = translationService;
        this.keyService = keyService;
    }

    private String getExtension(File file) {
        String fileName = file.getName();
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) return "";
        return fileName.substring(lastIndexOf + 1);
    }

    private HashMap processJsonFile(File file) {
        try {
            return new ObjectMapper().readValue(file, HashMap.class);
        } catch(IOException e) {
            throw new ProcessFileException(e.getMessage());
        }
    }

    @Override
    public List<TranslationDTO> createTranslationsFromFile(File tempFile) throws IOException {
        // read content try to parse into translation and save - > if saved add to list return list (filename is locale)
        String ext = this.getExtension(tempFile);
        switch (ext.toLowerCase()) {
            case "json":
                String locale = tempFile.getName().substring(0, 2);
                HashMap map = processJsonFile(tempFile);
                ConcurrentHashMap<String, List<TranslationDTO>> sortedByLocales = new ConcurrentHashMap<>();
                sortedByLocales.put(locale, convertToTranslationDTOs(locale, map));
                return createOrUpdateTranslations(sortedByLocales);
            case "csv":
                return this.createOrUpdateTranslations(this.processTranslationCsvFile(tempFile));
            case "xls":
            case "xlsx":
                return this.createOrUpdateTranslations(processTranslationExcelFile(tempFile));
            default:
                throw new FileExtensionException("Wrong extension. File should be a json, excel or csv file. (.json, .csv, .xls, .xlsx)");
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

    private ConcurrentHashMap<String, List<TranslationDTO>> processTranslationCsvFile(File file) {
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

    private ConcurrentHashMap<String, List<TranslationDTO>> processTranslationExcelFile(File file) throws IOException {
        ConcurrentHashMap<String, List<TranslationDTO>> translations = new ConcurrentHashMap<>();
        List<TranslationDTO> dtos = new ArrayList<>();
        Workbook workbook = getWorkBookFromFile(file);
        IntStream.range(0, workbook.getNumberOfSheets()).forEach(i -> {
            String locale = workbook.getSheetAt(i).getSheetName();
            if (locale.toLowerCase().equals("sheet" + i)){
                locale = "";
            }
            List<TranslationDTO> translationDTOs = getTranslationDTOsFromSheet(locale, workbook.getSheetAt(i));
            dtos.addAll(translationDTOs);
        });
        dtos.forEach(dto -> {
            if (!translations.containsKey(dto.getLocale())) {
                translations.put(dto.getLocale(), new ArrayList<>());
            }
            translations.get(dto.getLocale()).add(dto);
        });
        return translations;
    }

    private List<TranslationDTO> getTranslationDTOsFromSheet(String locale, Sheet sheet) {
        List<TranslationDTO> translationDTOs = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            TranslationDTO translationDTO = new TranslationDTO();
            if (row.getPhysicalNumberOfCells() == 2) {
                if (!locale.equals("")) {
                    translationDTO.setLocale(locale);
                }
                translationDTO.setKey(row.getCell(0).getStringCellValue().trim());
                translationDTO.setTranslation(row.getCell(1).getStringCellValue().trim());
            } else {
                translationDTO.setLocale(row.getCell(0).getStringCellValue().trim());
                translationDTO.setKey(row.getCell(1).getStringCellValue().trim());
                translationDTO.setTranslation(row.getCell(2).getStringCellValue().trim());
            }
            translationDTOs.add(translationDTO);
        }
        return translationDTOs;
    }

    private Workbook getWorkBookFromFile(File file) throws IOException {
        try {
            return  WorkbookFactory.create(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error reading file");
        }
    }

    // used for jsonfiles
    private List<TranslationDTO> convertToTranslationDTOs(String locale, HashMap map) {
        List<TranslationDTO> dtos = new ArrayList<>();
        for (Object o : map.keySet()) {
            if (LinkedHashMap.class.equals(map.get(o).getClass())) {
                HashMap newLevel = new HashMap();
                HashMap currentLevel = (HashMap) map.get(o);
                currentLevel.keySet().forEach(key -> {
                    newLevel.put(o + "." + key, currentLevel.get(key));
                });
                convertToTranslationDTOs(locale, newLevel).forEach(x -> {
                    dtos.add(new TranslationDTO(x.getLocale(), x.getKey(), x.getTranslation()));
                });
            } else {
                dtos.add(new TranslationDTO(locale, o.toString(), map.get(o).toString()));
            }
        }
        return dtos;
    }

    @Override
    public void updateKeysFromFile(File tempFile) {
        String ext = this.getExtension(tempFile);
        switch (ext.toLowerCase()) {
            case "json":
                updateKeys(processJsonFile(tempFile)); break;
            case "csv":
            case "xls":
            case "xlsx":
                throw new RuntimeException("not implemented");
            default:
                throw new FileExtensionException("Wrong extension. File should be a json, excel or csv file. (.json, .csv, .xls, .xlsx)");
        }
    }

    private void updateKeys(HashMap keys) {
        for (Object o : keys.keySet()) {
            if (LinkedHashMap.class.equals(keys.get(o).getClass())) {
                HashMap newLevel = new HashMap();
                HashMap currentLevel = (HashMap) keys.get(o);
                currentLevel.keySet().forEach(key -> {
                    newLevel.put(o + "." + key, currentLevel.get(key));
                });
                updateKeys(newLevel);
            } else {
                keyService.addKey(o.toString().toUpperCase());
            }
        }
    }
}
