package com.necesidades.api_necesidades.model;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "necesidades")
@Data
public class NecesidadesModel {
    @Id
    private String id;
    private String usuarioId;
    @NotBlank(message = "La descripción no puede quedar vacía")
    private String desc;
    private String fecha;
    @NotBlank(message = "Se debe especificar la sede")
    private String sede;
}
