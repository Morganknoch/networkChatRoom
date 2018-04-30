import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Morgan on 4/29/2018.
 */
public class ClientThreadHandler implements Runnable {

    public DataInputStream ins;
    public DataOutputStream outs;
    public Socket socket;
    public String userID = null;
    public ServerSocket serverSocket;

    public ClientThreadHandler(DataInputStream ins, DataOutputStream outs, Socket socket) {
        this.ins = ins;
        this.outs = outs;
        this.socket = socket;
    }

    @Override
    public void run() {

        boolean connected = true;

        try {

            while (connected) {

                // used to get input from client

                String line = ins.readUTF();

                // parse string to get command and attributes
                if (line != null) {
                    String deliminators = "[ ]";
                    String[] tokens = line.split(deliminators);

                    if (tokens[0].equals("login")) {

                        String username = tokens[1];
                        String password = tokens[2];

                        login(username, password);

                    } else if (tokens[0].equals("newuser")) {

                        String username = tokens[1];
                        String password = tokens[2];

                        newUser(username, password);

                    } else if (tokens[0].equals("send") && tokens[1].equals("all")) {
                        // send all, only works if client is logged in

                        String message = tokens[2];

                        for( int i = 3; i < tokens.length; i++)
                        {
                            message += (" " + tokens[i]);
                        }

                        sendall(message);
                    } else if (tokens[0].equals("send")) {
                        // send only works if client is logged in
                        String userID = tokens[1];

                        //piece the original message back together if multiple words
                        String message = tokens[2];

                        for( int i = 3; i < tokens.length; i++)
                        {
                            message += (" " + tokens[i]);
                        }

                        send(message, userID);

                    } else if (tokens[0].equals("who")) {
                        // who, lists all clients

                        who();

                    } else if (tokens[0].equals("logout")) {
                        // logout only works if client is logged in
                        if (logout()) {
                            connected = false;

                            //remove from activeClients
                            EchoServer.activeClients.remove(this);
                            EchoServer.numClients--;
                            this.socket.close();

                            // I DONT KNOW IF THIS IS NEEDED OR NOT ////////////////////////////////////////////////////
                            Thread.currentThread().interrupt();
                        }
                    }
                } else {
                    // do nothing if empty string
                }
            }

            System.out.println("Client Closed.");
        } catch (IOException e) {
            //System.out.println(e);
        } catch (Exception e) {
            // do nothing
        }
    }

    public boolean login(String username, String password)
    {
        try {
            // handles the login functionality
            boolean loginSuccessful = true;
            User loggedInUser = null;
            // find username and compare password
            for (User u : EchoServer.Users) {
                if (u.username.equals(username)) {
                    //check if username and password match
                    if (u.password.equals(password)) {
                        loginSuccessful = true;
                        loggedInUser = u;
                    }
                }
            }


            boolean alreadyLoggedIn = false;
            // need to make sure a person is logged in more than once
            for(ClientThreadHandler client : EchoServer.activeClients)
            {
                if(client.userID != null && client.userID.equals(username)) {
                    alreadyLoggedIn = true;
                    break;
                }
            }

            // if person is already logged in, send error back to client that attempted login
            if(alreadyLoggedIn)
            {
                outs.writeBoolean(false);
                outs.writeUTF("That user is already logged in!");
                return false;
            }

            // if the login is successful then notify other clients and initialize userID to show that login successful
            if (loginSuccessful) {
                this.userID = username;

                // Write to other clients that new client has logged in
                for(ClientThreadHandler client : EchoServer.activeClients)
                {
                    if(client.userID != null) {

                        client.outs.writeBoolean(true);
                        client.outs.writeUTF(this.userID + " has logged in");
                    }
                }

                System.out.println(this.userID + ": logged in");

                return true;
            } else {
                outs.writeBoolean(false);
                outs.writeUTF("Incorrect username or password");
                return false;
            }
        }
        catch(Exception e)
        {

        }
        return false;
    }

