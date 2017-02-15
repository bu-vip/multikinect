package edu.bu.vip.kinect.controllerv2.webconsole;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import ratpack.handling.Context;
import ratpack.handling.Handler;

public class IPHandler implements Handler {

  public static final String URL_PATH = "_/ip.js";

  private static final String IP_VAR = "const IP = { HOST: \"%1$s:%2$d\" };";

  @Override
  public void handle(Context context) throws Exception {
    // TODO(doug) - Set port
    int port = 8080;

    String localIP = IPUtils.getIP();

    context.render(String.format(IP_VAR, localIP, port));
  }
}
