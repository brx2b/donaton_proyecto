package com.donaciones.api_donaciones.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class DonacionFactory {
    private final Map<String, DonacionesProcesador> procesadores = new HashMap<>();
    @Autowired
    public DonacionFactory(List<DonacionesProcesador> listaProcesadores){
        for (DonacionesProcesador p: listaProcesadores){
            procesadores.put(p.getTipo().toUpperCase(), p);
        }
    }

    public DonacionesProcesador getProcesador(String tipo){
        DonacionesProcesador procesador = procesadores.get(tipo.toUpperCase());
        if(procesador==null){
            throw new IllegalArgumentException("Tipo de donacion no soportada: "+tipo);
        }
        return procesador;
    }
}
