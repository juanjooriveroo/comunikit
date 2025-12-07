package boardservice.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Encapsula la validación y compresión de imágenes.
 */
@Component
public class ImageProcessor {

    private static final int MAX_SIZE_PX = 512;
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5MB

    public ProcessedImage process(MultipartFile file) {
        validateFile(file);

        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) {
                throw new IllegalArgumentException("El fichero no es una imagen válida");
            }

            BufferedImage resized = resize(original);
            boolean hasAlpha = resized.getColorModel().hasAlpha();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            if (hasAlpha) {
                ImageIO.write(resized, "png", outputStream);
            } else {
                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.6f);

                try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
                    writer.setOutput(ios);
                    writer.write(null, new IIOImage(resized, null, null), param);
                } finally {
                    writer.dispose();
                }
            }

            byte[] bytes = outputStream.toByteArray();
            return new ProcessedImage(bytes, resolveMimeType(file, hasAlpha), bytes.length);

        } catch (IOException e) {
            throw new IllegalStateException("Error al procesar la imagen", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("La imagen es obligatoria");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("La imagen no puede pesar más de 5MB");
        }

        String contentType = file.getContentType();
        if (!Objects.equals(contentType, "image/jpeg") && !Objects.equals(contentType, "image/png")) {
            throw new IllegalArgumentException("Solo se permiten imágenes JPG o PNG");
        }
    }

    private BufferedImage resize(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width <= MAX_SIZE_PX && height <= MAX_SIZE_PX) {
            return image;
        }

        float ratio = Math.min((float) MAX_SIZE_PX / width, (float) MAX_SIZE_PX / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        int imageType = image.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage resized = new BufferedImage(newWidth, newHeight, imageType);

        Graphics2D graphics = resized.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(image, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        return resized;
    }

    private String resolveMimeType(MultipartFile file, boolean hasAlpha) {
        if (hasAlpha) {
            return "image/png";
        }

        String contentType = file.getContentType();
        return contentType != null ? contentType : "image/jpeg";
    }

    public record ProcessedImage(byte[] bytes, String mimeType, long sizeBytes) {}
}
