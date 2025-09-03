package com.aej.cecarticulo.services;


import com.aej.cecarticulo.model.ArticuloModel;

import java.util.List;

public interface IArticuloService {
    public List<ArticuloModel>  getArticulos();
    public int SearchAndSaveArticles(String query,int maxResults);
}
