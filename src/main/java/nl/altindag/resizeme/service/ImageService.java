package nl.altindag.resizeme.service;

import com.mortennobel.imagescaling.ResampleOp;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageService {

    @PostConstruct
    private void init() {
    }

    public InputStream compress(Image image, double width) throws IOException {
        return compress(SwingFXUtils.fromFXImage(image, null), width);
    }

    public InputStream compress(final File file, double width) throws IOException {
        return compress(ImageIO.read(file), width);
    }

    public InputStream compress(BufferedImage bufferedImage, Double width) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage originalImage = bufferedImage;
        double originalWidth = originalImage.getWidth();
        double originalHeight = originalImage.getHeight();

        width = width == 0 ? originalWidth : width;
        Double height = originalHeight * width / originalWidth;

        ResampleOp resizeOp = new ResampleOp(width.intValue(), height.intValue());

        BufferedImage resizedImage = resizeOp.filter(originalImage, null);
        ImageIO.write(resizedImage, "jpg", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public double calculateHeight(Image image, double width) {
        return image.getHeight() * width / image.getWidth();
    }

    public double calculateWidth(Image image, double height) {
        return image.getWidth() * height / image.getHeight();
    }

}
