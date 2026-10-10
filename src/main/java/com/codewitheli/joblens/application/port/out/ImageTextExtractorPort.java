package com.codewitheli.joblens.application.port.out;

/**
 * Outbound port: extract job-posting text from an image (e.g. a screenshot).
 *
 * <p>The AI adapter uses a vision model; the demo adapter explains that image
 * import needs AI mode. Nothing is stored.
 */
public interface ImageTextExtractorPort {

    String extract(byte[] imageBytes, String contentType);
}
