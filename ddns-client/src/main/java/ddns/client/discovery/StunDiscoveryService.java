package ddns.client.discovery;

import ddns.client.domain.IP;
import ddns.client.domain.IPVersion;
import ddns.client.exception.DiscoveryException;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class StunDiscoveryService {

    private final Random random = new Random();

    public IP discover(IPVersion ipVersion, String host, int port) throws DiscoveryException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(1000);

            InetAddress address = InetAddress.getByName(host);
            socket.connect(address, port);
            byte[] request = buildStunRequest();

            DatagramPacket packet = new DatagramPacket(request, request.length);
            socket.send(packet);

            // Receive STUN response
            byte[] response = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(response, response.length);
            socket.receive(responsePacket);

            String publicIp = parseStunResponse(responsePacket.getData(), responsePacket.getLength());
            System.out.println("STUN public IP: " + publicIp);

            return new IP(publicIp);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw new DiscoveryException(e.getMessage());
        }
    }

    private byte[] buildStunRequest() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.order(ByteOrder.BIG_ENDIAN);

        // STUN Binding Request type 0x0001
        buffer.putShort((short) 0x0001);
        buffer.putShort((short) 0x0000); // Attributes length
        buffer.putInt(0x2112A442); // Magic Cookie

        // Unique Transaction ID - 12 random byte
        byte[] transactionId = new byte[12];
        random.nextBytes(transactionId);
        buffer.put(transactionId);

        return buffer.array();
    }

    // Parse XOR-MAPPED-ADDRESS from STUN response
    private String parseStunResponse(byte[] response, int length) throws UnknownHostException {
        ByteBuffer buffer = ByteBuffer.wrap(response, 0, length);
        buffer.order(ByteOrder.BIG_ENDIAN);

        // Check Magic Cookie
        if (buffer.getInt(4) != 0x2112A442) {
            throw new RuntimeException("Invalid STUN response");
        }

        // Find attributes
        int offset = 20; // STUN Header - 20 bytes
        while (offset < response.length) {
            short attrType = buffer.getShort(offset);
            short attrLength = buffer.getShort(offset + 2);

            if (attrType == 0x0020) { // XOR-MAPPED-ADDRESS
                byte family = buffer.get(offset + 5); // 0x01 = IPv4, 0x02 = IPv6
                byte[] ipBytes;

                // Cast signed Short в Int and do XOR with Magic Cookie
                int port = (buffer.getShort(offset + 6) & 0xFFFF) ^ 0x2112;

                if (family == 0x01) { // IPv4
                    ipBytes = new byte[]{
                            (byte) (buffer.get(offset + 8) ^ 0x21),
                            (byte) (buffer.get(offset + 9) ^ 0x12),
                            (byte) (buffer.get(offset + 10) ^ 0xA4),
                            (byte) (buffer.get(offset + 11) ^ 0x42)
                    };
                    return InetAddress.getByAddress(ipBytes).getHostAddress() + ":" + port;
                }
            }
            offset += 4 + attrLength; // Go to next attribute
        }
        throw new RuntimeException("XOR-MAPPED-ADDRESS not found!");
    }
}
