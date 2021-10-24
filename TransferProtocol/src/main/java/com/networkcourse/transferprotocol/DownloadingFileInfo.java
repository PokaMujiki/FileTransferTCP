package com.networkcourse.transferprotocol;

public class DownloadingFileInfo {
    private final String fileName;
    private final long fileSize;

    public DownloadingFileInfo(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return fileName;
    }
}
    