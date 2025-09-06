package com.aej.cecarticulo.services;

import com.aej.cecarticulo.model.ArxivEntry;
import com.aej.cecarticulo.model.ArxivFeed;

import java.util.List;

public interface IProcessArticles {
    void processAndSaveArticles(List<ArxivEntry> articles);
}
