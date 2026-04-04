package com.donaciones.api_donaciones.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "donaciones")
@Data
public class DonacionesModel {
    @Id
    private String id;
    private String usuarioId;
    @NotNull(message = "No puede quedar vacío") //evita nulo
    @Positive(message = "Mayor a 0 solamente") //evita ingresar un numero negativo o igual a 0
    private Double monto;

    private String fecha;
}
