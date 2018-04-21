/**
 * Created by Morgan Knoch on 4/20/2018.
 */
import java.io.*;
import java.net.*;

public class EchoServer
{
    public static void main(String args[])
    {
        try
        {
            ServerSocket echoServer = new ServerSocket(11569);
            //Try not to use port number < 2000.
            System.out.println("Waiting for a client to connect...");
            while (true)
            {
                Socket s = echoServer.accept();
                System.out.println("Client Connected.");
                BufferedReader ins = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintStream outs = new PrintStream(s.getOutputStream());
                String line = ins.readLine();
                outs.println(line);
                s.close();
                System.out.println("Client Closed.");
            }
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
