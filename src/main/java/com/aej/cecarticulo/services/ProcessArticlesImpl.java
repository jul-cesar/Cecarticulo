package com.aej.cecarticulo.services;

import com.aej.cecarticulo.model.ArxivEntry;

import com.aej.cecarticulo.model.ProgressStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
public class ProcessArticlesImpl implements IProcessArticles {

    private int total;
    private int procesados;
    private long startTime;
    private long endTime;
    @Value("${app.threads}")
    private int threads;
    @Autowired
    private IArticuloService articuloService;

    @Autowired
    ExecutorService executor;

    CountDownLatch latch = new CountDownLatch(this.threads);

    @Override
    public void processAndSaveArticles(List<ArxivEntry> articles) {
        this.total = articles.size();
        this.procesados = 0;
        this.startTime = System.currentTimeMillis();
        BlockingQueue<ArxivEntry> queue = new LinkedBlockingQueue<>(articles);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    ArxivEntry entry;
                    while ((entry = queue.poll(2, TimeUnit.SECONDS)) != null) {
                        articuloService.ProcessAndSave(entry);
                        procesados++;
                    }
                    this.endTime = System.currentTimeMillis();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                latch.countDown();
            }
            });
        }
        try {
            latch.await();
            this.endTime = System.currentTimeMillis();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
@Override
    public ProgressStatus getProgress() {
        ProgressStatus status = new ProgressStatus();
        status.setTotal(total);
        status.setProcesados(procesados);
            long tiempo = ( endTime - startTime ) / 1000;
            status.setTiempoSegundos(tiempo >= 0 ? tiempo : 0);
        return status;
    }


}
