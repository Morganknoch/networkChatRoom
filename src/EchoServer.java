/**
 * Created by Morgan Knoch on 4/20/2018.
 */
import java.io.*;
import java.net.*;
//import java.nio.file.Files;
import java.util.*;

public class EchoServer
{
    public static ArrayList<User> Users;

    public static void main(String args[])
    {
        String inputFile = "users.txt";

        Users = getUsersFromFile(inputFile);
        Socket s;

        while(true) {

            boolean connected = true;

            try {
                ServerSocket echoServer = new ServerSocket(1060);

                System.out.println("Waiting for a client to connect...");

                s = echoServer.accept();
                System.out.println("Client Connected.");
                BufferedReader ins = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintStream outs = new PrintStream(s.getOutputStream());

                while (connected) {
                    //used to get input from client
                    String line = ins.readLine();

                    //parse string to get command and attributes
                    String deliminators = "[ ]";
                    String[] tokens = line.split(deliminators);

                    if (tokens[0].equals("login")) {
                        //send username and password to server for authentication
                        String username = tokens[1];
                        String password = tokens[2];

                        login(username, password, ins, outs);

                    } else if (tokens[0].equals("newuser")) {

                    } else if (tokens[0].equals("send")) {

                    } else if (tokens[0].equals("logout")) {
                        connected = false;
                    }

                }
                s.close();
                System.out.println("Client Closed.");
            } catch (IOException e) {
                //System.out.println(e);
            } catch (Exception e)
            {
                // do nothing
            }
        }
    }

    public static boolean login(String username, String password, BufferedReader ins, PrintStream outs)
    {
        // handles the login functionality
        boolean loginSuccessful = false;

        // find username and compare password
        for(User u: Users)
        {
            if(u.username.equals(username))
            {
                //check if username and password match
                if(u.password.equals(password))
                {
                    loginSuccessful = true;
                }
            }
        }

        if(loginSuccessful)
        {
            outs.println(true);
            outs.println(username + " is now logged in.");
            return true;
        }
        else
        {
            outs.println(false);
            outs.println("Incorrect username or password");
            return false;
        }
    }

    public static boolean newUser(String username, String password)
    {

        //check if username and password are the correct format
        if( username.length() > 32)
        {
            //username too long, invalid
            outs.println(false);
            outs.println("Username invalid length!");
            return false;
        }
        else if(password.length() < 4 || password.length() > 8)
        {
            //password too long, invalid
            outs.println(false);
            outs.println("Password invalid length!");
            return false;
        }

        return true;
    }

    public static boolean send(String message)
    {
        return true;
    }

    public static boolean logout()
    {
        return true;
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

    public static boolean writeUserToFile()
    {
        return true;
    }
}
