package com.example.demo_translate_api.core.gateways;

import java.util.List;

public interface Gateway<T> {
    List<T> findAll();

    <S extends T> S save(S object);

    T deleteById(int id);
}
