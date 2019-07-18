class DeleteRequest extends AbstractMessage {
    private String filename;

    String getFilename() {
        return filename;
    }

    DeleteRequest(String filename) {
        this.filename = filename;
    }
}
