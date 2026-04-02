package com.donaciones.api_donaciones.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "donaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonacionesModel {
    @Id
    private String id;
    private Double monto;
    private String usuarioId;
    private String fecha;
}
