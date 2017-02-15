package edu.bu.vip.kinect.controllerv2.webconsole;

import java.net.URI;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.client.HttpClient;

public class DevRedirectHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    String uri = ctx.getRequest().getRawUri();
    String localIp = IPUtils.getIP();
    ctx.get(HttpClient.class)
        .requestStream(new URI(String.format("http://%1$s:3000", localIp) + uri),
            spec -> spec.getHeaders().copy(ctx.getRequest().getHeaders()))
        .then(resp -> resp.forwardTo(ctx.getResponse()));
  }
}
