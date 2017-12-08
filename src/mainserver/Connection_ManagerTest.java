package mainserver;

import static org.junit.jupiter.api.Assertions.*;

import java.net.DatagramPacket;

import org.junit.jupiter.api.Test;

class Connection_ManagerTest {
	
	Connection_Manager conn_manager = new Connection_Manager();

	/*
	@Test
	void testSend_packets() {
		fail("Not yet implemented");
	}

	@Test
	void testCreate_packet_bytes() {
		fail("Not yet implemented");
	}
*/
	@Test
	void testCommand_parse() {
		String string_test = "command:key=value:";
		byte[] byte_test = string_test.getBytes();
		DatagramPacket packet_test = new DatagramPacket(byte_test, byte_test.length);
		assertEquals("command", conn_manager.command_parse(packet_test));
	}

}
