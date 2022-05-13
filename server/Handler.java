import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;  

public class Handler extends Thread{
    private Socket sock = null;

    public Handler(Socket sock) {
        super("Handler");
		this.sock = sock;
    }

	public synchronized void run() {

            try {
                // Buffered Reader for performance get request from Client.
                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(
                                            sock.getInputStream() ) );

                PrintWriter writer = new PrintWriter(sock.getOutputStream());
                // Declare strings for request and requestedFile
                // Declare a boolean exists as false where it will be used to assign to if a
                // file exists or not
                String request,requestedFile = null; 
                boolean exists = false;
                // Read the request from the user
                request = reader.readLine();
                // if the request is for getting a file then get the filename and and assign true or false 
                // to the boolean 
                if (request.equals("get")){
                    requestedFile= reader.readLine();
                    File checkExists = new File("./serverFiles/"+requestedFile);
                    exists = checkExists.exists();
                }

                // Get the IP address of the client. Also get the date and time.
                // Open log.txt file for writing and write valid requests to the file
                // Write the log for getting the file if the requested file exists
                Date fullDate = new Date();
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");  
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");  
                String date=dateFormatter.format(fullDate).toString();
                String time=timeFormatter.format(fullDate).toString();

                InetAddress inet = sock.getInetAddress();
                String address = inet.getHostAddress (); 

                try {
                    FileWriter logWriter = new FileWriter("log.txt",true);
                    if (request.equals("list")){
                        logWriter.write(date +":"+time+":"+address +":"+ request +"\n");
                    }
                    else{
                        logWriter.write(date +":"+time+":"+address +":"+ request +" "+ requestedFile+"\n");
                    }
                    logWriter.close();
                } catch (IOException e) {
                    System.out.println("An error occurred while trying to write to a file.");
                }


                if (request.equals("list")){
                    // Get the file from the given directory
                    // Add all file names of server files to an array and Send it to client after
                    // sending the length
                    String directory = System.getProperty("user.dir")+ "/serverFiles";
                    File directoryPath = new File("./serverFiles");
                    String contents[] = directoryPath.list();
                    writer.println(contents.length);
                    for(int i=0; i<contents.length; i++) {
                        writer.println(contents[i]);
                    }
                }
                else{
                    // If requested file exists get the file from the serverFiles directory
                    // Declare a byte array with the length of the file
                    // Read the file using a buffered input stream with the file length into the byte array
                    // then write the byte array to the output stream to the client
                    if (exists){
                        File sendFile = new File("./serverFiles/"+requestedFile);
                        byte[] sendFileArray = new byte[(int) sendFile.length()];

                        BufferedInputStream fileReader = new BufferedInputStream(
                                                                new FileInputStream(sendFile));
                        BufferedOutputStream fileWriter = new BufferedOutputStream(
                                                                sock.getOutputStream());
                        fileReader.read(sendFileArray, 0, sendFileArray.length);
                        fileWriter.write(sendFileArray, 0, sendFileArray.length);
                        // Close the file reader and the file writer (and flush it) 
                        fileWriter.flush();
                        fileWriter.close();
                        fileReader.close();
                    }
                }   
                    // Close the reader , writer and the connection.                 
                    writer.close();
                    reader.close();
                    sock.close();
            }catch (IOException e) {
                System.out.println( "An unexpected error ocuured . Please try again." );
            }
    }
}