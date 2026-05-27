package com.donaciones.api_donaciones.service;

import com.donaciones.api_donaciones.model.DonacionesModel;

public interface DonacionesProcesador {
    void procesar(DonacionesModel donacion); //metodo que contiene los datos ingresados

    String getTipo(); //metodo que obtiene el tipo de donacion
}
