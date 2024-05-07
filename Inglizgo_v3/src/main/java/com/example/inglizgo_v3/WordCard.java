package com.example.inglizgo_v3;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;


public class WordCard {
    private Map<String, Object> data;

    public WordCard(Map<String, Object> data) {
        this.data = data;
    }

    public String getEnglishWord() {
        return (String) data.get("EN_word");
    }

    public String getTranslation() {
        return (String) data.get("TR_translate");
    }

    public String getFirstExample() {
        return (String) data.get("FirstEx");
    }

    public String getSecondExample() {
        return (String) data.get("SecondEX");
    }

    public Image getwordImage() {
        // Assuming you have a method to load image from data
        return loadImageFromData((byte[]) data.get("Word_Image"));
    }

    // Method to load image from byte array data
    private Image loadImageFromData(byte[] imageData) {
        try {
            if (imageData != null && imageData.length > 0) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                if (bufferedImage != null) {
                    return SwingFXUtils.toFXImage(bufferedImage, null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

