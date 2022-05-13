import java.io.*;
import java.net.*;
import java.util.*;

public class Client{
	 public void connect(String[] clientInput) {
        try {
            // Open up a connection with host ('localhost'), port number 8501.
            Socket s = new Socket( "localhost", 8501 );

            // Buffer the input stream for performance.
            BufferedReader reader = new BufferedReader(
                                       new InputStreamReader(
                                          s.getInputStream() ) );
                                          
            // Writer to send which request was specified by the user
            PrintWriter writer = new PrintWriter(s.getOutputStream(),true);

            // Send the first argmuent to the server
            writer.println(clientInput[0]);

            // Get number of files from the server .
            // loop and read file names from the server then print them
            if (clientInput[0].equals("list")){
                System.out.println( "Listed Files:" );
                int numFiles = Integer.parseInt(reader.readLine());
                String fileName = null;
                // Print all file names
                for(int i=0; i<numFiles; i++) {
                    fileName = reader.readLine();
                    System.out.println(fileName);
                }
            }
            else {
                // If the request is to get a file 
                // send the filename to the server and read the sent bytes 
                // count the received bytes from the sever and if they are not 
                // greater than one then the file doesn't exist and display
                // an error message  
                writer.println(clientInput[1]);
                InputStream byteReader = s.getInputStream();
                byte[] bytes = new byte[1];
                int bytesRead;
                bytesRead = byteReader.read(bytes, 0, bytes.length);
                int count =0;
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                do {
                        byteStream.write(bytes);
                        bytesRead = byteReader.read(bytes);
                        count ++;
                } while (bytesRead != -1);
                byteReader.close();
                if(count>1){
                    File file = new File(clientInput[1]);
                    BufferedOutputStream fileWriter = new BufferedOutputStream(
                                                        new FileOutputStream( file ));
                    fileWriter.write(byteStream.toByteArray());
                    // Close the file and flush the writer.
                    fileWriter.flush();
                    fileWriter.close();
                    System.out.println( "File received." );
                }
                else {
                    System.out.println("Requested file doesn't exist. Please try another one.");
                }
            }
                // Close the reader , writer and the connection.
                reader.close();
                writer.close();
                s.close();
        }catch( IOException e ){
            System.out.println("An unexpected error has occured. Please try again.");
        }
    }

    public static void main(String[] args){	
        // validate the argument from the command line and display error messages
        // if the agument is validated create a client 
        //and connect with argument as paramters 
        if (args.length !=0){
            if (args[0].equals("list")){
                Client client = new Client();
                client.connect(args);
                
            }
            else if (args[0].equals("get")){
                if (args.length==1){
                    System.out.println("No text file was specified.");
                }
                else if ( args[1].endsWith(".txt")){
                    Client client = new Client();
                    client.connect(args);
                }
                else {
                    System.out.println("Provided file was not a text file.");
                }

            }
            else{
                System.out.println("Cannot process argument.Please provide either list or get [filname] as arguments.");
            }
        }
        else if (args.length >=3){
            System.out.println("Cannot process argument.Please provide either list or get [filname] as arguments.");
        }
        else{
            System.out.println("Error.No arguments provided");
        }
		
    }
}