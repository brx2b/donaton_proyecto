package com.inventario.api_inventario.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import java.util.List;

@Data
public class InventarioModel {
    @Id
    private String id;
    @NotBlank(message = "No puede quedar vacío")
    private String sede;
    private List<ElementoModel> elementos;
}
