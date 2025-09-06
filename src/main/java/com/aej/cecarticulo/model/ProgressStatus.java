package com.aej.cecarticulo.model;

import lombok.Data;

@Data
public class ProgressStatus {
    private int total;
    private int procesados;
    private long tiempoSegundos;

}
