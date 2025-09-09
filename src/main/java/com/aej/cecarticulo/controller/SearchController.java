package com.aej.cecarticulo.controller;


import com.aej.cecarticulo.dao.ArticuloRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class SearchController {

    @Autowired
    IArticuloService articuloService;

    @Autowired
    IProcessArticles processArticles;
    @Autowired
    private ArticuloRepository articuloRepository;

    @GetMapping(value = "/articles", produces = "application/json")
    public ResponseEntity<Page<ArticuloModel>> getArticulos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int paginaSpringData = page - 1;
        Page<ArticuloModel> ats = articuloService.getArticulos(paginaSpringData, size);

            for(ArticuloModel articulo: ats.getContent() ) {
                articulo.setText("");
            }
        return ResponseEntity.ok(ats);
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticuloModel> getArticuloText(
        @PathVariable("id") String id
    ) {
       Optional<ArticuloModel> art = articuloRepository.findById(id);
        return ResponseEntity.ok(art.get());
    }

    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity<String> Search(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int maxResults

    ) {
        SearchArticlesDTO arts = articuloService.SearchArticles(query, maxResults);
        processArticles.processAndSaveArticles(arts.getArticles());

        return ResponseEntity.ok("Se han encontrado " + arts.getCount() + " articulos " );
    }

    @GetMapping(value = "/progress", produces = "application/json")
    public ProgressStatus progress() {
        return processArticles.getProgress();
    }


}
