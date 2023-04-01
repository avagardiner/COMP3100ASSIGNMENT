import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("127.0.0.1", 50000);

            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort());
            System.out.println("Local IP: " + s.getLocalAddress() + " Local Port: " + s.getLocalPort());

            out.writeUTF("HELO\n");
            out.flush();
            System.out.println("SENT: HELO");

            String str = in.readLine();
            System.out.println("RCVD: " + str);

            in.close();
            out.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
