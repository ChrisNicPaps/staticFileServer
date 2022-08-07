/*
       This program is a very simple network file server.  The
       server has a list of available text files that can be
       downloaded by the client.  The client can also download
       the list of files.  When the connection is opened, the
       client sends one of two possible commands to the server:
       "index" or "get <file-name>".  The server replies to
       the first command by sending the list of available files.
       It responds to the second with a one-line message,
       either "ok" or "error".  If the message is "ok", it is
       followed by the contents of the file with the specified
       name.  The "error" message indicates that the specified
       file does not exist on the server. (The server can also
       respond with the message "unknown command" if the command
       it reads is not one of the two possible legal commands.)

       The server program requires a command-line parameter
       that specifies the directory that contains the files
       that the server can serve.  The files should all be
       text files, but this is not checked.  Also, the server
       must have permission to read all the files.

       This program uses the non-standard class, TextReader.
   */

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.util.List;

public class FileServer {

    static final int LISTENING_PORT = 3210;
    static ArrayList<ConnectionHandler> clients = new ArrayList<ConnectionHandler>();
    static String computer = "127.0.0.1";
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) {

        File directory; // The directory from which the
// gets the files that it serves.
        ServerSocket listener; // Listens for connection requests.
        Socket connection = null; // A socket for communicating with
// a client.

        /*
         * Check that there is a command-line argument. If not, print a usage message
         * and end.
         */
/*
        if (args.length == 0) {
            System.out.println("Usage:  java FileServer <directory>");
            return;
        }
*/
        /*
         * Get the directory name from the command line, and make it into a file object.
         * Check that the file exists and is in fact a directory.
         */
/*
        directory = new File(args[0]);
        if (!directory.exists()) {
            System.out.println("Specified directory does not exist.");
            return;
        }
        if (!directory.isDirectory()) {
            System.out.println("The specified file is not a directory.");
            return;
        }
*/
        /*
         * Listen for connection requests from clients. For each connection, create a
         * separate Thread of type ConnectionHandler to process it. The
         * ConnectionHandler class is defined below. The server runs until the program
         * is terminated, for example by a CONTROL-C.
         */
/*
try {
listener = new ServerSocket(LISTENING_PORT);
System.out.println("Listening on port " + LISTENING_PORT);
while (true) {
connection = listener.accept();
ConnectionHandler conn = new ConnectionHandler(directory, connection);
clients.add(conn);
conn.start();
}
} catch (Exception e) {
System.out.println("Server shut down unexpectedly.");
System.out.println("Error:  " + e);
return;
}*/

        try {
            System.out.println("Connecting to client .");
            connection = new Socket(computer, LISTENING_PORT);
            System.out.println("Connection Established");
ConnectionHandler cHandler = new ConnectionHandler(connection);
           while(connection.isConnected())
           {
               
           }

        } catch (Exception e) {
            System.out.println("Can't make connection to server at \"" + args[0] + "\".");
            System.out.println("Error:  " + e);
            return;
        } finally {
            
            try {
                connection.close();
                dataInputStream.close();
                dataOutputStream.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    } // end main()

    static class ConnectionHandler extends Thread {
// An object of this class is a thread that will
// process the connection with one client. The
// thread starts itself in the constructor.

        File directory; // The directory from which files are served
        Socket connection; // A connection to the client.
        PrintWriter outgoing; // For transmitting data to the client.

        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;

        private boolean isConnected = false;
        private ObjectOutputStream outputStream = null;
        private String sourceDirectory = "C:/Temp/server";
        private String destinationDirectory = "C:/Temp/client/";
        private int fileCount = 0;
        private FileEvent fileEvent = null;

        ConnectionHandler(Socket conn) throws IOException {
// Constructor. Record the connection and
// the directory and start the thread running.
            connection = conn;
            outputStream= new ObjectOutputStream(conn.getOutputStream());
            dataInputStream = new DataInputStream(conn.getInputStream());
            dataOutputStream = new DataOutputStream(conn.getOutputStream());

            //showFiles(new File(sourceDirectory).listFiles());

            start();
        }

        public void showFiles(File[] files) throws IOException {
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("Directory: " + file.getAbsolutePath());
                    showFiles(file.listFiles()); // Calls same method again.
                } else {
                    System.out.println("File: " + file.getAbsolutePath());
                }
            }
        }


        public List<File> recListFile(File[] files) throws IOException {
            List<File> dirList = new ArrayList<File>();
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("Directory: " + file.getAbsolutePath());
                    dirList.add(file);
                    dirList.addAll(recListFile(file.listFiles())); // Calls same method again.
                } else {
                    dirList.add(file);
                    System.out.println("File: " + file.getAbsolutePath());
                }
            }
            return dirList;
        }

        public void locateFiles()  {
            File srcDir = new File(sourceDirectory);
            if (!srcDir.isDirectory()) {
                System.out.println("Source directory is not valid ..Exiting the client");
                System.exit(0);
            }
           // File[] files = srcDir.listFiles();
            List<File> files = new ArrayList<File>();
            try {
                files = recListFile(new File(sourceDirectory).listFiles());
            }
            catch( IOException ex)
            {
                System.out.println("Source directory is not valid ..Exiting the client");
                System.exit(0);
            }

            fileCount = files.size();
            if (fileCount == 0) {
                System.out.println("Empty directory ..Exiting the client");
                System.exit(0);
            }

            int i = 0;
            for (File file : files) {
                System.out.println("Sending " + file.getAbsolutePath());

                sendFile(file.getAbsolutePath(), fileCount - i - 1);
                System.out.println(file.getAbsolutePath());
                i++;
            }
            /*for (int i = 0; i < fileCount; i++) {
                System.out.println("Sending " + files[i].getAbsolutePath());

                sendFile(files[i].getAbsolutePath(), fileCount - i - 1);
                System.out.println(files[i].getAbsolutePath());
            }*/
        }


        public void sendFile(String fileName, int index) {
            fileEvent = new FileEvent();
            fileEvent.setDestinationDirectory(destinationDirectory);
            fileEvent.setSourceDirectory(sourceDirectory);
            File file = new File(fileName);
            try {

                if(file.isDirectory())
                fileEvent.setFileType(true);
            else {
                fileEvent.setFileType(false);
                DataInputStream diStream = null;
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
            }
            System.out.println(file.getAbsoluteFile().toString().replace(new File(sourceDirectory).getAbsolutePath()+"\\","")+ " || "+ file.getName());
            fileEvent.setFilename(file.getAbsoluteFile().toString().replace(new File(sourceDirectory).getAbsolutePath()+"\\",""));
                System.out.println("New File Name: "+file.getAbsoluteFile().toString().replace(new File(sourceDirectory).getAbsolutePath()+"\\",""));
            fileEvent.setRemainder(index);

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

        void sendFile(String path) throws Exception {
            int bytes = 0;
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

// send file size
            dataOutputStream.writeLong(file.length());
// break file into chunks
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }
            fileInputStream.close();
        }

        void sendIndex() throws Exception {
// This is called by the run() method in response
// to an "index" command. Send the list of files
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
         * void sendFile(String fileName) throws Exception { // This is called by the
         * run() command in response // to "get <fileName>" command. If the file doesn't
         * // exist, send the message "error". Otherwise, // send the message "ok"
         * followed by the contents // of the file. File file = new
         * File(directory,fileName); if ( (! file.exists()) || file.isDirectory() ) { //
         * (Note: Don't try to send a directory, which // shouldn't be there anyway.)
         * outgoing.println("error"); } else { outgoing.println("ok"); TextReader fileIn
         * = new TextReader( new FileReader(file) ); while (fileIn.peek() != '\0') { //
         * Read and send lines from the file until // an end-of-file is encountered.
         * String line = fileIn.getln(); outgoing.println(line); } } outgoing.flush();
         * outgoing.close(); if (outgoing.checkError()) throw new
         * Exception("Error while transmitting data."); }
         */
/*
public void run() {
// This is the method that is executed by the thread.
// It creates streams for communicating with the client,
// reads a command from the client, and carries out that
// command. The connection is logged to standard output.
// An output beginning with ERROR indicates that a network
// error occurred. A line beginning with OK means that
// there was no network error, but does not imply that the
// command from the client was a legal command.
String command = "Command not read";
try {
incoming = new TextReader(connection.getInputStream());
outgoing = new PrintWriter(connection.getOutputStream());
command = incoming.getln();
if (command.equals("index")) {
sendIndex();
} else if (command.startsWith("get")) {
String fileName = command.substring(3).trim();
sendFile(fileName);
} else {
outgoing.println("unknown command");
outgoing.flush();
}
System.out.println("OK    " + connection.getInetAddress() + " " + command);
} catch (Exception e) {
System.out.println("ERROR " + connection.getInetAddress() + " " + command + " " + e);
} finally {
try {
connection.close();
dataInputStream.close();
dataOutputStream.close();
} catch (IOException e) {
}
}
}
*/

        public void run() {
            locateFiles();
        }

    } // end nested class ConnectionHandler

} // end class FileServer
