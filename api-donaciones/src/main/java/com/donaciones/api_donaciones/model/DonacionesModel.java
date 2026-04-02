package com.donaciones.api_donaciones.model;

import org.springframework.data.annotation.Id;

public class DonacionesModel {
    @Id
    private String id;
    private String nombre;
    private String email;
    private String rol;
    private String ubicacion;
}
