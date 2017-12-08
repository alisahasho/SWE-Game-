package mainserver;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main_Panel extends JPanel {
  private JLabel client1_status_label;
  private JLabel client2_status_label;
  private JLabel client1_last_connect;
  private JLabel client2_last_connect;
  private int client1_status, client2_status;
  private String[] client_status_list = { "disconnected", "connecting", "connected", "playing" };
  Color[] client_status_colors = { Color.GRAY, Color.BLUE, Color.GREEN, Color.RED };
  private JPanel inner_panel;

  public Main_Panel() {
    inner_panel = new JPanel();
    inner_panel.setLayout(new BorderLayout());

    JPanel client1_panel = new JPanel();
    JLabel client1 = new JLabel("Client 1");
    client1_status_label = new JLabel();
    client1_last_connect = new JLabel("0");
    client1_panel.add(client1);
    client1_panel.add(client1_status_label);
    client1_panel.add(client1_last_connect);

    JPanel client2_panel = new JPanel();
    JLabel client2 = new JLabel("Client 2");
    client2_status_label = new JLabel();
    client2_last_connect = new JLabel("0");
    client2_panel.add(client2);
    client2_panel.add(client2_status_label);
    client2_panel.add(client2_last_connect);

    JButton start_button = new JButton("Start");
    start_button.addActionListener(test());

    inner_panel.add(client1_panel, BorderLayout.NORTH);
    inner_panel.add(client2_panel, BorderLayout.WEST);
    inner_panel.add(start_button, BorderLayout.SOUTH);
    inner_panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    add(inner_panel);
  }

  public boolean set_status(int client_num, int status) {
    // System.out.println("Setting something " + status);
    String action = client_status_list[status];
    Color bg = client_status_colors[status];
    if (client_num == 0) {
      client1_status = status;
      client1_status_label.setText(action);
      client1_status_label.setForeground(bg);
    } else if (client_num == 1) {
      client2_status = status;
      client2_status_label.setText(action);
      client2_status_label.setForeground(bg);
    } else {
      return false;
    }
    inner_panel.repaint();
    inner_panel.revalidate();
    return true;
  }

  public void set_last_ping(int client_num, int seconds) {
    if (client_num == 0) {
      client1_last_connect.setText(seconds + " ms");
    } else if (client_num == 1) {
      client2_last_connect.setText(seconds + " ms");
    }
    inner_panel.repaint();
    inner_panel.revalidate();
  }

  public ActionListener test() {
    return new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // if (CDS.clients_status[0] == 2 && CDS.clients_status[1] == 2) {
        CDS.conn.start_game();
        // } else {
        // Log.log(0, "There aren't 2 connected users");
        // }
        // String[] keys = { "message" };
        // String[] values = { "I love pie, i have love" };
        // CDS.conn.send_packets(CDS.conn.create_packet_bytes("error", keys,
        // values), CDS.conn.clients_IPs[0]);
      }
    };
  }
}
