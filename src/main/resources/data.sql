INSERT INTO LANGUAGE (language_id, locale) VALUES (1, 'en');
INSERT INTO LANGUAGE (language_id, locale) VALUES (2, 'nl');
INSERT INTO LANGUAGE (language_id, locale) VALUES (3, 'fr');

INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (1, 'HOME', 'Home', 1);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (2, 'EDIT', 'Edit', 1);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (3, 'LANGUAGE', 'Language', 1);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (4, 'DESCRIPTION', 'This text should be translated!', 1);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (5, 'HOME', 'Start', 2);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (6, 'EDIT', 'Wijzig', 2);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (7, 'LANGUAGE', 'Taal', 2);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (8, 'DESCRIPTION', 'Deze tekst is vertaald!', 2);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (9, 'HOME', 'Home', 3);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (10, 'EDIT', 'Editer', 3);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (11, 'LANGUAGE', 'Langue', 3);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (12, 'DESCRIPTION', 'Ce texte est taduite!', 3);
INSERT INTO TRANSLATION (translation_id, key, value, language_language_id) VALUES (13, 'HOME_FR', 'home_fr', 3);
