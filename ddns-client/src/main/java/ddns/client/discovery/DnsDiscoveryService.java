package ddns.client.discovery;

import ddns.client.domain.IP;
import ddns.client.domain.dns.DnsRecordType;
import ddns.client.domain.dns.DnsServerInfo;
import ddns.client.exception.DiscoveryException;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class DnsDiscoveryService {

    private final Random random = new Random();

    public IP discover(DnsServerInfo dnsServerInfo, int socketTimeout) throws DiscoveryException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(socketTimeout);

            InetAddress serverAddress = InetAddress.getByName(dnsServerInfo.getDnsResolverName());
            socket.connect(serverAddress, dnsServerInfo.getPort());
            DnsRequest dnsQuery = buildDNSQuery(dnsServerInfo.getDnsRecordType(), dnsServerInfo.getDomainName());

            DatagramPacket requestPacket = new DatagramPacket(dnsQuery.payload, dnsQuery.payload.length);
            socket.send(requestPacket);

            byte[] response = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(response, response.length);
            socket.receive(responsePacket);

            String publicIp = parseDNSResponse(responsePacket.getData(), responsePacket.getLength(), dnsQuery.transactionId, dnsServerInfo.getDnsRecordType());
            return new IP(publicIp);
        } catch (Exception e) {
            throw new DiscoveryException(e.getMessage());
        }
    }

    private DnsRequest buildDNSQuery(DnsRecordType dnsRecordType, String domainName) {
        ByteBuffer buffer = ByteBuffer.allocate(128);

        byte[] transactionId = new byte[2];
        random.nextBytes(transactionId);
        buffer.put(transactionId); // Transaction ID - 2 random bytes
        buffer.putShort((short) 0x0100); // Flags (Standard Query)
        buffer.putShort((short) 0x0001); // Questions
        buffer.putShort((short) 0x0000); // Answer RRs
        buffer.putShort((short) 0x0000); // Authority RRs
        buffer.putShort((short) 0x0000); // Additional RRs

        String[] labels = domainName.split("\\.");
        for (String label : labels) {
            buffer.put((byte) label.length());
            buffer.put(label.getBytes());
        }
        buffer.put((byte) 0x00); // End of domain name

        short recordTypeCode = switch (dnsRecordType) {
            case A -> (short) 0x0001;
            case AAAA -> (short) 0x001C;
            case TXT -> (short) 0x0010;
        };

        buffer.putShort(recordTypeCode); // Type
        buffer.putShort((short) 0x0001); // Class IN

        byte[] query = new byte[buffer.position()];
        buffer.flip();
        buffer.get(query);

        return new DnsRequest(query, transactionId);
    }

    private String parseDNSResponse(byte[] response, int length, byte[] transactionId, DnsRecordType dnsRecordType) throws DiscoveryException, UnknownHostException {
        ByteBuffer buffer = ByteBuffer.wrap(response, 0, length);
        byte[] receivedTransactionId = new byte[2];
        buffer.get(receivedTransactionId); // Transaction ID
        buffer.getShort(); // Flags
        int questions = buffer.getShort(); // Questions
        int answers = buffer.getShort(); // Answer RRs
        buffer.getShort(); // Authority RRs
        buffer.getShort(); // Additional RRs

        if (!Arrays.equals(transactionId, receivedTransactionId)) {
            throw new DiscoveryException("Transaction ID mismatch!");
        }

        for (int i = 0; i < questions; i++) {
            skipName(buffer);
            buffer.getShort(); // Type
            buffer.getShort(); // Class
        }

        for (int i = 0; i < answers; i++) {
            skipName(buffer);
            int type = buffer.getShort() & 0xFFFF;
            buffer.getShort(); // Class
            buffer.getInt(); // TTL
            int dataLength = buffer.getShort() & 0xFFFF;

            switch (dnsRecordType) {
                case A -> {
                    if (type == 0x0001 && dataLength == 4) {
                        byte[] ipBytes = new byte[4];
                        buffer.get(ipBytes);
                        return InetAddress.getByAddress(ipBytes).getHostAddress();
                    }
                }
                case AAAA -> {
                    if (type == 0x001C && dataLength == 16) {
                        byte[] ipBytes = new byte[16];
                        buffer.get(ipBytes);
                        return InetAddress.getByAddress(ipBytes).getHostAddress();
                    }
                }
                case TXT -> {
                    if (type == 0x0010 && dataLength > 0) {
                        int txtEnd = buffer.position() + dataLength;
                        StringBuilder sb = new StringBuilder();
                        while (buffer.position() < txtEnd) {
                            int txtLen = buffer.get() & 0xFF;
                            byte[] txtData = new byte[txtLen];
                            buffer.get(txtData);
                            sb.append(new String(txtData)); // Assuming UTF-8 or ASCII
                        }
                        return sb.toString();
                    }
                }
            }

            buffer.position(buffer.position() + dataLength);
        }

        throw new DiscoveryException("DNS record not found!");
    }

    private void skipName(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            int length = buffer.get() & 0xFF;
            if (length == 0) break;
            if ((length & 0xC0) == 0xC0) {
                buffer.get(); // Skip second byte of compressed name
                break;
            } else {
                buffer.position(buffer.position() + length);
            }
        }
    }

    public static class DnsRequest {
        byte[] payload;
        byte[] transactionId;

        DnsRequest(byte[] payload, byte[] transactionId) {
            this.payload = payload;
            this.transactionId = transactionId;
        }
    }
}
