import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DsClient {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 50000;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Send HELO and receive OK
        out.println("HELO");
        in.readLine();

        // Send AUTH and receive OK
        out.println("AUTH Ava Gardiner");
        in.readLine();

        // Main loop
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.equals("NONE")) {
                break;
            }
            if (inputLine.startsWith("JOBN")) {
                // Parse job information
                String[] tokens = inputLine.split("\\s+");
                int jobId = Integer.parseInt(tokens[1]);
                int submitTime = Integer.parseInt(tokens[2]);
                int estRuntime = Integer.parseInt(tokens[3]);
                int cores = Integer.parseInt(tokens[4]);
                int memory = Integer.parseInt(tokens[5]);
                int disk = Integer.parseInt(tokens[6]);

                // Determine largest server type
                out.println("GETS All");
                inputLine = in.readLine();
                String largestServerType = "";
                int largestServerCores = 0;
                while (inputLine.startsWith("DATA")) {
                    String[] records = inputLine.split("\\s+");
                    int numRecords = Integer.parseInt(records[1]);
                    int recordSize = Integer.parseInt(records[2]);
                    for (int i = 0; i < numRecords; i++) {
                        inputLine = in.readLine();
                        records = inputLine.split("\\s+");
                        String serverType = records[0];
                        int serverCores = Integer.parseInt(records[4]);
                        if (serverCores > largestServerCores) {
                            largestServerType = serverType;
                            largestServerCores = serverCores;
                        }
                    }
                    out.println("OK");
                    inputLine = in.readLine();
                }

                // Schedule job on largest server type
                out.println("SCHD " + jobId + " " + largestServerType + " 0");
            } else {
                out.println("REDY");
            }
        }

        // Send QUIT and receive QUIT
        out.println("QUIT");
        in.readLine();

        // Close socket
        socket.close();
    }

}