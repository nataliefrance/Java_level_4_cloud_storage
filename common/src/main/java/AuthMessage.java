public class AuthMessage extends AbstractMessage{

    String login;
    String password;
    String message;

    public AuthMessage() {
    }

    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public AuthMessage(String message) {
        this.message = message;
    }
}
