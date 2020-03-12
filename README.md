# translate_api
A spring boot rest API that can be used in front end applications to translate over http. This allows to manage the translations without having to recompile the frontend when changing translations.
Uon creating a new language the existing keys are not added to the language to prevent from having blank text by default

# rest endpoints
![alt text](https://i.gyazo.com/cb368e59f3a56a9fd4fb325dc308942f.png)

{key} -> 
{
    "locale": "",
    "key": ""
}

{translation} ->
{
    "locale": "",
    "key": "",
    "translation": ""
}

{language} ->
json object with keys as properties and translations as value


