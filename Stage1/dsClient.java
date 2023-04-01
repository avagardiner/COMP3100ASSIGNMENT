import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DsClient {
    private static final String HELO_MSG = "HELO";
    private static final String AUTH_MSG = "AUTH";
    private static final String REDY_MSG = "REDY";
    private static final String GETS_MSG = "GETS Avail";
    private static final String OK_MSG = "OK";
    private static final String SCHD_MSG = "SCHD";
    private static final String QUIT_MSG = "QUIT";
    private static final String NONE_MSG = "NONE";

    private static final int DEFAULT_SERVER_PORT = 50000;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public static void main(String[] args) {
        DsClient client = new DsClient();
        try {
            client.connect("127.0.0.1", DEFAULT_SERVER_PORT);
            client.handshake("username");
            client.dispatchJobs();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void connect(String hostname, int port) throws Exception {
        InetAddress addr = InetAddress.getByName(hostname);
        int p = port > 0 ? port : DEFAULT_SERVER_PORT;
        socket = new Socket(addr, p);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void handshake(String username) throws Exception {
        sendMessage(HELO_MSG);
        expectMessage(HELO_MSG);
        sendMessage(AUTH_MSG + " " + username);
        expectMessage(OK_MSG);
    }

    public void dispatchJobs() throws Exception {
        String msg = "";
        while (!msg.equals(NONE_MSG)) {
            sendMessage(REDY_MSG);
            msg = expectMessage();
            switch (msg) {
                case "JOBN":
                    handleJob();
                    break;
                case "JCPL":
                case NONE_MSG:
                    break;
                default:
                    throw new Exception("Unexpected message from server: " + msg);
            }
        }
        sendMessage(QUIT_MSG);
        expectMessage(QUIT_MSG);
    }

    private String getLargestServer(int numServerTypes) throws Exception {
        String largestServerType = "";
        int largestServerCoreCount = -1;
        for (int i = 0; i < numServerTypes; i++) {
            String serverData = expectMessage();
            String[] serverFields = serverData.split("\\s+");
            String serverType = serverFields[0];
            int serverCoreCount = Integer.parseInt(serverFields[4]);
            if (serverCoreCount > largestServerCoreCount) {
                largestServerType = serverType;
                largestServerCoreCount = serverCoreCount;
                largestServerIndex = i;
            }
        }
        sendMessage(OK_MSG);
        expectMessage(".");
        return largestServerType;
    }

    private void handleJob() throws Exception {
        String jobData = expectMessage();
        String[] jobFields = jobData.split("\\s+");
        int jobID = Integer.parseInt(jobFields[2]);
        sendMessage(GETS_MSG);
        String getsResponse = expectMessage();
        String[] serverDataFields = getsResponse.split("\\s+");
        int numServerTypes = Integer.parseInt(serverDataFields[1]);
        sendMessage(OK_MSG);
        String largestServerType = getLargestServer(numServerTypes);

        int largestServerCoreCount = Integer.parseInt(largestServerType.split("\\s+")[4]);

        String nextServerType = "";
        int largestServerIndex = 0;
        int nextServerIndex = -1;
        if (largestServerIndex == numServerTypes - 1) {
            nextServerType = expectMessage().split("\\s+")[0];
            nextServerIndex = 0;
        } else {
            nextServerType = expectMessage().split("\\s+")[0];
            nextServerIndex = largestServerIndex + 1;
        }

        while (!nextServerType.equals(".")) {
            String[] nextServerFields = nextServerType.split("\\s+");
            String nextType = nextServerFields[0];
            int nextCoreCount = Integer.parseInt(nextServerFields[4]);
            if (nextCoreCount == largestServerCoreCount) {
                sendMessage(SCHD_MSG + " " + jobID + " " + nextType + " 0");
                break;
            }
            if (nextServerIndex == numServerTypes - 1) {
                nextServerType = expectMessage().split("\\s+")[0];
                nextServerIndex = 0;
            } else {
                nextServerType = expectMessage().split("\\s+")[0];
                nextServerIndex++;
            }
        }
    }

    private void sendMessage(String message) throws Exception {
        out.writeUTF(message);
    }

    private String expectMessage() throws Exception {
        return in.readUTF().trim();
    }

    private void expectMessage(String expectedMessage) throws Exception {
        String message = expectMessage();
        if (!message.equals(expectedMessage)) {
            throw new Exception("Unexpected message from server: " + message);
        }
    }

    public void disconnect() throws Exception {
        if (socket != null) {
            socket.close();
        }
    }
}