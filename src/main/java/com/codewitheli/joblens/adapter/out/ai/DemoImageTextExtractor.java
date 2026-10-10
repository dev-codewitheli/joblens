package com.codewitheli.joblens.adapter.out.ai;

import com.codewitheli.joblens.application.BadInputException;
import com.codewitheli.joblens.application.port.out.ImageTextExtractorPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Demo-mode image import: there's no offline OCR, so this explains how to proceed.
 * The AI profile provides real vision-based extraction.
 */
@Component
@Profile("demo")
public class DemoImageTextExtractor implements ImageTextExtractorPort {

    @Override
    public String extract(byte[] imageBytes, String contentType) {
        throw new BadInputException("Image import needs AI mode. Upload a PDF or paste the text instead.");
    }
}
