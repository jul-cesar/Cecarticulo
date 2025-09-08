package com.aej.cecarticulo.dto;

import com.aej.cecarticulo.model.ArxivEntry;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class SearchArticlesDTO {

    private List<ArxivEntry> articles;
    private int count;
}
