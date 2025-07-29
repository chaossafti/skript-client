package de.safti.skriptclient.utils;

import java.nio.file.Path;

public class PathUtils {

    public static String getExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("File does not have an extension!");
        }
        return fileName.substring(dotIndex + 1);
    }

}
