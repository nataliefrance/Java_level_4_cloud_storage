import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileMessage extends AbstractMessage {
    private String filename;
    private byte[] data;

    String getFilename() {
        return filename;
    }

    byte[] getData() {
        return data;
    }

    FileMessage(Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }
}
