/**
 * Created by Morgan Knoch on 4/21/2018.
 */
import java.io.*;
import java.net.*;


public class Test extends Thread{

    public static void main(String args[]){

        String inputFile = "users.txt";
        String username = "Bob";
        String password = "Bob44";

        InputStreamReader convert = new InputStreamReader(System.in);
        BufferedReader stdin = new BufferedReader(convert);

        try{
            System.out.println("Start thread");
            (new Thread(new Test())).start();
            System.out.println("Enter commands");
            String line = stdin.readLine();
            System.out.println("Start new thread");
            (new Thread(new Test())).start();
        }catch(IOException e){

        }

    }
    public void run(){
        int x = 0;
        while(x++<1000000000)
        {
            System.out.println(x);
        }
    }
}