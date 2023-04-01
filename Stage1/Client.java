import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("127.0.0.1", 50000);

            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort());
            System.out.println("Local IP: " + s.getLocalAddress() + " Local Port: " + s.getLocalPort());

            // Step 1: Send HELO to server
            out.writeUTF("HELO\n");
            out.flush();
            System.out.println("SENT: HELO");

            // Step 2: Wait for OK response from server
            String str = in.readLine();
            if (str.startsWith("OK")) {
                System.out.println("RCVD: " + str);
            } else {
                throw new Exception("Invalid response from server: " + str);
            }

            // Step 3: Send AUTH with authentication information to server
            out.writeUTF("AUTH Ava\n");
            out.flush();
            System.out.println("SENT: AUTH Ava");

            // Step 4: Wait for OK response from server
            str = in.readLine();
            if (str.startsWith("OK")) {
                System.out.println("RCVD: " + str);
            } else {
                throw new Exception("Invalid response from server: " + str);
            }

            // Step 5: Send REDY to server
            out.writeUTF("REDY\n");
            out.flush();
            System.out.println("SENT: REDY");

            // Step 6: Wait for JOBN, JOBP, JCPL, RESF, RESR, CHKQ, or NONE message from
            // server
            str = in.readLine();
            System.out.println("RCVD: " + str);

            // Step 7: Send a message to gracefully terminate the simulation
            out.writeUTF("QUIT\n");
            out.flush();
            System.out.println("SENT: QUIT");

            // Step 8: Wait for the server to finish processing remaining jobs and terminate
            str = in.readLine();
            System.out.println("RCVD: " + str);

            in.close();
            out.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }}

    

    
    

    

    

    
    

    

    

    

    

    

    

    

    

    
    
    
    
    
    

    

    
    
    
    
    
        
        
    

    

    

    

    

    
    
        
    
