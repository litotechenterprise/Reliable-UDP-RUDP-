// Imports
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;

public class Sender {
	// Maximum Segment Size
		public static final int MSS = 4;

		// Probability of package being lost
		public static final double PROBABILITY = 0.1;

		// Window size
		public static final int WINDOW_SIZE = 2;
		
		// Time in milliseconds before re-sending Packer
		public static final int TIMER = 30;
		
		// data that is going to be sent to the Receiver
		public static final String Data2BSent =  "Computer Science Majors Rule!";


		public static void main(String[] args) throws Exception{

			// Sequence number of the last packet sent
			int lastSent = 0;
			
			// Sequence number of the last ACK packet
			int waitingForAck = 0;

			
			byte[] fileBytes = Data2BSent.getBytes();

			System.out.println("Data size: " + fileBytes.length + " bytes");

			// Last packet sequence number
			int lastSeq = (int) Math.ceil( (double) fileBytes.length / MSS);

			System.out.println("Number of packets to send: " + lastSeq);

			DatagramSocket toReceiver = new DatagramSocket();

			// Receiver address
			InetAddress receiverAddress = InetAddress.getByName("localhost");
			
			// List of all the packets sent
			ArrayList<Packet> sent = new ArrayList<Packet>();

			while(true){

				// Sending loop
				while(lastSent - waitingForAck < WINDOW_SIZE && lastSent < lastSeq){

					// Array to store part of the bytes to send
					byte[] filePacketBytes = new byte[MSS];

					// Copy data bytes to array
					filePacketBytes = Arrays.copyOfRange(fileBytes, lastSent*MSS, lastSent*MSS + MSS);

					// Create Packet object
					Packet PacketObject = new Packet(lastSent, filePacketBytes, (lastSent == lastSeq-1) ? true : false);

					// Resize the Packet object
					byte[] sendData = Resizer.toBytes(PacketObject);

					// Create the packet
					DatagramPacket packet = new DatagramPacket(sendData, sendData.length, receiverAddress, 1314 );
					
					System.out.println("Sending packet with sequence number " + lastSent);

					// Add packet to the sent list
					sent.add(PacketObject);
					
					// Send with some probability of loss
					if(Math.random() > PROBABILITY){
						toReceiver.send(packet);
					}else{
						System.out.println("!!! Lost packet with sequence number " + lastSent + " !!!");
					}

					// Increase the last sent
					lastSent++;

				} // End of sending while
				
				// Byte array for the ACK sent by the receiver
				byte[] ackBytes = new byte[40];
				
				// Creating packet for the ACK
				DatagramPacket ack = new DatagramPacket(ackBytes, ackBytes.length);
				
				try{
					// Setting a limit of how long to wait for ACK
					toReceiver.setSoTimeout(TIMER);
					
					// Receive the packet
					toReceiver.receive(ack);
					
					// resize the ACK object
					Ack ackObject = (Ack) Resizer.toObject(ack.getData());
					// print out that we have received ACK
					System.out.println("Received ACK for " + ackObject.getPacket());
					
					// Checks if it is equal to the last sequence #
					if(ackObject.getPacket() == lastSeq){
					// if so that mean it's the last packet
						break;
					}
					
					waitingForAck = Math.max(waitingForAck, ackObject.getPacket());
					// this is triggered when TIMER expires
				}catch(SocketTimeoutException e){
					// then send all the sent but non-acked packets
					for(int i = waitingForAck; i < lastSent; i++){
						
						// Resize the Packet object
						byte[] sendData = Resizer.toBytes(sent.get(i));

						// Create the packet
						DatagramPacket packet = new DatagramPacket(sendData, sendData.length, receiverAddress, 1314 );
						
						// Send with some probability
						if(Math.random() > PROBABILITY){
							toReceiver.send(packet);
						}else{
							System.out.println("!!! Lost packet with sequence number " + sent.get(i).getSeq() + "  !!!");
						}

						System.out.println("REsending packet with sequence number " + sent.get(i).getSeq());
					}
				}
				
			
			}
			// When printed mean all ACK have been received 
			System.out.println("Finished transmission");

		}
}
