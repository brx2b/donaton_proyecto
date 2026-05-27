package com.donaciones.api_donaciones.service.impl;

import com.donaciones.api_donaciones.model.DonacionesModel;
import com.donaciones.api_donaciones.service.DonacionesProcesador;
import org.springframework.stereotype.Component;

@Component
public class EmpresaProcesador implements DonacionesProcesador {

    @Override
    public void procesar(DonacionesModel donacion) { //se utiiza el metodo procesar para utilizar los datos ingresados
        System.out.println("Procesando donacion corporativa...");
        if(donacion.getMonto()<10000){ //si la donacion en menor a 10.000 tira el error
            throw new IllegalArgumentException("Las donaciones de empresa deben ser superiores a $10.000");
        }
        System.out.println("Donacion procesada exitosamente");
    }

    @Override
    public String getTipo() {
        return "EMPRESA";
    } //si el tipo que recibe es empresa
}
