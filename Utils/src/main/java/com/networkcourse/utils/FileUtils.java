package com.networkcourse.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtils {
    public static File createFile(String fileName) throws IOException {
        File file = new File(fileName);
        String filePath = file.getPath();
        if (filePath.charAt(0) == File.separatorChar || filePath.contains("..")) {
            throw new IOException("Forbidden file name");
        }

        if (filePath.contains(File.separator) && !file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("Unable to create folders for downloading file");
            }
        }

        String parentName = file.getParent() + File.separator;
        if (file.getParent() == null) {
            parentName = "";
        }

        File result = new File(parentName + file.getName());
        boolean created = result.createNewFile();
        while (!created) {
            result = new File(parentName + UUID.randomUUID());
            created = result.createNewFile();
        }
        return result;
    }
}
    