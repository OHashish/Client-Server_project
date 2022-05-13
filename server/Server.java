import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Server {
    public static void main(String[] args) throws IOException {

        ServerSocket server = null;
        ExecutorService service = null;

        // Try to open up the listening port
        try {
            server = new ServerSocket(8501);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8501.");
            System.exit(-1);
        }

        // Initialise the executor an create a fixed thread pool with a pool size of 15.
        service = Executors.newFixedThreadPool(15);

        // For each new client, submit a new handler to the thread pool.
        while( true ){
                Socket client = server.accept();            
                service.submit( new Handler(client) );
        }
    }
}