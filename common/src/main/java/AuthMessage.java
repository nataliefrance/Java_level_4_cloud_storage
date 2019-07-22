public class AuthMessage extends AbstractMessage{

    String login;
    String password;

    public AuthMessage() {
    }

    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
