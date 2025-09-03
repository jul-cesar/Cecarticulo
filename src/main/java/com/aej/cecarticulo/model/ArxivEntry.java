package com.aej.cecarticulo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora cualquier campo que no mapeemos
public class ArxivEntry {

    // Campos básicos
    private String id;
    private String updated;
    private String published;
    private String title;
    private String summary;

    // Autores
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Author> author;

    // Categorías
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Category> category;

    // Links (HTML, PDF, DOI, etc.)
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Link> link;

    // Campos específicos de arXiv
    @JacksonXmlProperty(localName = "doi", namespace = "http://arxiv.org/schemas/atom")
    private String doi;

    @JacksonXmlProperty(localName = "comment", namespace = "http://arxiv.org/schemas/atom")
    private String comment;

    @JacksonXmlProperty(localName = "journal_ref", namespace = "http://arxiv.org/schemas/atom")
    private String journalRef;

    @JacksonXmlProperty(localName = "primary_category", namespace = "http://arxiv.org/schemas/atom")
    private PrimaryCategory primaryCategory;

    // ------------------ Clases internas ------------------

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private String name;
    }

    @Data
    public static class Category {
        @JacksonXmlProperty(isAttribute = true)
        private String term;

        @JacksonXmlProperty(isAttribute = true)
        private String scheme;
    }

    @Data
    public static class Link {
        @JacksonXmlProperty(isAttribute = true)
        private String href;

        @JacksonXmlProperty(isAttribute = true)
        private String type;

        @JacksonXmlProperty(isAttribute = true)
        private String rel;

        @JacksonXmlProperty(isAttribute = true)
        private String title;
    }

    @Data
    public static class PrimaryCategory {
        @JacksonXmlProperty(isAttribute = true)
        private String term;

        @JacksonXmlProperty(isAttribute = true)
        private String scheme;
    }
}