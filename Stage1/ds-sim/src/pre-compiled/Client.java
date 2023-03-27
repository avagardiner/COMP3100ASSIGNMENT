import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import pre-compiled.Server;

public class Client {
    private static final int DEFAULT_PORT = 50000;
    private static final String DEFAULT_HOSTNAME = "localhost";
    private static final String QUIT_MESSAGE = "QUIT\n";
    private static final String JOBS_PREFIX = "JOBS ";
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Server[] servers;
    private int serverIndex;

    public Client(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void run() throws IOException, ParserConfigurationException, SAXException {
        // Send HELLO message to server
        out.write("HELO Client\n");
        out.flush();

        // Read response from server
        String response = in.readLine();
        if (!response.startsWith("OK")) {
            throw new IOException("Failed to connect to server: " + response);
        }

        // Send AUTH message to server
        out.write("AUTH dsclient\n");
        out.flush();

        // Read response from server
        response = in.readLine();
        if (!response.startsWith("OK")) {
            throw new IOException("Authentication failed: " + response);
        }

        // Send REDY message to server
        out.write("REDY\n");
        out.flush();

        // Read response from server
        while (true) {
            response = in.readLine();
            if (response.startsWith(JOBS_PREFIX)) {
                // Extract job information
                String[] jobInfo = response.substring(JOBS_PREFIX.length()).split("\\s+");
                int jobID = Integer.parseInt(jobInfo[0]);
                int estRunTime = Integer.parseInt(jobInfo[1]);
                int numCPUs = Integer.parseInt(jobInfo[4]);

                // Dispatch job using LRR
                String schedulingDecision = dispatchJob(jobID, estRunTime, numCPUs);

                // Send scheduling decision to server
                out.write(schedulingDecision + "\n");
                out.flush();
            } else if (response.equals(".")) {
                // All jobs have been processed
                out.write("QUIT\n");
out.flush();
break;
} else {
throw new IOException("Unexpected response from server: " + response);
}
}
// Read response from server
    response = in.readLine();
    if (!response.equals("QUIT")) {
        throw new IOException("Unexpected response from server: " + response);
    }

    // Send OK message to server
    out.write("OK\n");
    out.flush();

    // Close socket and streams
    socket.close();
    in.close();
    out.close();
}

private String dispatchJob(int jobID, int estRunTime, int numCPUs) throws ParserConfigurationException, SAXException, IOException {
    // Retrieve server list from server
    out.write("GETS Avail " + numCPUs + " " + estRunTime + " " + jobID + "\n");
    out.flush();

    // Read response from server
    String response = in.readLine();
    if (!response.startsWith("OK")) {
        throw new IOException("Failed to retrieve server list: " + response);
    }

    // Parse server list
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new InputSource(new StringReader(response.substring(3))));

    NodeList nodeList = doc.getElementsByTagName("server");
    servers = new Server[nodeList.getLength()];
    for (int i = 0; i < nodeList.getLength(); i++) {
        Element element = (Element) nodeList.item(i);
        int serverID = Integer.parseInt(element.getElementsByTagName("server_id").item(0).getTextContent());
        int availableCPUs = Integer.parseInt(element.getElementsByTagName("available_cpus").item(0).getTextContent());
        int availableJobs = Integer.parseInt(element.getElementsByTagName("available_jobs").item(0).getTextContent());
        int serverLimit = Integer.parseInt(element.getElementsByTagName("server_limit").item(0).getTextContent());
        int serverLoad = Integer.parseInt(element.getElementsByTagName("server_load").item(0).getTextContent());

        servers[i] = new Server(serverID, availableCPUs, availableJobs, serverLimit, serverLoad);
    }

    // Determine best server based on LRR algorithm
    int bestServerIndex = 0;
    for (int i = 1; i < servers.length; i++) {
        if (servers[i].getAvailableCPUs() > servers[bestServerIndex].getAvailableCPUs()) {
            bestServerIndex = i;
        } else if (servers[i].getAvailableCPUs() == servers[bestServerIndex].getAvailableCPUs()) {
            if (servers[i].getServerLoad() < servers[bestServerIndex].getServerLoad()) {
                bestServerIndex = i;
            }
        }
    }

    // Set current server index
    serverIndex = bestServerIndex;

    // Return scheduling decision
    return "SCHD " + jobID + " " + servers[bestServerIndex].getServerType() + " " + servers[bestServerIndex].getServerID();
}

public static void main(String[] args) {
    try {
        String hostname = args.length > 0 ? args[0] : DEFAULT_HOSTNAME;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        Client client = new Client(hostname, port);
        client.run();
    } catch (IOException | ParserConfigurationException | SAXException e) {
        System.err.println("Error: " + e.getMessage());
    }
}
}

