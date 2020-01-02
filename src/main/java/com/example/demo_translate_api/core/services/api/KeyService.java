package com.example.demo_translate_api.core.services.api;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface KeyService {
    List<String> getKeys();
    List<String> getKeys(String locale);
    ConcurrentHashMap<String, List<String>> getMissingKeys();
    List<String> updateKeys();
    List<String> updateKeys(String locale);
    List<String> deleteKeys(String[] keys);
    String deleteKey(String key);
    List<String> addKey(String key);
}
