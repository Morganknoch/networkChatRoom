/**
 * Created by Morgan Knoch on 4/21/2018.
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
