package com.judtih.judith_management_system.global.download;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Server-side download proxy: fetches the file bytes from its URL (S3 in prod,
 * local path URL in dev) and streams them back with Content-Disposition: attachment.
 *
 * WHY a proxy instead of linking S3 directly:
 * iOS Safari ignores the `download` attribute on cross-origin links and breaks
 * async blob downloads (user-gesture context is lost). Proxying through our own
 * origin with an attachment header forces a real download on every platform.
 *
 * The filename*=UTF-8'' form is RFC 5987 — it keeps Korean filenames intact.
 * Used by ReservationController (pamphlet) and BoardController (attachments).
 */
@Slf4j
@Service
public class FileDownloadService {

    public ResponseEntity<byte[]> buildDownloadResponse(String fileUrl, String filename) throws IOException {
        return buildDownloadResponse(fileUrl, filename, MediaType.APPLICATION_OCTET_STREAM);
    }

    public ResponseEntity<byte[]> buildDownloadResponse(String fileUrl, String filename, MediaType contentType) throws IOException {
        log.debug("buildDownloadResponse: filename={}", filename);
        byte[] bytes = URI.create(fileUrl).toURL().openStream().readAllBytes();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentType(contentType)
                .body(bytes);
    }
}
