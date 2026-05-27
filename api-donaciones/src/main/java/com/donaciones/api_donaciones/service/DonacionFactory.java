package com.donaciones.api_donaciones.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class DonacionFactory {
    private final Map<String, DonacionesProcesador> procesadores = new HashMap<>(); //obtiene los datos ingresados de la donacion y los mapea
    @Autowired
    public DonacionFactory(List<DonacionesProcesador> listaProcesadores){
        for (DonacionesProcesador p: listaProcesadores){ //recorre la lista
            procesadores.put(p.getTipo().toUpperCase(), p); //se guarda el tipo a la lista en mayus para no guardar datos ambiguos
        }
    }

    public DonacionesProcesador getProcesador(String tipo){
        DonacionesProcesador procesador = procesadores.get(tipo.toUpperCase()); //obtiene el tipo para realizar verificacion
        if(procesador==null){
            throw new IllegalArgumentException("Tipo de donacion no soportada: "+tipo);
        }
        return procesador;
    }
}
