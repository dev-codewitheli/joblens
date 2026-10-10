package com.codewitheli.joblens.adapter.in.web.importer;

import com.codewitheli.joblens.application.BadInputException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Extracts plain job-description text from a PDF upload or a public URL.
 *
 * <p>PDF uses Apache PDFBox. URL fetching is hardened against SSRF: http/https
 * only, DNS-resolved IPs must be public (no loopback/private/link-local), no
 * auto-redirects, with request timeouts and a response-size cap. Nothing is stored.
 */
@Component
public class DocumentImporter {

    private static final int MAX_BYTES = 2 * 1024 * 1024; // 2 MB response cap
    private static final int MIN_TEXT = 40;               // reject near-empty extractions

    private final HttpClient http = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(6))
            .build();

    /** Extract text from a PDF's bytes. */
    public String fromPdf(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new BadInputException("The uploaded file is empty.");
        }
        try (PDDocument doc = Loader.loadPDF(bytes)) {
            String text = new PDFTextStripper().getText(doc).strip();
            if (text.length() < MIN_TEXT) {
                throw new BadInputException("Couldn't read text from that PDF — it may be scanned/image-only. "
                        + "Try the image import, or paste the text.");
            }
            return text;
        } catch (IOException e) {
            throw new BadInputException("That doesn't look like a readable PDF.");
        }
    }

    /** Fetch a public URL and extract its main text. */
    public String fromUrl(String rawUrl) {
        URI uri = parseAndValidate(rawUrl);
        try {
            HttpRequest req = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(8))
                    .header("User-Agent", "JobLensBot/1.0 (+https://github.com/dev-codewitheli/joblens)")
                    .header("Accept", "text/html,application/xhtml+xml")
                    .GET()
                    .build();

            HttpResponse<InputStream> resp = http.send(req, HttpResponse.BodyHandlers.ofInputStream());
            int status = resp.statusCode();
            if (status >= 300 && status < 400) {
                throw new BadInputException("That link redirects — open it in your browser and paste the final URL.");
            }
            if (status != 200) {
                throw new BadInputException("Couldn't fetch that URL (HTTP " + status + "). Paste the text instead.");
            }
            String html = readCapped(resp.body());
            Document doc = Jsoup.parse(html, uri.toString());
            doc.select("script, style, noscript, nav, footer, header, svg").remove();
            String main = doc.selectFirst("main, article, [role=main]") != null
                    ? doc.selectFirst("main, article, [role=main]").text()
                    : doc.body() != null ? doc.body().text() : doc.text();
            String text = main == null ? "" : main.strip();
            if (text.length() < MIN_TEXT) {
                throw new BadInputException("Couldn't extract readable text from that page "
                        + "(it may require login or JavaScript). Paste the text instead.");
            }
            return text;
        } catch (BadInputException e) {
            throw e;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new BadInputException("Couldn't reach that URL. Paste the text instead.");
        }
    }

    private URI parseAndValidate(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new BadInputException("Enter a URL.");
        }
        URI uri;
        try {
            uri = new URI(rawUrl.trim());
        } catch (URISyntaxException e) {
            throw new BadInputException("That isn't a valid URL.");
        }
        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new BadInputException("Only http and https URLs are supported.");
        }
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new BadInputException("That URL has no host.");
        }
        try {
            for (InetAddress addr : InetAddress.getAllByName(host)) {
                if (addr.isLoopbackAddress() || addr.isAnyLocalAddress()
                        || addr.isLinkLocalAddress() || addr.isSiteLocalAddress()
                        || addr.isMulticastAddress()) {
                    throw new BadInputException("That URL resolves to a private or local address and can't be fetched.");
                }
            }
        } catch (UnknownHostException e) {
            throw new BadInputException("Couldn't resolve that host.");
        }
        return uri;
    }

    private String readCapped(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int total = 0, n;
        try (in) {
            while ((n = in.read(buf)) != -1) {
                total += n;
                if (total > MAX_BYTES) {
                    break; // cap — use what we have
                }
                out.write(buf, 0, n);
            }
        }
        return out.toString(java.nio.charset.StandardCharsets.UTF_8);
    }
}
