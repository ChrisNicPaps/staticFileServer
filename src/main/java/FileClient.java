
/*
       This program is a client for the FileServer server.  The
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
       file does not exist on the server.  (The server can also
       respond with the message "unknown command" if the command
       it reads is not one of the two possible legal commands.)

       The client program works with command-line arguments.
       The first argument must be the name or IP address of the
       computer where the server is running.  If that is the
       only argument on the command line, then the client
       gets the list of files from the server and displays
       it on standard output.  If there are two parameters,
       the second parameter is interpreted as the name of a
       file to be downloaded.  A copy of the file is saved
       as a local file of the same name, unless a file of
       the same name already exists.  If there are three
       arguments, the second is the name of the file to be
       downloaded and the third is the name under which the
       local copy of the file is to be saved.  This will
       work even if a file of the same name already exists.

       This program uses the non-standard class, TextReader.
   */

import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.io.*;

public class FileClient {

    static final int LISTENING_PORT = 3210;
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;



    public final static BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(1);

    public static void main(String[] args) {

        String computer; // Name or IP address of server.
// Socket connection; // A socket for communicating with
// that computer.
        PrintWriter outgoing; // Stream for sending a command
// to the server.
// TextReader incoming; // Stream for reading data from
// the connection.
        String command; // Command to send to the server.

        /*
         * Check that the number of command-line arguments is legal. If not, print a
         * usage message and end.
         */

       /* if (args.length == 0 || args.length > 3) {
            System.out.println("Usage:  java FileClient <server>");
            System.out.println("    or  java FileClient <server> <file>");
            System.out.println("    or  java FileClient <server> <file> <local-file>");
            return;
        }*/

        /* Get the server name and the message to send to the server. */
/*
        computer = args[0];

        if (args.length == 1)
            command = "index";
        else
            command = "get " + args[1];*/
        waitForRequest();
        /*
         * Make the connection and open streams for communication. Send the command to
         * the server. If something fails during this process, print an error message
         * and end.
         */
/*ServerSocket listener; // Listens for connection requests.
Socket connection; // A socket for communicating with
// a client.
try {
listener = new ServerSocket(LISTENING_PORT, 1);
System.out.println("Listening on port " + LISTENING_PORT);
while (true) {
connection = listener.accept();
ConnectionHandler conn = new ConnectionHandler(connection);
clients.add(conn);
conn.start();
}
} catch (Exception e) {
System.out.println("Server shut down unexpectedly.");
System.out.println("Error:  " + e);
return;
}

try {
connection = new Socket(computer, LISTENING_PORT);

dataInputStream = new DataInputStream(connection.getInputStream());
dataOutputStream = new DataOutputStream(connection.getOutputStream());

} catch (Exception e) {
System.out.println("Can't make connection to server at \"" + args[0] + "\".");
System.out.println("Error:  " + e);
return;
} finally {
dataInputStream.close();
dataOutputStream.close();
}
*/
        /* Read and process the server's response to the command. */
/*
try {
if (args.length == 1) {
// The command was "index". Read and display lines
// from the server until the end-of-stream is reached.
System.out.println("File list from server:");
while (incoming.eof() == false) {
String line = incoming.getln();
System.out.println("   " + line);
}
} else {
// The command was "get <file-name>". Read the server's
// response message. If the message is "ok", get the file.
String message = incoming.getln();
if (!message.equals("ok")) {
System.out.println("File not found on server.");
return;
}
PrintWriter fileOut; // For writing the received data to a file.
if (args.length == 3) {
// Use the third parameter as a file name.
fileOut = new PrintWriter(new FileWriter(args[2]));
} else {
// Use the second parameter as a file name,
// but don't replace an existing file.
File file = new File(args[1]);
if (file.exists()) {
System.out.println("A file with that name already exists.");
System.out.println("To replace it, use the three-argument");
System.out.println("version of the command.");
return;
}
fileOut = new PrintWriter(new FileWriter(args[1]));
}
while (incoming.peek() != '\0') {
// Copy lines from incoming to the file until
// the end of the incoming stream is encountered.
String line = incoming.getln();
fileOut.println(line);
}
if (fileOut.checkError()) {
System.out.println("Some error occurred while writing the file.");
System.out.println("Output file might be empty or incomplete.");
}
}
} catch (Exception e) {
System.out.println("Sorry, an error occurred while reading data from the server.");
System.out.println("Error: " + e);
}*/

    } // end main()

