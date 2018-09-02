package ngeneanalysys.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jang
 * @since 2018-06-18
 */
public class ImageService {

    private ImageService() { }

    public static void convertPDFtoImage(File file, String baseFileName, File imageFile) {
        String path = file.getParentFile().getAbsolutePath();
        try {
            PDDocument document = PDDocument.load(file);
            PDFRenderer d = new PDFRenderer(document);
            int index = document.getNumberOfPages();
            List<File> fileList = new ArrayList<>();
            int width = 0;
            int height = 0;
            for(int i = 0; i < index ;i++) {
                BufferedImage im  = d.renderImage(i, 2, ImageType.RGB);
                width = im.getWidth();
                height = im.getHeight();
                File image = new File(path + File.separator + "temp" + File.separator
                        + baseFileName + "_" + i+".jpg");
                ImageIO.write(im, "jpg",  image);
                fileList.add(file);
            }
            createOneImageFile(index, width, height, fileList, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createOneImageFile(final int index, final int width, final int height,
                                           final List<File> imageFiles, final File imageFile) {

        int totalHeight = height * index;
        BufferedImage mergedImage = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        int currentHeight = 0;
        Graphics2D graphics2D = (Graphics2D) mergedImage.getGraphics();

        try {
            for(int i = 0; i < index ; i++) {
                BufferedImage image = ImageIO.read(imageFiles.get(i));
                graphics2D.drawImage(image, 0, currentHeight, null);
                currentHeight += height;
            }

            ImageIO.write(mergedImage, "jpg", imageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
