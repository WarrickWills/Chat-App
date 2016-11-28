
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Warrick Wills 13831575
 * @author Keenen Leyson 13828049
 */
public class Server {

    private ArrayList<HandleMessages> clients;
    private static final int PORT = 6666; // some unused port number

    //-----------------------------------------------------------------------------------------
    //server constructor
    public Server() {
        try {
            System.out.println(InetAddress.getLocalHost());
            clients = new ArrayList<>();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //-----------------------------------------------------------------------------------------
    // start the server if not already started and repeatedly listen
    // for client connections until stop requested
    public void startServer() throws IOException {
        try {
            ServerClientListener scl = new ServerClientListener();
            scl.start();
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                HandleMessages hm = new HandleMessages(socket);
                clients.add(hm);
                hm.start();
            }
        } catch (UnknownHostException ioe) {
            JOptionPane.showMessageDialog(null, "Server not found!");
            System.exit(0);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Server not found!");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------
    //runs server
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startServer();
    }

    //-----------------------------------------------------------------------------------------
    //Thread which handles messages
    public class HandleMessages extends Thread {

        private Socket clientSocket;
        private String clientName;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private Message message;
        private Client client;

        //-----------------------------------------------------------------------------------------
        //handle message constructor
        public HandleMessages(Socket socket) {
            this.clientSocket = socket;
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                BroadcastMessage bm = (BroadcastMessage) input.readObject();
                clientName = bm.toString();
                send(clientName + " has joined\n");
                System.out.println(clientName + " has joined");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //-----------------------------------------------------------------------------------------
        //determines type of message to be sent
        @Override
        public void run() {
            while (true) {
                try {
                    message = (Message) input.readObject();
                    if (message instanceof MessageTo) {
                        System.out.println("private");
                        MessageTo messageTo = (MessageTo) message;
                        sendTo(messageTo.getMessage(), messageTo.getName());
                    } else if (message instanceof BroadcastMessage) {
                        System.out.println("broadcast");
                        String message = this.message.getMessage();
                        send(message);
                    } else if (message instanceof CancelMessage) {
                        String message = this.message.getMessage();
                        send(message);
                    } else {
                        clients.remove(this);
                        System.out.println(clientName + " has disconnected");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Message recieved: " + message);
            }
        }

        //-----------------------------------------------------------------------------------------
        //checks if socket connected, if so writes message
        public boolean writeMes(String mes) {
            if (!clientSocket.isConnected()) {
                return false;
            }
            try {
                output.writeObject(mes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        //-----------------------------------------------------------------------------------------
        //Sends message to all connected clients
        public boolean send(String mes) {
            for (HandleMessages han : clients) {
                han.writeMes(mes);
            }
            return true;
        }

        //-----------------------------------------------------------------------------------------
        //Sends message to a connected client
        public boolean sendTo(String mes, String name) {
            for (HandleMessages han : clients) {
                if (han.getClientName().equalsIgnoreCase(name)) {
                    han.writeMes(mes);
                    if (!clientName.equalsIgnoreCase(han.getClientName())) {
                        writeMes(mes);
                    }
                    return true;
                }
            }
            writeMes("Nobody called '" + name + "' is present in this chat room\n");
            return true;
        }

        //-----------------------------------------------------------------------------------------
        // gets client name
        public String getClientName() {
            return clientName;
        }
    }

    //-----------------------------------------------------------------------------------------
    class ServerClientListener extends Thread {

        @Override
        public void run() {
            try {
                DatagramSocket serverSocket = new DatagramSocket(9999);
                byte[] receiveData = new byte[1024];
                byte[] sendData = new byte[1024];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    String c = "";
                    for (int i = 0; i < clients.size(); i++) {
                        c += clients.get(i).getClientName() + "_";
                    }
                    sendData = c.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
            } catch (SocketException se) {
                se.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
