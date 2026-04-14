package com.donaciones.api_donaciones.service.impl;

import com.donaciones.api_donaciones.model.DonacionesModel;
import com.donaciones.api_donaciones.service.DonacionesProcesador;
import org.springframework.stereotype.Component;

@Component
public class EmpresaProcesador implements DonacionesProcesador {

    @Override
    public void procesar(DonacionesModel donacion) {
        System.out.println("Procesando donacion corporativa...");
        if(donacion.getMonto()<10000){
            throw new IllegalArgumentException("Las donaciones de empresa deben ser superiores a $10.000");
        }
        System.out.println("Donacion procesada exitosamente");
    }

    @Override
    public String getTipo() {
        return "EMPRESA";
    }
}
