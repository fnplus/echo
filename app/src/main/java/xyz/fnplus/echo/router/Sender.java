package xyz.fnplus.echo.router;

import java.util.concurrent.ConcurrentLinkedQueue;
import xyz.fnplus.echo.config.Configuration;
import xyz.fnplus.echo.router.tcp.TcpSender;

/**
 * Responsible for sending all packets that appear in the queue
 *
 * @author Matthew Vertescher
 */
public class Sender implements Runnable {

  /**
   * Queue for packets to send
   */
  private static ConcurrentLinkedQueue<Packet> ccl;

  /**
   * Constructor
   */
  public Sender() {
    if (ccl == null) ccl = new ConcurrentLinkedQueue<Packet>();
  }

  /**
   * Enqueue a packet to send
   */
  public static boolean queuePacket(Packet p) {
    if (ccl == null) ccl = new ConcurrentLinkedQueue<Packet>();
    return ccl.add(p);
  }

  @Override public void run() {
    TcpSender packetSender = new TcpSender();

    while (true) {
      //Sleep to give up CPU cycles
      while (ccl.isEmpty()) {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      Packet p = ccl.remove();
      String ip = MeshNetworkManager.getIPForClient(p.getMac());
      packetSender.sendPacket(ip, Configuration.RECEIVE_PORT, p);
    }
  }
}
