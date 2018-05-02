/**
 * Created by Morgan Knoch on 4/20/2018.
 * Written for CS4850 Graduate Network Chatroom Project
 * This program creates a server socket connection, takes input
 * from up to three clients, and communicates to and from the clients to create
 * a multi-user chatroom.
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class EchoServer
{
    public static Vector<User> Users;
    public static Vector<ClientThreadHandler> activeClients = new Vector<>();
    public static String inputFile;
    public static final int MAXCLIENTS = 3;
    public static int numClients = 0;
    static Socket prevSocket;

    public static void main(String args[]) {
        inputFile = "users.txt";

        Users = getUsersFromFile(inputFile);
        Socket socket;
        ServerSocket echoServer;

        System.out.println("Waiting for a client to connect...");
        try{
            echoServer = new ServerSocket(11060);
        }catch(IOException ioe){
            System.out.println("System failed to create server socket.");
            return;
        }

        // keeps the server running indefinitely
        while(true) {

            // server accepts connection request from client
            try {
                socket = echoServer.accept();

                // if more than maximum clients allowed, don't create new objects for it
                if(numClients < MAXCLIENTS) {
                    System.out.println("Client Connected.");

                    // create I/O streams for communication between client and server
                    DataOutputStream outs = new DataOutputStream(socket.getOutputStream());
                    DataInputStream ins = new DataInputStream(socket.getInputStream());

                    // create new object for client thread execution
                    ClientThreadHandler newUser = new ClientThreadHandler(ins, outs, socket);

                    // create new thread for client thread object
                    Thread newThread = new Thread(newUser);

                    // add current user to the active user vector
                    activeClients.add(newUser);

                    // start the thread execution
                    newThread.start();

                    // keeps track of clients logged in
                    numClients += 1;
                }
                else {
                    // if more than maximum clients allowed, close socket
                    System.out.println("There are too many clients connected");
                    socket.close();
                }

            } catch (Exception e) {
                // print the error
                System.out.println(e);
            }
        }
    }

    public static Vector<User> getUsersFromFile(String inputFile)
    {
        // READ FILE AND PARSE OUT USERS AND PASSWORD AND PUT IN VECTOR

        //Using vector since it is synchronized
        Vector<User> Users = new Vector<>();

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
