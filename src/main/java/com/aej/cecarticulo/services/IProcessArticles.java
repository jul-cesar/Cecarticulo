package com.aej.cecarticulo.services;

import com.aej.cecarticulo.model.ArxivEntry;
import com.aej.cecarticulo.model.ArxivFeed;
import com.aej.cecarticulo.model.ProgressStatus;

import java.util.List;

public interface IProcessArticles {
    void processAndSaveArticles(List<ArxivEntry> articles);
    ProgressStatus getProgress();
}
