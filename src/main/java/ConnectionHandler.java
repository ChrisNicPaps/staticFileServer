
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler {
    // An object of this class is a thread that will
    // process the connection with one client.  The
    // thread starts itself in the constructor.

    File directory; // The directory from which files are served
    Socket connection; // A connection to the client.
    PrintWriter outgoing; // For transmitting data to the client.

    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;

    private boolean isConnected = false;
    private ObjectOutputStream outputStream = null;
    private String sourceDirectory = "C:/temp/server";
    private String destinationDirectory = "C:/temp/client/";
    private int fileCount = 0;
    private FileEvent fileEvent = null;


    ConnectionHandler(File dir, Socket conn) throws IOException {
        // Constructor.  Record the connection and
        // the directory and start the thread running.
        directory = dir;
        connection = conn;
        dataInputStream = new DataInputStream(conn.getInputStream());
        dataOutputStream = new DataOutputStream(conn.getOutputStream());
        //start();
    }



    ConnectionHandler(int unit, Socket conn) throws IOException {
        // Constructor.  Record the connection and
        // the directory and start the thread running.
        unit = unit;
        connection = conn;
        dataInputStream = new DataInputStream(conn.getInputStream());
        dataOutputStream = new DataOutputStream(conn.getOutputStream());
        //start();
    }

    public void locateFiles() {
        File srcDir = new File(sourceDirectory);
        if (!srcDir.isDirectory()) {
            System.out.println("Source directory is not valid ..Exiting the client");
            System.exit(0);
        }
        File[] files = srcDir.listFiles();
        fileCount = files.length;
        if (fileCount == 0) {
            System.out.println("Empty directory ..Exiting the client");
            System.exit(0);
        }

        for (int i = 0; i < fileCount; i++) {
            System.out.println("Sending " + files[i].getAbsolutePath());
            sendFile(files[i].getAbsolutePath(), fileCount - i - 1);
            System.out.println(files[i].getAbsolutePath());
        }
    }

    public void sendFile(String fileName, int index) {
        fileEvent = new FileEvent();
        fileEvent.setDestinationDirectory(destinationDirectory);
        fileEvent.setSourceDirectory(sourceDirectory);
        File file = new File(fileName);
        fileEvent.setFilename(file.getName());
        fileEvent.setRemainder(index);
        DataInputStream diStream = null;
        try {
            diStream = new DataInputStream(new FileInputStream(file));
            long len = (int) file.length();
            byte[] fileBytes = new byte[(int) len];
            int read = 0;
            int numRead = 0;
            while (read < fileBytes.length
                    && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
                read = read + numRead;
            }
            fileEvent.setFileData(fileBytes);
            fileEvent.setStatus("Success");
        } catch (Exception e) {
            e.printStackTrace();
            fileEvent.setStatus("Error");

        }

        try {
            outputStream.writeObject(fileEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        dataOutputStream.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }


    void sendIndex() throws Exception {
        // This is called by the run() method in response
        // to an "index" command.  Send the list of files
        // in the directory.
        String[] fileList = directory.list();
        for (int i = 0; i < fileList.length; i++)
            outgoing.println(fileList[i]);
        outgoing.flush();
        outgoing.close();
        if (outgoing.checkError())
            throw new Exception("Error while transmitting data.");
    }
         
   /*
         void sendFile(String fileName) throws Exception {
               // This is called by the run() command in response
               // to "get <fileName>" command.  If the file doesn't
               // exist, send the message "error".  Otherwise,
               // send the message "ok" followed by the contents
               // of the file.
            File file = new File(directory,fileName);
            if ( (! file.exists()) || file.isDirectory() ) {
                 // (Note:  Don't try to send a directory, which
                 // shouldn't be there anyway.)
               outgoing.println("error");
            }
            else {
               outgoing.println("ok");
               TextReader fileIn = new TextReader( new FileReader(file) );
               while (fileIn.peek() != '\0') {
                     // Read and send lines from the file until
                     // an end-of-file is encountered.
                  String line = fileIn.getln();
                  outgoing.println(line);
               }
            }
            outgoing.flush();
            outgoing.close();
            if (outgoing.checkError())
               throw new Exception("Error while transmitting data.");
         }
         */
   
        /* public void run() {
               // This is the method that is executed by the thread.
               // It creates streams for communicating with the client,
               // reads a command from the client, and carries out that
               // command.  The connection is logged to standard output.
               // An output beginning with ERROR indicates that a network
               // error occurred.  A line beginning with OK means that
               // there was no network error, but does not imply that the
               // command from the client was a legal command.
            String command = "Command not read";
            try {
               incoming = new TextReader( connection.getInputStream() );
               outgoing = new PrintWriter( connection.getOutputStream() );
               command = incoming.getln();
               if (command.equals("index")) {
                  sendIndex();
               }
               else if (command.startsWith("get")){
                  String fileName = command.substring(3).trim();
                  sendFile(fileName);
               }
               else {
                  outgoing.println("unknown command");
                  outgoing.flush();
               }
               System.out.println("OK    " + connection.getInetAddress()
                                           + " " + command);
            }
            catch (Exception e) {
               System.out.println("ERROR " + connection.getInetAddress()
                                        + " " + command + " " + e);
            }
            finally {
               try {
                  connection.close();              
                  dataInputStream.close();
                  dataOutputStream.close();
               }
               catch (IOException e) {
               }
            }
         }*/

}  // end nested class ConnectionHandler