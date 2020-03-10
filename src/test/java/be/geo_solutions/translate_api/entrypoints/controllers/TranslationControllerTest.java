package be.geo_solutions.translate_api.entrypoints.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TranslationControllerTest {
    private MockMvc mvc;

    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    public void setup(){
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @Transactional
    public void getTranslation() throws Exception {
        String url = "/api/translations/get";
        String body = "{\"locale\": \"en\", \"key\": \"HOME\"}";

        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void getTranslationWithInvalidBody() throws Exception {
        String url = "/api/translations/get";

        mvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void getTranslationWithInvalidLocale() throws Exception {
        String locale = "locale_that_doesnt_exist";
        String body = "{\"locale\": \"" + locale + "\", \"key\": \"HOME\"}";
        String expectedResponse = "No language found with locale " + locale;

        mvc.perform(get("/api/translations/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void getTranslationWithInvalidKey() throws Exception {
        String url = "/api/translations/get";
        String locale = "en";
        String key = "NON.EXISTING.KEY";
        String body = "{\"locale\": \"" + locale + "\", \"key\": \"" + key + "\"}";
        String expectedResponse = "No key " + key + " found for locale " + locale;

        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void addTranslation() throws Exception {
        String url = "/api/translations/add";
        String body = "{\"locale\": \"en\", \"key\": \"NEW.KEY\", \"translation\": \"New translation\"}";
        String expectedResponse = "{\"locale\":\"en\",\"key\":\"NEW.KEY\",\"translation\":\"New translation\"}";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse))
                .andExpect(jsonPath("$.locale").value("en"))
                .andExpect(jsonPath("$.key").value("NEW.KEY"))
                .andExpect(jsonPath("$.translation").value("New translation"));
    }

    @Test
    @Transactional
    public void addTranslationInvalidBody() throws Exception {
        String url = "/api/translations/add";
        String body = "{}";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void addTranslationWithInvalidLocale() throws Exception {
        String url = "/api/translations/add";
        String locale = "locale_that_doesnt_exist";
        String body = "{\"locale\": \"" + locale + "\", \"key\": \"NEW.KEY\", \"translation\": \"New translation\"}";
        String expectedResponse = "No language found with locale " + locale;

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void addTranslationWithInvalidKey() throws Exception {
        String url = "/api/translations/add";
        String key = "HOME";
        String body = "{\"locale\": \"en\", \"key\": \"" +  key + "\", \"translation\": \"New translation\"}";
        String expectedResponse = "Translation with key " + key + " already exists";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void updateTranslation() throws Exception {
        String url = "/api/translations/update";
        String updatedTranslation = "Updated translation";
        String body = "{\"locale\": \"en\", \"key\": \"KEY.TO.UPDATE.IN.TEST\", \"translation\": \"" + updatedTranslation + "\"}";
        String expectedResponse = "{\"locale\":\"en\",\"key\":\"KEY.TO.UPDATE.IN.TEST\",\"translation\":\"" + updatedTranslation + "\"}";

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse))
                .andExpect(jsonPath("$.translation").value(updatedTranslation));
    }

    @Test
    @Transactional
    public void updateTranslationWithInvalidBody() throws Exception {
        String url = "/api/translations/update";
        String body = "{}";

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(builder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void updateTranslationWithInvalidLocale() throws Exception {
        String url = "/api/translations/update";
        String locale = "locale_that_doesnt_exist";
        String body = "{\"locale\": \"" + locale + "\", \"key\": \"KEY.TO.UPDATE.IN.TEST\", \"translation\": \"test\"}";
        String expectedResponse = "No language found with locale " + locale;

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(builder)
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void updateTranslationWithInvalidKey() throws Exception {
        String url = "/api/translations/update";
        String key = "INVALID.KEY";
        String body = "{\"locale\": \"en\", \"key\": \"" + key + "\", \"translation\": \"test\"}";
        String expectedResponse = "No key " + key + " found for locale en";

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(builder)
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void deleteTranslation() throws Exception {
        String url = "/api/translations/delete";
        String body = "{\"locale\": \"en\", \"key\": \"KEY.TO.DELETE.IN.TEST\"}";
        String expectedResponse = "{\"locale\":\"en\",\"key\":\"KEY.TO.DELETE.IN.TEST\",\"translation\":\"deleted translation\"}";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Transactional
    public void deleteTranslationWithInvalidBody() throws Exception {
        String url = "/api/translations/delete";
        String body = "{}";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    public void deleteTranslationWithInvalidLocale() throws Exception {
        String url = "/api/translations/delete";
        String locale = "locale_that_doesnt_exist";
        String body = "{\"locale\": \"" + locale + "\", \"key\": \"KEY.TO.DELETE.IN.TEST\"}";
        String expectedResponse = "No language found with locale " + locale;

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void deleteTranslationWithInvalidKey() throws Exception {
        String url = "/api/translations/delete";
        String key = "INVALID.KEY";
        String body = "{\"locale\": \"en\", \"key\": \"" + key + "\"}";
        String expectedResponse = "No key " + key + " found for locale en";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }
}
