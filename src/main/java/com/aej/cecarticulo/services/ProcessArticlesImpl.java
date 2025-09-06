package com.aej.cecarticulo.services;

import com.aej.cecarticulo.model.ArxivEntry;
import com.aej.cecarticulo.model.ArxivFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
public class ProcessArticlesImpl implements IProcessArticles {
    @Value("${app.threads}")
    private int threads;
    @Autowired
    private IArticuloService articuloService;

    @Autowired
    ExecutorService executor;

    @Override
    public void processAndSaveArticles(List<ArxivEntry> articles) {
        BlockingQueue<ArxivEntry> queue = new LinkedBlockingQueue<>(articles);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    ArxivEntry entry;
                    while ((entry = queue.poll(2, TimeUnit.SECONDS)) != null) {
                        articuloService.ProcessAndSave(entry);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }


}
