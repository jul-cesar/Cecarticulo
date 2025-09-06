package com.aej.cecarticulo.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Articulos")
public class ArticuloModel {
    @Id
    private String id;
    private String title;
    private String summary;
    private String publishedDate;
    private List<String> authors;
    private List<String> categories;
    private String pdfUrl;
    private String text;
    private List<byte[]> images;
    private List<String> keywords;




}
