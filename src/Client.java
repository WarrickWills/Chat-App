
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Warrick Wills 13831575
 * @author Keenen Leyson 13828049
 */
public class Client {

    public String HOST_NAME;
    public static final int HOST_PORT = 6666;
    private String clientName;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private ClientGUI cgui;

    //-----------------------------------------------------------------------------------------
    public Client() {

    }

    //-----------------------------------------------------------------------------------------
    //creates a new client
    public void startClient() throws IOException {
        try {
            ServerSend ss = new ServerSend();
            ss.start();
            //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            Socket clientSocket = new Socket(HOST_NAME, HOST_PORT);
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
            ServerListener sl = new ServerListener();
            sl.start();

            //Sends a empty value instead of message
            //send("");
            output.writeObject(new BroadcastMessage(clientName));

            cgui.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (JOptionPane.showConfirmDialog(cgui,
                            "Are you sure you want to exit?", "Exiting?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        try {
                            send(new DisconnectMessage());
                        } catch (IOException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        cgui.dispose();
                        System.exit(0);
                    }
                }
            });
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
    // allows user to enter username
    public void getClientName() {
        Scanner scan = new Scanner(System.in);
        while (clientName == null || clientName.isEmpty()) {
            System.out.println("Please enter your display name: ");
            clientName = scan.nextLine();
        }

    }

    //-----------------------------------------------------------------------------------------
    public String getClientNameGUI() {
        return clientName;
    }

    //-----------------------------------------------------------------------------------------
    //sends a message to the server
    public void send(Message mes) throws IOException {
        try {
            output.writeObject(mes);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------
//    //runs client command line
//    public static void main(String[] args) throws IOException {
//        Client client = new Client();
//        client.getClientName(); //gets client name
//        client.startClient(); //stars client
//        Scanner scan = new Scanner(System.in);
//        while (true) {
//            System.out.print("> ");
//            String mes = scan.nextLine();
//            System.out.println("Who do you want to send the message to? ('all' or enter username): ");
//            String sendToClient = scan.nextLine();
//            if (sendToClient.equalsIgnoreCase("all")) {
//                client.send(new BroadcastMessage(client.clientName + "(ALL): " + mes));
//            } else {
//                client.send(new MessageTo(client.clientName+"(PRIVATE): " + mes, sendToClient));
//            }
//        }
//    }
    //-----------------------------------------------------------------------------------------
    //runs client gui
    public static void main(String[] args) throws IOException {

        Client client = new Client();
        final ClientGUI ui = new ClientGUI(client);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ui.setVisible(true);
            }
        });
    }

    //-----------------------------------------------------------------------------------------
    public void setCgui(ClientGUI cgui) {
        this.cgui = cgui;
    }

    //-----------------------------------------------------------------------------------------
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    //-----------------------------------------------------------------------------------------
    public void setIPAddress(String address) {
        this.HOST_NAME = address;
    }

    //-----------------------------------------------------------------------------------------
    //thread which listens to the input and prints it to server console
    class ServerListener extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    String mes = (String) input.readObject();
                    cgui.append(mes);
                    System.out.println(mes);
                    System.out.print("> ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //-----------------------------------------------------------------------------------------
    //thread which sends address data and receives list of online users data
    //it allows connected clients to be updated
    class ServerSend extends Thread {

        @Override
        public void run() {
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName(HOST_NAME);
                byte[] sendData = new byte[1024];
                byte[] receiveData = new byte[1024];
                while (true) {
                    String out = "";
                    sendData = out.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
                    clientSocket.send(sendPacket);
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    String rec = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println(rec);
                    cgui.updateOnlineClients(rec);
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
    }
}
