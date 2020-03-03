package be.geo_solutions.translate_api.core.services.api;

import org.springframework.core.io.Resource;
import javax.servlet.http.HttpServletResponse;

public interface DownloadService {
    byte[] downloadTranslationsExcel(HttpServletResponse response);
    Resource downloadTranslationsCsv(HttpServletResponse response);
}
