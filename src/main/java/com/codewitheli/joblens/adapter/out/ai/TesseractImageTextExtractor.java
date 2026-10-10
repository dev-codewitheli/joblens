package com.codewitheli.joblens.adapter.out.ai;

import com.codewitheli.joblens.application.BadInputException;
import com.codewitheli.joblens.application.port.out.ImageTextExtractorPort;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Offline OCR image import (default/demo mode) using native Tesseract via Tess4J.
 *
 * <p>Runs with no API key. The Docker image installs {@code tesseract-ocr} and the
 * English data; locally it needs tesseract on the host. If tessdata can't be found
 * or OCR yields too little text, it falls back to a clear message. The {@code ai}
 * profile instead uses a vision model, which handles messy screenshots better.
 */
@Component
@Profile("demo")
public class TesseractImageTextExtractor implements ImageTextExtractorPort {

    private static final Logger log = LoggerFactory.getLogger(TesseractImageTextExtractor.class);

    /** Common locations for the Tesseract language data across base images. */
    private static final List<String> DATAPATH_CANDIDATES = List.of(
            "/usr/share/tesseract-ocr/5/tessdata",
            "/usr/share/tesseract-ocr/4.00/tessdata",
            "/usr/share/tessdata",
            "/usr/share/tesseract-ocr/tessdata",
            "/opt/homebrew/share/tessdata",
            "/usr/local/share/tessdata"
    );

    private final String configuredDataPath;
    private final String language;

    public TesseractImageTextExtractor(
            @Value("${joblens.ocr.datapath:}") String configuredDataPath,
            @Value("${joblens.ocr.language:eng}") String language) {
        this.configuredDataPath = configuredDataPath;
        this.language = language;
    }

    @Override
    public String extract(byte[] imageBytes, String contentType) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new BadInputException("The uploaded image is empty.");
        }
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            image = null;
        }
        if (image == null) {
            throw new BadInputException("Couldn't read that image. Use a PNG or JPG screenshot.");
        }

        Tesseract tesseract = new Tesseract();
        String dataPath = resolveDataPath();
        if (dataPath != null) {
            tesseract.setDatapath(dataPath);
        }
        tesseract.setLanguage(language);

        try {
            String text = tesseract.doOCR(image).strip();
            if (text.length() < 20) {
                throw new BadInputException("Couldn't read enough text from that image. "
                        + "Try a clearer/larger screenshot, or paste the text.");
            }
            return text;
        } catch (TesseractException | UnsatisfiedLinkError | NoClassDefFoundError e) {
            log.warn("OCR failed: {}", e.toString());
            throw new BadInputException("Image OCR isn't available right now. Upload a PDF or paste the text instead.");
        }
    }

    private String resolveDataPath() {
        if (configuredDataPath != null && !configuredDataPath.isBlank()) {
            return configuredDataPath;
        }
        for (String candidate : DATAPATH_CANDIDATES) {
            if (Files.isReadable(Path.of(candidate, language + ".traineddata"))) {
                return candidate;
            }
        }
        return null; // let Tesseract fall back to TESSDATA_PREFIX if set
    }
}
