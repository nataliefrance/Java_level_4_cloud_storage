import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    Button deleteFromClient, deleteFromServer;

    @FXML
    ListView<String> clientFileList;

    @FXML
    ListView<String> serverFileList;

    @FXML
    HBox cloudPanel;

    @FXML
    VBox authPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    Label authLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
        Network.start();

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage abstractMessage = Network.readObject();
                    if (abstractMessage instanceof AuthMessage) {
                        AuthMessage authMessage = (AuthMessage) abstractMessage;
                        if ("/authOk".equals(authMessage.message)) {
                            setAuthorized(true);
                            break;
                        }
                        if ("/null_userId".equals(authMessage.message)) {
                            Platform.runLater(() -> authLabel.setText("Неверный логин или пароль"));
                        }
                    }
                }

                Network.sendMsg(new RefreshServerMessage());

                while (true) {
                    AbstractMessage abstractMessage = Network.readObject();
                    if (abstractMessage instanceof FileMessage) {
                        FileMessage fileMessage = (FileMessage) abstractMessage;
                        Files.write(Paths.get("client_storage/" + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE); //StandardOpenOption.CREATE всегда оздаёт/перезаписывает новые объекты
                        refreshLocalFilesList();
                    }
                    if (abstractMessage instanceof RefreshServerMessage) {
                        RefreshServerMessage refreshServerMsg = (RefreshServerMessage) abstractMessage;
                        refreshServerFilesList(refreshServerMsg.getServerFileList());
                    }
                }
            } catch (ClassNotFoundException | IOException ex) {
                ex.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        thread.setDaemon(true);
        thread.start();
        refreshLocalFilesList();
    }

    private void setAuthorized(boolean isAuthorized) {
        if (!isAuthorized) {
            authPanel.setVisible(true);
            authPanel.setManaged(true);
            cloudPanel.setVisible(false);
            cloudPanel.setManaged(false);
        } else {
            authPanel.setVisible(false);
            authPanel.setManaged(false);
            cloudPanel.setVisible(true);
            cloudPanel.setManaged(true);
        }
    }

    public void tryToAuth() {
        Network.sendMsg(new AuthMessage(loginField.getText(), passwordField.getText()));
        loginField.clear();
        passwordField.clear();
    }

    public void pressOnDownloadButton(ActionEvent actionEvent) {
        Network.sendMsg(new DownloadRequest(serverFileList.getSelectionModel().getSelectedItem()));
    }

    public void pressOnSendToCloudButton(ActionEvent actionEvent) {
        try {
            Network.sendMsg(new FileMessage(Paths.get("client_storage/" + clientFileList.getSelectionModel().getSelectedItem())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pressOnDeleteButton(ActionEvent actionEvent) {
        Button sourceButton = (Button) actionEvent.getSource();

        if (deleteFromClient.equals(sourceButton)) {
            try {
                Files.delete(Paths.get("client_storage/" + clientFileList.getSelectionModel().getSelectedItem()));
                refreshLocalFilesList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (deleteFromServer.equals(sourceButton)) {
            Network.sendMsg(new DeleteRequest(serverFileList.getSelectionModel().getSelectedItem()));
        }
    }

    private void refreshLocalFilesList() {
        updateUI(() -> {
            try {
                clientFileList.getItems().clear();
                Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> clientFileList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void refreshServerFilesList(ArrayList<String> filesList) {
        updateUI(() -> {
            serverFileList.getItems().clear();
            serverFileList.getItems().addAll(filesList);
        });
    }

    private static void updateUI(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
