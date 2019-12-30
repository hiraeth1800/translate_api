package com.example.demo_translate_api.core.gateways;

import com.example.demo_translate_api.core.model.Language;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LanguageGateway extends Gateway<Language> {
    List<Language> findAll();
    Language findByLocale(String locale);
}
