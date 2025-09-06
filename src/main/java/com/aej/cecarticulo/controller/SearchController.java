package com.aej.cecarticulo.controller;


import com.aej.cecarticulo.dto.SearchArticlesDTO;
import com.aej.cecarticulo.model.ArticuloModel;

import com.aej.cecarticulo.model.ArxivEntry;
import com.aej.cecarticulo.model.ProgressStatus;
import com.aej.cecarticulo.services.IArticuloService;
import com.aej.cecarticulo.services.IProcessArticles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    IArticuloService articuloService;

    @Autowired
    IProcessArticles processArticles;

    @GetMapping(value = "/articles",  produces = "application/json")
    public ResponseEntity<Page<ArticuloModel>>getArticulos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        Page<ArticuloModel> ats = articuloService.getArticulos(page,size);
        return ResponseEntity.ok(ats);
    }
    @GetMapping("/search")
    public ResponseEntity<String> Search(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int maxResults
    ) {
         SearchArticlesDTO arts =  articuloService.SearchArticles(query, maxResults);
        processArticles.processAndSaveArticles(arts.getArticles());

        return ResponseEntity.ok("✅ Se encontraron " + arts.getCount() + " artículos para: " + query);
    }

    @GetMapping(value = "/progress", produces = "application/json")
    public ProgressStatus progress() {
        return processArticles.getProgress();
    }



}
