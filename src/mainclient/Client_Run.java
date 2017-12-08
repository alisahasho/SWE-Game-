package mainclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Client_Run {
  private int status = 0;
  private InetAddress server_ip;
  private int server_port = 9876;
  private DatagramSocket client_socket;
  private int id;
  private String name = "filippo";
  private UUID uuid;
  public static String[] client_map = { "XXXX", "AAAA", "BBBB", "CCCC" };

  public Client_Run() {
    try {
      server_ip = InetAddress.getByName("127.0.0.1");
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    while (true) {
      if (status < 1) {
        request_to_connect();
        response_to_connect();
        if (status == 1) {
          ping_send();
          ping_receive();
        }
      } else if (status == 3) {
        wait_command_play();
      } else {
        ping_send();
        ping_receive();
        wait_command_idle();
      }
    }
  }

  private void wait_command_play() {
    byte[] receiveData = new byte[512];
    DatagramPacket receive_packet = new DatagramPacket(receiveData, receiveData.length);
    try {
      client_socket.setSoTimeout(10000); // 10 seconds
      client_socket.receive(receive_packet);
      String command_type = command_parse(receive_packet);
      Map variables = parse_request_map(receive_packet);
      switch (command_type) {
      case "error":
        System.out.println(variables.get("message"));
        break;
      case "mapreq":
        send_map_response(variables);
        break;
      default:
        System.out.println("bad request");
      }
    } catch (SocketTimeoutException e1) {
      // nothing happened
      System.out.println("didn't receive anything in the wait");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void send_map_response(Map variables) {
    if (((String) variables.get("UUID")).equals(uuid.toString())) {
      int which_row = Integer.parseInt((String) variables.get("row"));
      String[] keys = { "UUID", "row", "data" };
      String[] values = { uuid.toString(), which_row + "", client_map[which_row] };
      byte[] map_response_byte = create_packet_string("mapakk", keys, values);
      send_packets(map_response_byte);
    } else {
      System.out.println("Someone strange is trying to connect");
    }
  }

  private void wait_command_idle() {
    byte[] receiveData = new byte[512];
    DatagramPacket receive_packet = new DatagramPacket(receiveData, receiveData.length);
    try {
      client_socket.setSoTimeout(10000); // 30 seconds
      client_socket.receive(receive_packet);
      String command_type = command_parse(receive_packet);
      Map variables = parse_request_map(receive_packet);
      switch (command_type) {
      case "error":
        System.out.println(variables.get("message"));
        break;
      case "playstart":
        check_play_status(variables);
        break;
      default:
        System.out.println("bad request");
      }
    } catch (SocketTimeoutException e1) {
      // nothing happened
      System.out.println("didn't receive anything in the wait");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void response_to_connect() {
    byte[] receiveData = new byte[512];
    DatagramPacket receive_packet = new DatagramPacket(receiveData, receiveData.length);
    try {
      client_socket.setSoTimeout(5000);
      client_socket.receive(receive_packet);
      String command_type = command_parse(receive_packet);
      Map variables = parse_request_map(receive_packet);
      switch (command_type) {
      case "error":
        System.out.println(variables.get("message"));
        break;
      case "accept":
        if (((String) variables.get("name")).equals(name)) {
          id = Integer.parseInt((String) variables.get("id"));
          uuid = UUID.fromString((String) variables.get("UUID"));
          status = Integer.parseInt((String) variables.get("status"));
        } else {
          System.out.println("Wrong username back, restart connect");
        }
        break;
      default:
        System.out.println("not error or accept");
      }
    } catch (SocketTimeoutException e1) {
      System.out.println("timed out");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void request_to_connect() {
    try {
      client_socket = new DatagramSocket(9875);
    } catch (SocketException e) {
    }
    String[] keys = { "name" };
    String[] values = { name };
    byte[] send_packet = create_packet_string("connect", keys, values);
    send_packets(send_packet);
  }

  private boolean send_packets(byte[] packet_content) {
    byte[] send_data = packet_content;
    DatagramPacket send_packet = new DatagramPacket(send_data, send_data.length, server_ip, server_port);
    try {
      client_socket.send(send_packet);
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  private void check_play_status(Map variables) {
    if (((String) variables.get("UUID")).equals(uuid.toString())) {
      status = Integer.parseInt((String) variables.get("status"));
      String[] keys = { "UUID", "status" };
      String[] values = { uuid.toString(), Integer.toString(status) };
      byte[] send_play_akk = create_packet_string("playakk", keys, values);
      send_packets(send_play_akk);
      status = 3;
      // start map making and the AI
    } else {
      System.out.println("someone strange wants to connect to you");
    }
  }

  private Map<String, String> parse_request_map(DatagramPacket incoming) {
    // block : as inputs
    String[] vars_dirty = new String(incoming.getData()).split(":");
    // String[] vars = Arrays.copyOfRange(vars_dirty, 1, vars_dirty.length);
    Map<String, String> map_vars = new HashMap<String, String>();
    for (int i = 1; i < (vars_dirty.length - 1); i++) {
      // block = as inputs
      String[] temp = vars_dirty[i].split("=");
      map_vars.put(temp[0], temp[1]);
      // System.out.println("Packet Content " + temp[1]);
    }
    return map_vars;
  }

  private String command_parse(DatagramPacket incoming) {
    String[] vars_dirty = new String(incoming.getData()).split(":");
    return vars_dirty[0];
  }

  private byte[] create_packet_string(String command_word, String[] keys, String[] values) {
    if (keys.length != values.length) {
      System.out.println("The the command: " + command_word + " the keys and values don't match");
      throw new ArrayIndexOutOfBoundsException();
    } else {
      String packet_string = command_word + ":";
      for (int j = 0; j < keys.length; j++) {
        packet_string += keys[j] + "=" + values[j] + ":";
      }
      System.out.println(packet_string);

      return packet_string.getBytes();
    }
  }

  private void ping_send() {
    try {
      String command_word = "ping";
      String[] keys = { "UUID", "status" };
      String[] values = { uuid.toString(), Integer.toString(status) };
      byte[] send_ping_data = create_packet_string(command_word, keys, values);
      send_packets(send_ping_data);
      System.out.println("sent ping");
    } catch (NullPointerException e) {
      System.out.println("bad ping");
    }

  }

  private void ping_receive() {
    byte[] receiveData = new byte[512];
    DatagramPacket receive_packet = new DatagramPacket(receiveData, receiveData.length);
    try {
      client_socket.setSoTimeout(5000); // 5 seconds
      client_socket.receive(receive_packet);
      String command_type = command_parse(receive_packet);
      Map variables = parse_request_map(receive_packet);
      switch (command_type) {
      case "error":
        System.out.println(variables.get("message"));
        break;
      case "pingback":
        status = Integer.parseInt((String) variables.get("status"));
        System.out.println("received ping");
        break;
      default:
        System.out.println("not error or ping");
      }
    } catch (SocketTimeoutException e1) {
      // nothing happened
      System.out.println("didn't receive anything back");
      status = 0;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Client_Run client = new Client_Run();
  }

}
