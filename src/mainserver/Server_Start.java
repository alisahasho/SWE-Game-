package mainserver;

import javax.swing.JFrame;

public class Server_Start extends Thread {

  public static void main(String[] args) {
    CDS.conn = new Connection_Manager();
    Server_Start server = new Server_Start();
  }

  public Server_Start() {
    JFrame frame = new JFrame();
    CDS.GUI_editor = new Main_Panel();
    frame.add(CDS.GUI_editor);
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(300, 300);
    this.start();
  }

  public void run() {
    int randomizer = 5;
    int count = 0;
    while (true) {
      // System.out.println("i am in here");
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      CDS.GUI_editor.set_status(0, CDS.clients_status[0]);
      CDS.GUI_editor.set_status(1, CDS.clients_status[1]);
      long temp = System.currentTimeMillis();
      if (CDS.clients_status[0] > 0) {
        CDS.GUI_editor.set_last_ping(0, (int) (temp - CDS.last_ping_clients[0]));
      }
      if (CDS.clients_status[1] > 0) {
        CDS.GUI_editor.set_last_ping(1, (int) (temp - CDS.last_ping_clients[1]));
      }
      if (randomizer == count) {
        randomizer = (int) (Math.random() * 7 + 2);
        count = -1;
        temp = System.currentTimeMillis();
        if (((temp - CDS.last_ping_clients[0]) > 30000) && CDS.clients_status[0] > 0) {
          System.out.println("client 0 has been disconnected for more then 30 seconds");
        }
        if (((temp - CDS.last_ping_clients[1]) > 30000) && CDS.clients_status[1] > 0) {
          System.out.println("client 1 has been disconnected for more then 30 seconds");
        }
      }
      count++;
    }
  }
}
