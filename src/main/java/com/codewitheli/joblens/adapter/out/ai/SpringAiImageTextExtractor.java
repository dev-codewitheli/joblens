package com.codewitheli.joblens.adapter.out.ai;

import com.codewitheli.joblens.application.BadInputException;
import com.codewitheli.joblens.application.port.out.ImageTextExtractorPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * AI-mode image import (active under the {@code ai} profile): uses a vision model
 * to read a job posting out of a screenshot and return plain text.
 */
@Component
@Profile("ai")
public class SpringAiImageTextExtractor implements ImageTextExtractorPort {

    private static final String PROMPT = """
            This image is a screenshot of a job posting. Transcribe the job
            description as plain text — title, responsibilities, and requirements.
            Return only the transcribed text, no commentary.""";

    private final ChatClient chat;

    public SpringAiImageTextExtractor(ChatClient.Builder builder) {
        this.chat = builder.build();
    }

    @Override
    public String extract(byte[] imageBytes, String contentType) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new BadInputException("The uploaded image is empty.");
        }
        MimeType mimeType = contentType != null && !contentType.isBlank()
                ? MimeTypeUtils.parseMimeType(contentType)
                : MimeTypeUtils.IMAGE_PNG;
        String text = chat.prompt()
                .user(u -> u.text(PROMPT).media(mimeType, new ByteArrayResource(imageBytes)))
                .call()
                .content();
        if (text == null || text.isBlank()) {
            throw new BadInputException("Couldn't read text from that image. Try a clearer screenshot or paste the text.");
        }
        return text.strip();
    }
}
