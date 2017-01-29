package edu.bu.vip.kinect.controller.webconsole;

import java.net.URI;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.client.HttpClient;

public class DevRedirectHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    String uri = ctx.getRequest().getRawUri();
    ctx.get(HttpClient.class)
        .requestStream(new URI("http://localhost:3000" + uri),
            spec -> spec.getHeaders().copy(ctx.getRequest().getHeaders()))
        .then(resp -> resp.forwardTo(ctx.getResponse()));
  }
}
