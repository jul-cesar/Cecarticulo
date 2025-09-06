package com.aej.cecarticulo.services;


import com.aej.cecarticulo.dto.SearchArticlesDTO;
import com.aej.cecarticulo.model.ArticuloModel;
import com.aej.cecarticulo.model.ArxivEntry;
import com.aej.cecarticulo.model.ArxivFeed;
import com.aej.cecarticulo.model.ProgressStatus;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface IArticuloService {
    Page<ArticuloModel> getArticulos(int page, int size);

    SearchArticlesDTO SearchArticles(String query, int maxResults);

    void  ProcessAndSave(ArxivEntry entry);
    void SaveEntriesToMongo(ArxivEntry entry, String text, List<String> imgs, List<String> keywords);

    List<String> extractImagesFromPdf(byte[] pdfBytes);

    List<String> generateKeywordsLLM( String resume);


    byte[] DowloadPdf(String Url, String filename);
    String extractTextFromPdf(byte[] pdfBytes);
}
