package com.aej.cecarticulo.services;

import com.aej.cecarticulo.dao.ArticuloRepository;
import com.aej.cecarticulo.dto.SearchArticlesDTO;
import com.aej.cecarticulo.model.ArticuloModel;
import com.aej.cecarticulo.model.ArxivEntry;
import com.aej.cecarticulo.model.ArxivFeed;
import com.aej.cecarticulo.model.ProgressStatus;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.genai.types.GenerateContentResponse;
import org.apache.coyote.Response;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.net.URI;



import java.nio.file.Files;
import java.nio.file.Path;

import java.util.*;

import com.google.genai.Client;



@Service
public class ArticuloServiceImpl implements IArticuloService {

    Client gemini = new Client(

    );

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private RestTemplate client;

    private final String BASE_URL = "https://export.arxiv.org/api/query?search_query=all:%s&start=0&max_results=%d";


    @Override
    public void SaveEntriesToMongo(ArxivEntry entry, String text, List<String> imgs, List<String> keywords) {

        ArticuloModel articulo = new ArticuloModel();
        articulo.setTitle(entry.getTitle());
        articulo.setSummary(entry.getSummary());
        articulo.setPublishedDate(entry.getPublished());
        articulo.setAuthors(entry.getAuthor().stream().map(ArxivEntry.Author::getName).toList());
        articulo.setCategories(entry.getCategory().stream().map(ArxivEntry.Category::getTerm).toList());
        articulo.setImages(imgs);
        articulo.setKeywords(keywords);
        articulo.setText(text);
        entry.getLink().stream()
                .filter(l -> "application/pdf".equals(l.getType()))
                .findFirst()
                .ifPresent(l -> articulo.setPdfUrl(l.getHref()));

        articuloRepository.save(articulo);

    }

    @Override
    public List<String> extractImagesFromPdf(byte[] pdfBytes) {
        List<String> images = new ArrayList<>();
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            for (PDPage page : doc.getPages()) {
                for (COSName name : page.getResources().getXObjectNames()) {
                    PDXObject xObject = page.getResources().getXObject(name);
                    if (xObject instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) xObject;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image.getImage(), "png", baos);
                        images.add(Base64.getEncoder().encodeToString(baos.toByteArray()));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error extrayendo imágenes del PDF", e);
        }
        return images;
    }

    @Override
    public List<String> generateKeywordsLLM(String title, String resumen, String tex) {
        GenerateContentResponse response = gemini.models.generateContent(

                "gemini-2.5-flash",
                "This is the resume title and text of a article, give me 5 keywords, nothing else, the keywords in plain text separated by commas" + title + resumen + tex,
                null


                );


        List<String> keywords = Arrays.asList(response.text().split(",\\s*"));
        keywords.replaceAll(String::trim);
        if (keywords.size() > 10) {
            return keywords.subList(0, 10);
        }

        return keywords;
    }




    @Override
    public byte[] DowloadPdf(String Url, String filename) {
        try {
            ResponseEntity<byte[]> res = client.exchange(Url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), byte[].class);

            if (res.getStatusCode().is2xxSuccessful()) {
                Path path = Path.of("pdfs", filename);
                Files.createDirectories(path.getParent());
                Files.write(path, res.getBody());
                System.out.println("PDF guardado en: " + path.toAbsolutePath());
                return res.getBody();
            }
            throw new RuntimeException("Error descargando PDF: " + res.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Error descargando PDF desde " + Url, e);
        }

    }

    @Override
    public String extractTextFromPdf(byte[] pdfBytes) {
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        } catch (Exception e) {
            throw new RuntimeException("Error extrayendo texto del PDF", e);
        }
    }

    public String getArxivXml(String url) {
        return client.getForObject(url, String.class);
    }




    @Override
    public Page<ArticuloModel> getArticulos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articuloRepository.findAll(pageable);
    }

    @Override
    public SearchArticlesDTO SearchArticles(String query, int maxResults) {
        try {


            String url = String.format(BASE_URL, query.replace(" ", "+"), maxResults);

            URI uri = URI.create(url);


            String xmlContent = getArxivXml(url);


            Path filePath = Path.of("downloads", "arxiv_" + query + ".xml");
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, xmlContent);

            XmlMapper xmlMapper = new XmlMapper();
            ArxivFeed feed = xmlMapper.readValue(xmlContent, ArxivFeed.class);
            SearchArticlesDTO searchArticlesDTO = new SearchArticlesDTO();
//            Pageable pageable = PageRequest.of(page, size);
            List<ArxivEntry> entries = feed.getEntry();
//            searchArticlesDTO.setFeedEntries(entries);
//            int start = (int) pageable.getOffset();
//            int end = Math.min((start + pageable.getPageSize()), entries.size());
//            List<ArxivEntry> sublist = entries.subList(start, end);
//            Page<ArxivEntry> pageResult = new PageImpl<>(sublist, pageable, entries.size());


            searchArticlesDTO.setArticles(entries);
            searchArticlesDTO.setCount(feed.getEntry().size());
            return searchArticlesDTO;


        } catch (Exception e) {
            e.printStackTrace();
            return new SearchArticlesDTO();
        }

    }

    @Override
    public void ProcessAndSave(ArxivEntry entry) {
        try {
            String pdfUrl = entry.getLink().stream().filter(l -> "application/pdf".equals(l.getType())).findFirst().map(ArxivEntry.Link::getHref).orElse(null);
            if (pdfUrl == null) {
                return;
            }
            String filaname = sanitizeFilename(entry.getTitle());
            byte[] pdfBytes = DowloadPdf(pdfUrl.replace("http://", "https://"), filaname);
            String text = extractTextFromPdf(pdfBytes);
            List<String> images = extractImagesFromPdf(pdfBytes);
            List<String> keywords = generateKeywordsLLM(entry.getTitle(), entry.getSummary(), text);

            SaveEntriesToMongo(entry, text, images, keywords);

        }catch (Exception e) {
            System.err.println("❌ Error procesando artículo: " + entry.getTitle() + " -> " + e.getMessage());
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", "_")
                .trim();
    }

}
