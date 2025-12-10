package boardservice.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
            byte[] fileBytes = file.getBytes();
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(fileBytes));
            if (original == null) {
                throw new IllegalArgumentException("El fichero no es una imagen válida");
            }

            // Aplicar orientación EXIF si existe
            BufferedImage oriented = applyExifOrientation(original, fileBytes);
            BufferedImage resized = resize(oriented);
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

    /**
     * Lee y aplica la orientación EXIF de la imagen.
     * Las cámaras de móviles guardan las imágenes en orientación horizontal
     * y usan metadatos EXIF para indicar cómo rotarlas al mostrarlas.
     */
    private BufferedImage applyExifOrientation(BufferedImage image, byte[] imageBytes) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(imageBytes));
            ExifIFD0Directory exifDir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            
            if (exifDir == null || !exifDir.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                return image; // Sin metadatos EXIF, devolver original
            }

            int orientation = exifDir.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            
            return switch (orientation) {
                case 1 -> image; // Normal, sin rotación
                case 2 -> flipHorizontal(image);
                case 3 -> rotate(image, 180);
                case 4 -> flipVertical(image);
                case 5 -> flipHorizontal(rotate(image, 90));
                case 6 -> rotate(image, 90);  // Rotación 90° derecha (foto vertical)
                case 7 -> flipHorizontal(rotate(image, 270));
                case 8 -> rotate(image, 270); // Rotación 90° izquierda
                default -> image;
            };
            
        } catch (Exception e) {
            return image;
        }
    }

    private BufferedImage rotate(BufferedImage image, int degrees) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Para rotaciones de 90/270, intercambiar dimensiones
        int newWidth = (degrees % 180 != 0) ? height : width;
        int newHeight = (degrees % 180 != 0) ? width : height;
        
        int imageType = image.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, imageType);
        
        Graphics2D g = rotated.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        AffineTransform transform = new AffineTransform();
        transform.translate(newWidth / 2.0, newHeight / 2.0);
        transform.rotate(Math.toRadians(degrees));
        transform.translate(-width / 2.0, -height / 2.0);
        
        g.setTransform(transform);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        
        return rotated;
    }

    private BufferedImage flipHorizontal(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int imageType = image.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage flipped = new BufferedImage(width, height, imageType);
        
        Graphics2D g = flipped.createGraphics();
        g.drawImage(image, width, 0, -width, height, null);
        g.dispose();
        
        return flipped;
    }

    private BufferedImage flipVertical(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int imageType = image.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage flipped = new BufferedImage(width, height, imageType);
        
        Graphics2D g = flipped.createGraphics();
        g.drawImage(image, 0, height, width, -height, null);
        g.dispose();
        
        return flipped;
    }

    public record ProcessedImage(byte[] bytes, String mimeType, long sizeBytes) {}
}
