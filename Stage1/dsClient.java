import java.net.Socket;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.TimeUnit;

public class dsClient {
    public static void main(String[] args) {
        while (true) {
            try {
                InetAddress aHost = InetAddress.getByName(args[0]);
                int aPort = Integer.parseInt(args[1]);
                Socket s = new Socket(aHost, aPort);
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                DataInputStream in = new DataInputStream(s.getInputStream());

                System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort());
                System.out.println("Local IP: " + s.getLocalAddress() + " Local Port: " + s.getLocalPort());

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

                out.writeUTF("HELO");
                System.out.println("SENT: HELO");

                String str = (String) in.readUTF();
                System.out.println("RCVD: " + str);

                out.writeUTF("BYE");
                System.out.println("SENT: BYE");

                str = (String) in.readUTF();
                System.out.println("RCVD: " + str);

                in.close();
                out.close();
                s.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}