package com.donaciones.api_donaciones.service;

import com.donaciones.api_donaciones.model.DonacionesModel;

public interface DonacionesProcesador {
    void procesar(DonacionesModel donacion);

    String getTipo();
}
