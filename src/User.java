/**
 * Created by Morgan Knoch on 4/21/2018.
 * Written for CS4850 Graduate Network Chatroom Project
 * Helper class to be used with EchoServer, which
 * holds userID and password for each individual user
 */
public class User {

    public String username = null;
    public String password = null;

    public User(String[] tokens)
    {
        this.username = tokens[0];
        this.password = tokens[1];
    }
}
