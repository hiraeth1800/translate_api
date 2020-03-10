INSERT INTO language (locale) VALUES ('nl');
INSERT INTO language (locale) VALUES ('fr');
INSERT INTO language (locale) VALUES ('de');


INSERT INTO TRANSLATION (key, value, language_id) VALUES ('HOME', 'Home', 1);
INSERT INTO TRANSLATION (key, value, language_id) VALUES ('KEY.TO.DELETE.IN.TEST', 'deleted translation', 1);
INSERT INTO TRANSLATION (key, value, language_id) VALUES ('KEY.TO.UPDATE.IN.TEST', 'false translation', 1);


INSERT INTO TRANSLATION (key, value, language_id) VALUES ('HOME', 'Home nl', 2);
INSERT INTO TRANSLATION (key, value, language_id) VALUES ('KEY.NL', 'Home nl', 2);

INSERT INTO TRANSLATION (key, value, language_id) VALUES ('HOME', 'german', 4);
INSERT INTO TRANSLATION (key, value, language_id) VALUES ('KEY.NL', 'german', 4);
INSERT INTO TRANSLATION (key, value, language_id) VALUES ('KEY.TO.DELETE.IN.TEST', 'german', 4);
INSERT INTO TRANSLATION (key, value, language_id) VALUES ('KEY.TO.UPDATE.IN.TEST', 'german', 4);
