package com.aej.cecarticulo.controller;


import com.aej.cecarticulo.model.ArticuloModel;

import com.aej.cecarticulo.services.IArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    IArticuloService articuloService;

    @GetMapping(value = "/articles",  produces = "application/json")
    public ResponseEntity<List<ArticuloModel>>getArticulos(){
        List<ArticuloModel> ats = articuloService.getArticulos();
        return ResponseEntity.ok(ats);
    }
    @GetMapping("/search")
    public ResponseEntity<String> Search(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int maxResults
    ) {
        int count = articuloService.SearchAndSaveArticles(query, maxResults);
        return ResponseEntity.ok("✅ Se encontraron " + count + " artículos para: " + query);
    }


}
