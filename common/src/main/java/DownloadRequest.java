class DownloadRequest extends AbstractMessage {
    private String filename;

    String getFilename() {
        return filename;
    }

    DownloadRequest(String filename) {
        this.filename = filename;
    }
}
