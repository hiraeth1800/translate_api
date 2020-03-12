package be.geo_solutions.translate_api.entrypoints.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class LanguageControllerTest {

    private MockMvc mvc;

    @Value("${message.error.duplicate-language}")
    private String duplicateLanguageMessage;

    @Autowired
    WebApplicationContext wac;

    private String locales[];

    @BeforeEach
    public void setup(){
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        locales = new String[]{"en", "nl", "fr", "de"};
    }

    @Test
    @Transactional
    public void getLanguages() throws Exception {
        String url = "/api/languages";
        List<String> expectedLocales = new ArrayList<>(Arrays.asList(locales));

        mvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(locales.length)))
                .andExpect(jsonPath("$", is(expectedLocales)));
    }

    @Test
    @Transactional
    public void getLanguage() throws Exception {
        String url = "/api/languages/en";
        ConcurrentHashMap<String, String> englishTranslations = new ConcurrentHashMap<>();
        englishTranslations.put("HOME", "Home");
        englishTranslations.put("KEY.TO.DELETE.IN.TEST", "deleted translation");
        englishTranslations.put("KEY.TO.UPDATE.IN.TEST", "false translation");

        mvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(englishTranslations)));
    }

    @Test
    @Transactional
    public void getLanguageBadRequest() throws Exception {
        String url = "/api/languages/";
        String locale = "locale_that_doesnt_exist";
        String expectedResponse = "No language found with locale " + locale;

        mvc.perform(get(url + locale)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void addLanguage() throws Exception {
        String url = "/api/languages/add";
        String locale = "es";

        mvc.perform(post(url)
                .content(locale)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void addLanguageThatAlreadyExists() throws Exception {
        String url = "/api/languages/add";
        String locale = "en";
        String expectedResponse = duplicateLanguageMessage.replace("{locale}", locale);

        mvc.perform(post(url)
                .content(locale)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void deleteLanguage() throws Exception {
        String url = "/api/languages/delete";
        String locale = "fr";

        mvc.perform(post(url)
                .content(locale)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response", is(locale)));
    }

    @Test
    @Transactional
    public void deleteLanguageThatDoesntExists() throws Exception {
        String url = "/api/languages/delete";
        String locale = "locale_that_doesnt_exist";
        String expectedResponse = "No language found with locale " + locale;

        mvc.perform(post(url)
                .content(locale)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }
}
