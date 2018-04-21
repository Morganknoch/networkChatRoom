/**
 * Created by Morgan Knoch on 4/20/2018.
 */
import java.io.*;
import java.net.*;

public class EchoClient
{
    public static void main(String args[])
    {
        if(args.length != 1)
        {
            System.out.println("EchoClient MachineName");
        }

        InputStreamReader convert = new InputStreamReader(System.in);
        BufferedReader stdin = new BufferedReader(convert);

        try
        {
            Socket echoClient = new Socket(args[0], 11569);
            PrintStream outs = new PrintStream(echoClient.getOutputStream());
            BufferedReader ins = new BufferedReader(new InputStreamReader(echoClient.getInputStream()));

            System.out.print("Type whatever you want: ");
            String line = stdin.readLine();
            outs.println(line);
            System.out.println("Server says: " + ins.readLine());

            echoClient.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}

