package com.logistica.api_logistica.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "logistica")
@Data
public class LogisticaModel {
    @Id
    private String id;
    private List<ElementoModel> carga;
    @NotBlank
    private String chofer;
    @NotBlank
    private String matricula;
    @NotBlank
    private String destino;
    @NotBlank
    private String origen;
}
