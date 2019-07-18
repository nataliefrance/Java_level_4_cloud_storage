import java.util.ArrayList;

class RefreshServerMessage extends AbstractMessage {
    private ArrayList<String> serverFileList;

    public RefreshServerMessage() {
    }

    public RefreshServerMessage(ArrayList<String> serverFileList) {
        this.serverFileList = serverFileList;
    }

    public ArrayList<String> getServerFileList() {
        return serverFileList;
    }
}
