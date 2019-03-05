// Imports
import java.io.Serializable;

public class Ack implements Serializable {
	private int packet;
	// creates  ACK Packet
	public Ack(int packet) {
		super();
		this.packet = packet;
	}
	// returns Ack Packet
	public int getPacket() {
		return packet;
	}
	// sets Ack 
	public void setPacket(int packet) {
		this.packet = packet;
	}
	
}
