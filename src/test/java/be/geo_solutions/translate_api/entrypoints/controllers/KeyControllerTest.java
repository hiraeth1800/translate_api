package be.geo_solutions.translate_api.entrypoints.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class KeyControllerTest {
    private MockMvc mvc;

    @Autowired
    WebApplicationContext wac;

    List<String> keys;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        keys = new ArrayList<>();
        keys.add("HOME");
        keys.add("KEY.TO.DELETE.IN.TEST");
        keys.add("KEY.TO.UPDATE.IN.TEST");
        keys.add("KEY.NL");
    }

    @Test
    @Transactional
    public void getKeys() throws Exception {
        String url = "/api/keys";

        mvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(keys.size())))
                .andExpect(jsonPath("$[0]").value(keys.get(0)))
                .andExpect(jsonPath("$[1]").value(keys.get(3)))
                .andExpect(jsonPath("$[2]").value(keys.get(1)))
                .andExpect(jsonPath("$[3]").value(keys.get(2)));
    }

    @Test
    @Transactional
    public void getKeysByLocale() throws Exception {
        String url = "/api/keys/";
        String enLocale = "en";
        String enKeys[] = { "HOME", "KEY.TO.DELETE.IN.TEST", "KEY.TO.UPDATE.IN.TEST" };

        mvc.perform(get(url + enLocale)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(enKeys.length)))
                .andExpect(jsonPath("$", containsInAnyOrder(enKeys[0], enKeys[1], enKeys[2])));

        String nlLocale = "nl";
        String nlKeys[] = { "HOME", "KEY.NL" };

        mvc.perform(get(url + nlLocale)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(nlKeys.length)))
                .andExpect(jsonPath("$", containsInAnyOrder(nlKeys[0], nlKeys[1])));
    }

    @Test
    @Transactional
    public void getKeysByLocaleWithInvalidLocale() throws Exception {
        String url = "/api/keys/";
        String locale = "locale_that_doesnt_exist";
        String expectedResponse = "No language found with locale " + locale;

        mvc.perform(get(url + locale)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void getMissingKeys() throws Exception {
        String url = "/api/keys/missing";

        mvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.en", hasSize(1)))
                .andExpect(jsonPath("$.en", containsInAnyOrder("KEY.NL")))
                .andExpect(jsonPath("$.nl", hasSize(2)))
                .andExpect(jsonPath("$.nl", containsInAnyOrder("KEY.TO.DELETE.IN.TEST", "KEY.TO.UPDATE.IN.TEST")))
                .andExpect(jsonPath("$.fr", hasSize(4)))
                .andExpect(jsonPath("$.fr", containsInAnyOrder("KEY.NL", "HOME", "KEY.TO.UPDATE.IN.TEST", "KEY.TO.DELETE.IN.TEST")));
    }

    @Test
    @Transactional
    public void updateKeys() throws Exception {
        String url = "/api/keys/update";
        String verifyUrl = "/api/keys/missing";

        mvc.perform(post(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", containsInAnyOrder("en", "fr", "nl")));

        mvc.perform(get(verifyUrl)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    @Test
    @Transactional
    public void updateKeysByLocale() throws Exception {
        String url = "/api/keys/update/nl";
        String verifyUrl = "/api/keys/missing";

        mvc.perform(post(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", containsInAnyOrder("KEY.TO.DELETE.IN.TEST", "KEY.TO.UPDATE.IN.TEST")));

        mvc.perform(get(verifyUrl)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nl").doesNotExist())
                .andExpect(jsonPath("$.en", hasSize(1)))
                .andExpect(jsonPath("$.en", containsInAnyOrder("KEY.NL")))
                .andExpect(jsonPath("$.fr", hasSize(4)))
                .andExpect(jsonPath("$.fr", containsInAnyOrder("KEY.NL", "HOME", "KEY.TO.UPDATE.IN.TEST", "KEY.TO.DELETE.IN.TEST")));;
    }

    @Test
    @Transactional
    public void updateKeysByLocaleWithInvalidLocale() throws Exception {
        String locale = "locale_that_doesnt_exist";
        String url = "/api/keys/update/" + locale;
        String expectedResponse = "No language found with locale " + locale;

        mvc.perform(post(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Transactional
    public void addKey() throws Exception {
        String url = "/api/keys/add";
        String body = "ADDED.KEY";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$", containsInAnyOrder("en", "nl", "fr", "de")));
    }

    @Test
    @Transactional
    public void deleteKey() throws Exception {
        String key = "KEY.TO.DELETE.IN.TEST";
        String url = "/api/keys/delete/" + key;
        String verifyUrl = "/api/keys";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(key));

        String enKeys[] = { "HOME", "KEY.TO.UPDATE.IN.TEST", "KEY.NL" };

        mvc.perform(get(verifyUrl)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(enKeys.length)))
                .andExpect(jsonPath("$", containsInAnyOrder(enKeys)));
    }

    @Test
    @Transactional
    public void deleteKeys() throws Exception {
        String url = "/api/keys/delete";
        String body = "[\"KEY.TO.DELETE.IN.TEST\", \"KEY.TO.UPDATE.IN.TEST\"]";
        String verifyUrl = "/api/keys";

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        String notDeletedKeys[] = { "HOME", "KEY.NL" };
        mvc.perform(get(verifyUrl)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(notDeletedKeys.length)))
                .andExpect(jsonPath("$", containsInAnyOrder(notDeletedKeys)));
    }
}
