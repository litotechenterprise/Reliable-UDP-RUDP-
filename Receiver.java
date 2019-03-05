// Imports
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class Receiver {
	
	// Probability of ACK loss
		public static final double PROBABILITY = 0.1;
		public static void main(String[] args) throws Exception{
								// 13 & 14 is my favorite numbers
			DatagramSocket fromSender = new DatagramSocket(1314);
			
			// 83 is the base size
			byte[] receivedData = new byte[Sender.MSS + 83];
			
			int waitingFor = 0;
			
			// list that contains all the packets
			ArrayList<Packet> received = new ArrayList<Packet>();
			
			boolean end = false;
			
			while(!end){
				// When printed ready to receive packet
				System.out.println("Waiting for packet");
				
				// Receive packet
				DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
				fromSender.receive(receivedPacket);
				
				// Resize to a Packet object to get just the data in the packet
				Packet packet = (Packet) Resizer.toObject(receivedPacket.getData());
				
				System.out.println("Packet with sequence number " + packet.getSeq() + " received (last: " + packet.isLast() + " )");
			
				// check if it is the last pack 
				if(packet.getSeq() == waitingFor && packet.isLast()){
					
					waitingFor++;
					received.add(packet);
					
					System.out.println("Last packet received");
					
					end = true;
					
				}else if(packet.getSeq() == waitingFor){
					waitingFor++;
					received.add(packet);
					System.out.println("Packed stored in buffer");
				}else{
					System.out.println("Packet discarded (not in order)");
				}
				
				// Create an RDTAck object
				Ack ackObject = new Ack(waitingFor);
				
				// Serialize
				byte[] ackBytes = Resizer.toBytes(ackObject);
				
				
				DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, receivedPacket.getAddress(), receivedPacket.getPort());
				
				// probability of loss
				if(Math.random() > PROBABILITY){
					fromSender.send(ackPacket);
				}else{
					System.out.println("!!! Lost ack with sequence number " + ackObject.getPacket() + " !!!");
				}
				
				System.out.println("Sending ACK to seq " + waitingFor);
				

			}
			
			// Print the data received
			System.out.println(" <------- Data Received from Sender ------->");
			// Prints out data from received ArrayList that was sent from Sender
			for(Packet p : received){
				for(byte b: p.getData()){
					System.out.print((char) b);
				}
			}
			
		}
	

}
