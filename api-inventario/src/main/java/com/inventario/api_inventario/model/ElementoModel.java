package com.inventario.api_inventario.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ElementoModel {
    @NotBlank
    private String nombre;
    @NotNull
    @Positive
    private int cantidad;

}
