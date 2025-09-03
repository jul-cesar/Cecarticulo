package com.aej.cecarticulo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class ArxivEntry {
    private String title;
    private String summary;
    private String published;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Author> author;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Category> category;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Link> link;

    @Data
    public static class Author {
        private String name;
    }

    @Data
    public static class Category {
        @JacksonXmlProperty(isAttribute = true)
        private String term;
    }

    @Data
    public static class Link {
        @JacksonXmlProperty(isAttribute = true)
        private String href;
        @JacksonXmlProperty(isAttribute = true)
        private String type;
        @JacksonXmlProperty(isAttribute = true)
        private String title;
    }
}