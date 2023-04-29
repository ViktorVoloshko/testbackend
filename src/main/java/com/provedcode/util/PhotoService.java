package com.provedcode.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.List;
import javax.imageio.ImageIO;

import static org.apache.http.entity.ContentType.*;


@Service
@AllArgsConstructor
public class PhotoService {

    public File degradePhoto(File photoFile) throws IOException {
        // загружаем изображение из файла
        BufferedImage originalImage = ImageIO.read(photoFile);

        // устанавливаем новый размер для изображения
        int newWidth = 300;
        int newHeight = 300;

        // создаем новое изображение с новым размером
        Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        // создаем новый файл для уменьшенного изображения
        String fileName = photoFile.getName();
        String fileType = fileName.substring(fileName.lastIndexOf('.') + 1);
        File newFile = new File(photoFile.getParent(), fileName);

        // записываем уменьшенное изображение в новый файл
        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.createGraphics().drawImage(resizedImage, 0, 0, null);
        ImageIO.write(outputImage, fileType, newFile);

        // возвращаем новый файл
        return newFile;
    }

    public boolean isFileImage(MultipartFile file) {
        return !List.of(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType()).contains(file.getContentType());
    }

}
