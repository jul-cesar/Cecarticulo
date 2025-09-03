package com.aej.cecarticulo.services;
import com.aej.cecarticulo.dao.ArticuloRepository;
import com.aej.cecarticulo.model.ArticuloModel;
import com.aej.cecarticulo.model.ArxivEntry;
import com.aej.cecarticulo.model.ArxivFeed;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import java.net.URI;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ArticuloServiceImpl implements IArticuloService {
    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private RestTemplate client;

    private final String BASE_URL  = "https://export.arxiv.org/api/query?search_query=all:%s&start=0&max_results=%d";


    public String getArxivXml(String url) {
        return client.getForObject(url, String.class);
    }

    @Override
    public List<ArticuloModel> getArticulos() {
        return articuloRepository.findAll();
    }

    @Override
        public int SearchAndSaveArticles(String query, int maxResults) {
            try {


                String url = String.format(BASE_URL, query.replace(" ", "+"), maxResults);

                URI uri = URI.create(url);


                String xmlContent = getArxivXml(url);


                Path filePath = Path.of("downloads", "arxiv_" + query + ".xml");
                Files.createDirectories(filePath.getParent());
                Files.writeString(filePath, xmlContent);

                XmlMapper xmlMapper = new XmlMapper();
                ArxivFeed feed = xmlMapper.readValue(xmlContent, ArxivFeed.class);



                for (ArxivEntry entry : feed.getEntry()) {
                    ArticuloModel articulo = new ArticuloModel();
                    articulo.setTitle(entry.getTitle());
                    articulo.setSummary(entry.getSummary());
                    articulo.setPublishedDate(entry.getPublished());
                    articulo.setAuthors(entry.getAuthor().stream().map(ArxivEntry.Author::getName).toList());
                    articulo.setCategories(entry.getCategory().stream().map(ArxivEntry.Category::getTerm).toList());

                    // Buscar link PDF
                    entry.getLink().stream()
                            .filter(l -> "application/pdf".equals(l.getType()))
                            .findFirst()
                            .ifPresent(l -> articulo.setPdfUrl(l.getHref()));

                    articuloRepository.save(articulo);
                }

                return feed.getEntry().size();


            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }

        }

}
