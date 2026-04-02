package com.necesidades.api_necesidades.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "necesidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NecesidadesModel {
    @Id
    private String id;
    private String usuarioId;
    private String desc;
    private String fecha;
    private String sede;
}
