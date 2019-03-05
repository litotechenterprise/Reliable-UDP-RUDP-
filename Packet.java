// Imports
import java.io.Serializable;
import java.util.Arrays;

public class Packet implements Serializable {

public int seq;
	
	public byte[] data;
	
	public boolean last;
	// create new Packet Object
	public Packet(int seq, byte[] data, boolean last) {
		super();
		this.seq = seq;
		this.data = data;
		this.last = last;
	}
	// return Seq #
	public int getSeq() {
		return seq;
	}
	// Set Seq #
	public void setSeq(int seq) {
		this.seq = seq;
	}
	// return Data
	public byte[] getData() {
		return data;
	}
	// set Data
	public void setData(byte[] data) {
		this.data = data;
	}
	// returns last sequence number
	public boolean isLast() {
		return last;
	}
	// setting last to the value of the last seq number
	public void setLast(boolean last) {
		this.last = last;
	}
	
}
