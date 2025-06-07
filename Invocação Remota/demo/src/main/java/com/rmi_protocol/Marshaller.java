package com.rmi_protocol;

import com.entidades.Capa;
import com.entidades.Celular;
import com.entidades.Pelicula; 
import com.entidades.PowerBank;
import com.entidades.Produto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class Marshaller {
    
    private final Gson gson;

    public Marshaller() {
        RuntimeTypeAdapterFactory<Produto> typeFactory = RuntimeTypeAdapterFactory
                .of(Produto.class, "type")
                .registerSubtype(Celular.class, "celular") 
                .registerSubtype(Capa.class, "capa")      
                .registerSubtype(Pelicula.class, "pelicula") 
                .registerSubtype(PowerBank.class, "powerbank");

        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(typeFactory)
                .create();
    }

    public byte[] empacotar(Object obj) {
        return gson.toJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    public byte[] empacotar(Object obj, Type tipo) {
        return gson.toJson(obj, tipo).getBytes(StandardCharsets.UTF_8);
    }

    public <T> T desempacotar(byte[] dados, Class<T> clazz) {
        return gson.fromJson(new String(dados, StandardCharsets.UTF_8), clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T desempacotar(byte[] dados, Type tipo) {
        return (T) gson.fromJson(new String(dados, StandardCharsets.UTF_8), tipo);
    }
}