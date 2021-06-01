package com.zoli.survivor.internal;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class ImageCache {

    private static final Logger logger = LogManager.getLogger(ImageCache.class);

    private static Map<String, Image> images = new HashMap<>();

    public static Image get(String filename) {
        filename = filename.toLowerCase(Locale.ROOT);
        if (images.containsKey(filename)) {
            return images.get(filename);
        } else {
            logger.debug("Loading: " + filename);
            Image image = null;
            InputStream stream = ImageCache.class.getResourceAsStream(filename);
            if (null == stream) {
                logger.error("File is not found: " + filename);
            } else {
                image = new Image(stream);
                images.put(filename, image);
            }
            return image;
        }
    }

}
