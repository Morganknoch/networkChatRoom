/**
 * Created by Morgan Knoch on 4/20/2018.
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class EchoServer
{
    public static ArrayList<User> Users;
    public static String inputFile;
    public static final int MAXCLIENTS = 3;

    public static void main(String args[]) {
        inputFile = "users.txt";

        Users = getUsersFromFile(inputFile);
        Socket s;
        ServerSocket echoServer;

    }


    public static ArrayList<User> getUsersFromFile(String inputFile)
    {
        // READ FILE AND PARSE OUT USERS AND PASSWORD AND PUT IN ARRAYLIST

        ArrayList Users = new ArrayList<User>();

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(inputFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                //PARSE OUT NAMES AND PASSWORDS
                String deliminators = "[ ]";
                String[] tokens = line.split(deliminators);

                if(tokens.length == 2) //Includes name and password
                {
                    User newUser = new User(tokens);
                    Users.add(newUser);
                }
                else
                {
                    System.out.println("Unable to obtain user information");
                }

            }

            // Always close files.
            bufferedReader.close();
            fileReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            inputFile + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + inputFile + "'");
        }

        return Users;
    }
}
