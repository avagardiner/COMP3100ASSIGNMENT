import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class server {

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println("Usage: java TCPServer <port number>");
                System.exit(1);
            }
            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Accepted connection from " + socket.getInetAddress() + ":" + socket.getPort());

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

                String str = (String) in.readUTF();
                System.out.println("Received from client: " + str);

                out.writeUTF("Hi from server!");
                System.out.println("Sent to client: Hi from server!");

                str = (String) in.readUTF();
                System.out.println("Received from client: " + str);

                out.writeUTF("Bye from server!");
                System.out.println("Sent to client: Bye from server!");

                in.close();
                out.close();
                socket.close();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}