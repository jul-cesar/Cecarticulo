package com.aej.cecarticulo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "feed") // raíz del XML
public class ArxivFeed {
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ArxivEntry> entry;
}



