/**
 * Created by Morgan Knoch on 4/21/2018.
 */
import java.io.*;
import java.net.*;
//import java.nio.file.Files;

public class Test {

    public static void main(String args[]){


        String inputFile = "users.txt";
        String username = "Bob";
        String password = "Bob44";

        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile, true));
            writer.append("\n" + username + " " + password);

            writer.close();
        }catch(IOException e){
            System.out.println(e);

        }
    }

}
