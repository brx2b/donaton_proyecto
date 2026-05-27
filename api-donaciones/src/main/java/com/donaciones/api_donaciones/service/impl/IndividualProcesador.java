package com.donaciones.api_donaciones.service.impl;

import com.donaciones.api_donaciones.model.DonacionesModel;
import com.donaciones.api_donaciones.service.DonacionesProcesador;
import org.springframework.stereotype.Component;

@Component
public class IndividualProcesador implements DonacionesProcesador {
    @Override
    public void procesar(DonacionesModel donacion) { //se utiiza el metodo procesar para utilizar los datos ingresados
        System.out.println("Procesando donación inidividual");
        // Lógica para personas naturales
        if(donacion.getMonto()<=1000){ //si la donacion es inferior a 1.000
            throw new IllegalArgumentException("Las donaciones deben ser superiores a $1.000");
        }
        System.out.println("Donacion procesada exitosamente");
        // mensaje de exito
    }

    @Override
    public String getTipo() {
        return "INDIVIDUAL";
    } //si el tipo que recibe es individual
}
