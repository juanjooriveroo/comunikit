package boardservice.mapper;

import boardservice.dto.ImageDto;
import boardservice.dto.ImagePushRequestDto;
import boardservice.entity.Image;
import boardservice.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageMapper {

    private final LanguageRepository languageRepository;

    public ImageDto toDto(Image image) {
        return ImageDto.builder()
                .id(image.getId())
                .language(image.getLanguage())
                .image(image.getImage())
                .name(image.getName())
                .build();
    }

    public Image create(ImagePushRequestDto request, MultipartFile file) {
        return Image.builder()
                .name(request.name())
                .ownerId(request.ownerId())
                .isPublic(false)
                .language(languageRepository.findByCode(request.language())
                        .orElseThrow(() -> new RuntimeException("Lenguaje no encontrado")))
                .image(compressImage(file))
                .build();
    }

    private byte[] compressImage(MultipartFile file) {
        try {
            validateFile(file);

            BufferedImage original = ImageIO.read(file.getInputStream());
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

                writer.setOutput(ImageIO.createImageOutputStream(outputStream));
                writer.write(null, new IIOImage(resized, null, null), param);
                writer.dispose();
            }

            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar la imagen", e);
        }
    }

    private BufferedImage resize(BufferedImage image) {

        int maxSize = 512;

        int width = image.getWidth();
        int height = image.getHeight();

        if (width <= maxSize && height <= maxSize) {
            return image;
        }

        float ratio = Math.min(
                (float) maxSize / width,
                (float) maxSize / height
        );

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        BufferedImage resized = new BufferedImage(
                newWidth,
                newHeight,
                image.getColorModel().hasAlpha()
                        ? BufferedImage.TYPE_INT_ARGB
                        : BufferedImage.TYPE_INT_RGB
        );

        resized.getGraphics().drawImage(image, 0, 0, newWidth, newHeight, null);

        return resized;
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("La imagen es obligatoria");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen no puede pesar más de 5MB");
        }

        String contentType = file.getContentType();

        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new IllegalArgumentException("Solo se permiten imágenes JPG o PNG");
        }
    }
}
