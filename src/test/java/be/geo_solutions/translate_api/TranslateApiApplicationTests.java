package be.geo_solutions.translate_api;

import be.geo_solutions.translate_api.core.services.impl.KeyServiceImplTest;
import be.geo_solutions.translate_api.core.services.impl.LanguageServiceImplTest;
import be.geo_solutions.translate_api.core.services.impl.TranslationServiceImplTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
        KeyServiceImplTest.class,
        LanguageServiceImplTest.class,
        TranslationServiceImplTest.class
})
public class TranslateApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
