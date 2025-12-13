package boardservice.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageProcessorTest {

    private final ImageProcessor imageProcessor = new ImageProcessor();

    @Test
    void process_ShouldCompressValidPngImage() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                baos.toByteArray()
        );

        ImageProcessor.ProcessedImage result = imageProcessor.process(file);

        assertNotNull(result);
        assertEquals("image/png", result.mimeType());
        assertTrue(result.sizeBytes() > 0);
        assertNotNull(result.bytes());
    }

    @Test
    void process_ShouldCompressValidJpegImage() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                baos.toByteArray()
        );

        ImageProcessor.ProcessedImage result = imageProcessor.process(file);

        assertNotNull(result);
        assertEquals("image/jpeg", result.mimeType());
        assertTrue(result.sizeBytes() > 0);
    }

    @Test
    void process_ShouldResizeLargeImage() throws IOException {
        BufferedImage largeImage = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(largeImage, "jpg", baos);
        
        MultipartFile file = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                baos.toByteArray()
        );

        ImageProcessor.ProcessedImage result = imageProcessor.process(file);

        BufferedImage resultImage = ImageIO.read(new ByteArrayInputStream(result.bytes()));
        assertTrue(resultImage.getWidth() <= 512);
        assertTrue(resultImage.getHeight() <= 512);
    }

    @Test
    void process_ShouldThrowException_WhenFileIsNull() {
        assertThrows(IllegalArgumentException.class, () -> imageProcessor.process(null));
    }

    @Test
    void process_ShouldThrowException_WhenFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> imageProcessor.process(emptyFile));
    }

    @Test
    void process_ShouldThrowException_WhenFileTooLarge() {
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        MultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.png",
                "image/png",
                largeContent
        );

        assertThrows(IllegalArgumentException.class, () -> imageProcessor.process(largeFile));
    }

    @Test
    void process_ShouldThrowException_WhenInvalidMimeType() {
        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                new byte[100]
        );

        assertThrows(IllegalArgumentException.class, () -> imageProcessor.process(invalidFile));
    }
}
