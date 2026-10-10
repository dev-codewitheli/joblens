package com.codewitheli.joblens.adapter.in.web;

import com.codewitheli.joblens.adapter.in.web.dto.ImportedText;
import com.codewitheli.joblens.adapter.in.web.dto.UrlImportRequest;
import com.codewitheli.joblens.adapter.in.web.importer.DocumentImporter;
import com.codewitheli.joblens.application.BadInputException;
import com.codewitheli.joblens.application.port.out.ImageTextExtractorPort;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Import a job posting from different sources, returning plain text the UI drops
 * into the job-description field. Nothing is stored.
 */
@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final DocumentImporter documents;
    private final ImageTextExtractorPort images;

    public ImportController(DocumentImporter documents, ImageTextExtractorPort images) {
        this.documents = documents;
        this.images = images;
    }

    @PostMapping("/pdf")
    public ImportedText fromPdf(@RequestParam("file") MultipartFile file) {
        requireFile(file);
        String name = file.getOriginalFilename();
        String type = file.getContentType();
        boolean looksPdf = (type != null && type.contains("pdf"))
                || (name != null && name.toLowerCase().endsWith(".pdf"));
        if (!looksPdf) {
            throw new BadInputException("Please upload a PDF file.");
        }
        return new ImportedText(documents.fromPdf(readBytes(file)));
    }

    @PostMapping("/url")
    public ImportedText fromUrl(@Valid @RequestBody UrlImportRequest request) {
        return new ImportedText(documents.fromUrl(request.url()));
    }

    @PostMapping("/image")
    public ImportedText fromImage(@RequestParam("file") MultipartFile file) {
        requireFile(file);
        return new ImportedText(images.extract(readBytes(file), file.getContentType()));
    }

    private void requireFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadInputException("No file was uploaded.");
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BadInputException("Couldn't read the uploaded file.");
        }
    }
}
