package com.donaciones.api_donaciones.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "donaciones")
@Data
public class DonacionesModel {
    @Id
    private String id;
    @NotBlank(message = "No puede quedar vacío el monto")
    private Double monto;
    private String usuarioId;
    private String fecha;
}
