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

    public InputStream compress(BufferedImage bufferedImage, Double width) throws IOException {
        var outputStream = new ByteArrayOutputStream();

        double originalWidth = bufferedImage.getWidth();
        double originalHeight = bufferedImage.getHeight();

        width = width == 0 ? originalWidth : width;
        Double height = originalHeight * width / originalWidth;

        ResampleOp resizeOp = new ResampleOp(width.intValue(), height.intValue());

        BufferedImage resizedImage = resizeOp.filter(bufferedImage, null);
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