    public static void waitForRequest() {
        ServerSocket server;
        Socket socket;

        try {
            server = new ServerSocket(LISTENING_PORT);
            try {
                while (true) {
                    System.out.println("Listening on port " + LISTENING_PORT);
                    socket = server.accept();
                    System.out.println("Connection Established: " + LISTENING_PORT);
                    serviceRequest(socket);
                    socket.close();

                }
            } catch (IOException e) {
                server.close();
                System.err.println(e);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void serviceRequest(Socket socket) {
        ClientConnectionHandler cHandler = new ClientConnectionHandler(socket);
        cHandler.start();

        try {
            while (true) {
                queue.take();
                System.out.println("Client Connection ending");
            }

        } catch (InterruptedException ex) {
            System.out.println("Major exception!!!");
        }
    }

    static class ClientConnectionHandler extends Thread {
// An object of this class is a thread that will
// process the connection with one client. The
// thread starts itself in the constructor.

        Socket socket; // A connection to the client.
        PrintWriter outgoing; // For transmitting data to the client.

        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        private ObjectInputStream inputStream = null;
        private FileEvent fileEvent;
        private File dstFile = null;
        private FileOutputStream fileOutputStream = null;

        private boolean isConnected = false;
        private ObjectOutputStream outputStream = null;
        private String sourceDirectory = "C:/temp/server";
        private String destinationDirectory = "C:/temp/client/";
        private int fileCount = 0;

        public ClientConnectionHandler(Socket conn) {
// Constructor. Record the connection and
// the directory and start the thread running.
            socket = conn;
            try {
                dataInputStream = new DataInputStream(conn.getInputStream());
                dataOutputStream = new DataOutputStream(conn.getOutputStream());
                inputStream = new ObjectInputStream(conn.getInputStream());
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }/*
            finally {
                try {
                    System.out.println("CLOSING STREAMS!!!");
                    inputStream.close();
                    dataInputStream.close();
                    dataOutputStream.close();
                } catch (IOException e) {
// TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }*/

            //start();
        }

        public void run() {

            downloadFiles();
            queue.add(1);
        }

        private void receiveFile(String fileName) throws Exception {
            int bytes = 0;
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);

            long size = dataInputStream.readLong(); // read file size
            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes; // read upto file size
            }
            fileOutputStream.close();
        }


        /**
         * Reading the FileEvent object and copying the file to disk.
         */
        public void downloadFiles() {
            while (socket.isConnected()) {
                try {
                    fileEvent = (FileEvent) inputStream.readObject();
                    if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                        System.out
                                .println("Error occurred ..with file" + fileEvent.getFilename() + "at sending end ..");
                    }
                    System.out.println(fileEvent.getDestinationDirectory() + " || " + fileEvent.getFilename());
                    String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
                    if (!new File(fileEvent.getDestinationDirectory()).exists()) {
                        new File(fileEvent.getDestinationDirectory()).mkdirs();
                    }
                    if(fileEvent.isFileTypeDir())
                    {
                        new File(fileEvent.getDestinationDirectory()+ fileEvent.getFilename()).mkdirs();
                        System.out.println("Directory Successfully created.");
                    }
                    else
                    {
                        dstFile = new File(outputFile);
                        fileOutputStream = new FileOutputStream(dstFile);
                        fileOutputStream.write(fileEvent.getFileData());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        System.out.println("Output file : " + outputFile + " is successfully saved ");
                        if (fileEvent.getRemainder() == 0) {
                            System.out.println("Whole directory is copied...So system is going to exit");
                            System.exit(0);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
} // end class FileClient

