package com.donaciones.api_donaciones.service.impl;

import com.donaciones.api_donaciones.model.DonacionesModel;
import com.donaciones.api_donaciones.service.DonacionesProcesador;
import org.springframework.stereotype.Component;

@Component
public class IndividualProcesador implements DonacionesProcesador {
    @Override
    public void procesar(DonacionesModel donacion) {
        // Lógica para personas naturales
        if(donacion.getMonto()<=1000){
            throw new IllegalArgumentException("Las donaciones deben ser superiores a $1.000");
        }
        System.out.println("Donacion procesada exitosamente");
        // mensaje de exito
    }

    @Override
    public String getTipo() {
        return "INDIVIDUAL";
    }
}
