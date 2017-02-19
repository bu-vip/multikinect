package edu.bu.vip.multikinect.controller.webconsole;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPUtils {

  public static String getIP() throws SocketException {
    String localIP = "localhost";

    // TODO(doug) - Setting to prefer a specific interface
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    while (interfaces.hasMoreElements()) {
      NetworkInterface inter = interfaces.nextElement();
      Enumeration<InetAddress> addresses = inter.getInetAddresses();
      while (addresses.hasMoreElements()) {
        InetAddress add = addresses.nextElement();
        if (add.isSiteLocalAddress()) {
          localIP = add.getHostAddress();
        }
      }
    }

    return localIP;
  }
}