    public boolean newUser(String username, String password)
    {
        try {
            //check if username and password are the correct format
            if (username.length() > 32) {
                //username too long, invalid
                outs.writeBoolean(false);
                outs.writeUTF("Username invalid length!");
                return false;
            } else if (password.length() < 4 || password.length() > 8) {
                //password too long, invalid
                outs.writeBoolean(false);
                outs.writeUTF("Password invalid length!");
                return false;
            }

            for (User u : EchoServer.Users) {
                if (u.username.equals(username)) {
                    // The username already exists
                    outs.writeBoolean(false);
                    outs.writeUTF("Username already exists!");
                    return false;
                }
            }

            // The user info is valid and does not already exist, so create new user
            String[] userInfo = {username, password};
            User newUser = new User(userInfo);

            // add new user to ArrayList
            EchoServer.Users.add(newUser);

            // write new user to inputfile
            synchronized (this) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(EchoServer.inputFile, true));
                    writer.append("\n" + username + " " + password);
                    writer.close();
                } catch (IOException e) {
                    System.out.println(e);
                    outs.writeBoolean(false);
                    outs.writeUTF("Error writing to file! User creation failed!");
                    EchoServer.Users.remove(newUser);
                    return false;
                } catch (Exception e) // in case of Null Pointer Exception
                {
                    outs.writeBoolean(false);
                    outs.writeUTF("Error in writing to file! User creation failed!");
                    EchoServer.Users.remove(newUser);
                    return false;
                }
            }

            // User creation successful, user logged in
            this.userID = username;

            // Write to other clients that new client has logged in
            for(ClientThreadHandler client : EchoServer.activeClients)
            {
                if(client.userID != null) {

                    client.outs.writeBoolean(true);
                    client.outs.writeUTF(this.userID + " has logged in");
                }
            }

            System.out.println(this.userID + ": logged in");
            return true;
        }
        catch(Exception e)
        {

        }
        return false;
    }

    public boolean send(String message, String userID)
    {
        // check to make sure user is logged in
        try {
            if (this.userID == null)
            {
                // if user is not logged in shoot back error message
                outs.writeBoolean(false);
                outs.writeUTF("You are currently not logged in!");
                return false;
            }
            else
            {
                // Write to other clients that new client has logged in
                for(ClientThreadHandler client : EchoServer.activeClients)
                {
                    if(client.userID != null && client.userID.equals(userID))
                    {
                        client.outs.writeBoolean(true);
                        client.outs.writeUTF(this.userID + ":  " + message);
                    }
                    else if(client.userID != null && !client.userID.equals(this.userID))
                    {
                        // the client requested does not exist
                        outs.writeBoolean(false);
                        outs.writeUTF("The client you requested does not exist!");
                        return false;
                    }
                }

                System.out.println(this.userID + " (to " + userID + "): " + message );

                return true;
            }
        }
        catch(Exception e)
        {

        }
        return false;
    }

    public boolean sendall(String message)
    {
        // check to make sure user is logged in
        try {
            if (this.userID == null) {
                // if user is not logged in shoot back error message
                outs.writeBoolean(false);
                outs.writeUTF("You are currently not logged in!");
                return false;
            } else {

                // Write to other clients that new client has logged in
                for(ClientThreadHandler client : EchoServer.activeClients)
                {
                    if( client.userID != null && !client.userID.equals(this.userID))
                    {
                        client.outs.writeBoolean(true);
                        client.outs.writeUTF(this.userID + ":  " + message);
                    }
                }

                System.out.println(this.userID + ": " + message);
                return true;
            }
        }
        catch(Exception e)
        {

        }
        return false;
    }

    public boolean who()
    {
        // check to make sure user is logged in
        try {
            if (this.userID == null) {
                // if user is not logged in shoot back error message
                outs.writeBoolean(false);
                outs.writeUTF("You are currently not logged in!");
                return false;
            } else {

                String userList = "";

                // Write to other clients that new client has logged in
                for(ClientThreadHandler client : EchoServer.activeClients)
                {
                    if( client.userID != null)
                    {
                        userList += (client.userID + ", ");
                    }
                }

                //write list to client
                this.outs.writeBoolean(true);
                this.outs.writeUTF(userList);

                return true;
            }
        }
        catch(Exception e)
        {

        }
        return false;
    }

    public boolean logout()
    {
        try {
            if (this.userID == null) {
                // if user is not logged in shoot back error message
                outs.writeBoolean(false);
                outs.writeUTF("You are currently not logged in!");
                return false;
            } else {

                for(ClientThreadHandler client : EchoServer.activeClients)
                {
                    if(client.userID != null)
                    {
                        client.outs.writeBoolean(true);
                        client.outs.writeUTF(this.userID + " left");
                    }
                }

                outs.writeBoolean(true);
                outs.writeUTF(this.userID + ": left");
                System.out.println(this.userID + ": left");
                return true;
            }
        }
        catch(Exception e)
        {

        }
        return false;
    }
}

