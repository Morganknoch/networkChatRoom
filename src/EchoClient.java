/**
 * Created by Morgan Knoch on 4/20/2018.
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EchoClient extends Thread
{
    //public static InputStreamReader convert;
    //public static BufferedReader stdin;
    public static DataInputStream ins;
    public static DataOutputStream outs;
    public static boolean loggedin = true;
    public static Scanner stdin;

    public static void main(String args[])
    {
        if(args.length != 1)
        {
            System.out.println("No IP address was given!");
            return;
        }

//        InputStreamReader convert = new InputStreamReader(System.in);
//        BufferedReader stdin = new BufferedReader(convert);
        stdin = new Scanner(System.in);

        try
        {
            Socket echoClient = new Socket(args[0], 11060);
            outs = new DataOutputStream(echoClient.getOutputStream());
            ins = new DataInputStream((echoClient.getInputStream()));

            //outs.writeUTF("login Tom Tom11");

            System.out.println("My chat room client.");

            Thread sendMessageToServer = new Thread(new Runnable() {

                /* This thread is used to send messages to the server
                * */

                public void run() {

                    while (loggedin) {

                        try {
                            System.out.print(": ");
                            String line = stdin.nextLine();

                            //CHECK IF INPUTTED COMMANDS AND ARGS ARE VALID
                            if (commandsAccepted(line)) {
                                //commands are valid
                                String deliminators = "[ ]";
                                String[] tokens = line.split(deliminators);

                                if (tokens[0].equals("login")) {
                                    //send username and password to server for authentication
                                    String username = tokens[1];
                                    String password = tokens[2];

                                    login(username, password);

                                } else if (tokens[0].equals("newuser")) {
                                    String username = tokens[1];
                                    String password = tokens[2];

                                    newUser(username, password);
                                } else if (tokens[0].equals("send")) {
                                    String message = tokens[1];

                                    send(message);
                                } else if (tokens[0].equals("logout")) {
                                    if (logout()) {
                                        // logout, close the client, and exit the program
                                        loggedin = false;
                                        echoClient.close();

                                        //exit out of program
                                        return;

                                    }
                                }
                            } else {
                                //commands were incorrect, do nothing
                            }
                        }catch(Exception e)
                        {

                        }
                    }
                }
            });

            Thread readMessageFromServer = new Thread( new Runnable()
            {
                /* messages from the server should come in two parts
                   1. Whether the message is normal or an error (true or false)
                   2. The message itself
                */
                public void run()
                {
                    while(loggedin) {

                        try {

                            boolean success = ins.readBoolean();
                            String message = ins.readUTF();

                            if(success)
                            {
                                // no error
                                System.out.println(message);
                            }
                            else
                            {
                                // error
                                System.out.println(message);

                            }
                            
                        } catch (Exception e) {

                        }
                    }
                }
            });

            sendMessageToServer.start();
            readMessageFromServer.start();

        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }

    public static boolean commandsAccepted(String line)
    {
        if(line.length() == 0)
        {
            System.out.println("No command was entered!");
            return false;
        }

        String deliminators = "[ ]";
        String[] tokens = line.split(deliminators);

        //Check correct format for login
        if(tokens[0].equals("login"))
        {
            if(tokens.length != 3)
            {
                System.out.println("Incorrect number of arguments for login!");
                return false;
            }
            return true;
        }
        else if (tokens[0].equals("newuser"))
        {
            if(tokens.length != 3)
            {
                System.out.println("Incorrect number of arguments for newuser!");
                return false;
            }
            return true;
        }
        else if (tokens[0].equals("send"))
        {
            if(tokens.length != 2)
            {
                System.out.println("No message given to send!");
                return false;
            }
            return true;
        }
        else if (tokens[0].equals("logout"))
        {
            return true;
        }
        else
        {
            System.out.println("Command not recognized!");
            return false;
        }
    }

    public static boolean login(String username, String password)
    {
        // handles the login functionality

        try {
            //send login info to server
            String line = "login " + username + " " + password;
            outs.writeUTF(line);

        }
        catch(Exception e){
            System.out.println("Error occured in login");
            System.out.println(e);
            return false;
        }

        return true;
    }

    public static boolean newUser(String username, String password)
    {
        // handles new user creation functionality

        try {
            //send login info to server
            String line = "newuser " + username + " " + password;
            outs.writeUTF(line);

        }
        catch(Exception e){
            System.out.println("Error occured in new user creation");
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static boolean send(String message)
    {
        // handles send functionality

        try {
            //send login info to server
            String line = "send " + message;
            outs.writeUTF(line);

        }
        catch(Exception e){
            System.out.println("Error occured in sending message");
            System.out.println(e);
            return false;
        }

        return true;
    }

    public static boolean logout()
    {
        // handles logout functionality
        try {
            //send login info to server
            String line = "logout";
            outs.writeUTF(line);

        }
        catch(Exception e){
            System.out.println("Error occured in logout");
            System.out.println(e);
            return false;
        }

        return true;
    }

}

