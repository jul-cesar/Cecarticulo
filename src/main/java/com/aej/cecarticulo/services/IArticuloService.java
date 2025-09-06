package com.aej.cecarticulo.services;


import com.aej.cecarticulo.dto.SearchArticlesDTO;
import com.aej.cecarticulo.model.ArticuloModel;
import com.aej.cecarticulo.model.ArxivEntry;
import com.aej.cecarticulo.model.ArxivFeed;
import com.aej.cecarticulo.model.ProgressStatus;

import java.util.List;

public interface IArticuloService {
    List<ArticuloModel> getArticulos();

    SearchArticlesDTO SearchArticles(String query, int maxResults);

    void  ProcessAndSave(ArxivEntry entry);
    void SaveEntriesToMongo(ArxivEntry entry);

    List<String> extractImagesFromPdf(byte[] pdfBytes);

    List<String> generateKeywordsLLM( String resume);


    byte[] DowloadPdf(String Url, String filename);
    String extractTextFromPdf(byte[] pdfBytes);
}
