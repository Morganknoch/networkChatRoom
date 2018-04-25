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
    public static User currentUser = null; //keeps track of the current user loggedin

    public static void main(String args[])
    {
        inputFile = "users.txt";

        Users = getUsersFromFile(inputFile);
        Socket s;
        ServerSocket echoServer;


        try{
            echoServer = new ServerSocket(1060);
        }catch(IOException ioe){
            System.out.println("System failed to create server socket.");
            return;
        }

        while(true) {

            boolean connected = true;

            try {

                System.out.println("Waiting for a client to connect...");

                s = echoServer.accept();
                System.out.println("Client Connected.");
                BufferedReader ins = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintStream outs = new PrintStream(s.getOutputStream());

                while (connected) {
                    // used to get input from client
                    String line = ins.readLine();

                    // parse string to get command and attributes
                    String deliminators = "[ ]";
                    String[] tokens = line.split(deliminators);

                    if (tokens[0].equals("login")) {

                        String username = tokens[1];
                        String password = tokens[2];

                        login(username, password, ins, outs);

                    } else if (tokens[0].equals("newuser")) {

                        String username = tokens[1];
                        String password = tokens[2];

                        newUser(username, password, ins, outs);

                    } else if (tokens[0].equals("send")) {
                        // send only works if client is logged in

                        String message = tokens[1];

                        send(message, ins, outs);

                    } else if (tokens[0].equals("logout")) {
                        // logout only works if client is logged in
                        if(logout(ins, outs))
                        {
                            connected = false;
                        }
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
        User loggedInUser = null;
        // find username and compare password
        for(User u: Users)
        {
            if(u.username.equals(username))
            {
                //check if username and password match
                if(u.password.equals(password))
                {
                    loginSuccessful = true;
                    loggedInUser = u;
                }
            }
        }

        if(loginSuccessful)
        {
            currentUser = loggedInUser;
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

    public static boolean newUser(String username, String password, BufferedReader ins, PrintStream outs)
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

        for(User u: Users)
        {
            if(u.username.equals(username))
            {
                // The username already exists
                outs.println(false);
                outs.println("Username already exists!");
                return false;
            }
        }

        // The user info is valid and does not already exist, so create new user
        String[] userInfo = {username, password};
        User newUser = new User(userInfo);

        // add new user to ArrayList
        Users.add(newUser);

        // write new user to inputfile
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile, true));
            writer.append("\n" + username + " " + password);
            writer.close();
        }catch(IOException e){
            System.out.println(e);
            outs.println(false);
            outs.println("Error writing to file! User creation failed!");
            Users.remove(newUser);
            return false;
        }catch(Exception e) // in case of Null Pointer Exception
        {
            outs.println(false);
            outs.println("Error in writing to file! User creation failed!");
            Users.remove(newUser);
            return false;
        }

        // User creation successful, user logged in
        currentUser = newUser;
        outs.println(true);
        outs.println("User creation successful! Welcome " + username + "!");

        return true;
    }

    public static boolean send(String message, BufferedReader ins, PrintStream outs)
    {
        // check to make sure user is logged in

        if(currentUser.equals(null))
        {
           // if user is not logged in shoot back error message
           outs.println(false);
           outs.println("You are currently not logged in!");
           return false;
        }
        else
        {
            outs.println(true);
            outs.println(currentUser.username + ": " + message);
            return true;
        }

    }

    public static boolean logout(BufferedReader ins, PrintStream outs)
    {

        if(currentUser.equals(null))
        {
            // if user is not logged in shoot back error message
            outs.println(false);
            outs.println("You are currently not logged in!");
            return false;
        }
        else
        {
            outs.println(true);
            outs.println(currentUser.username + ": left");
            return true;
        }
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
