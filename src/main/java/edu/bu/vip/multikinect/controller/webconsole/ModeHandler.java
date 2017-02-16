package edu.bu.vip.multikinect.controller.webconsole;

import static ratpack.jackson.Jackson.jsonNode;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.bu.vip.multikinect.controller.Controller;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class ModeHandler implements Handler {
  public static final String URL_PATH = "_/mode";

  public static final String KEY_MODE = "mode";

  private final Controller controller;

  @Inject
  protected ModeHandler(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void handle(Context ctx) throws Exception {
    ctx.render(ctx.parse(jsonNode()).map((node) -> {
      // Check if the JSON has the mode key
      if (node.has(KEY_MODE)) {
        String modeString = (String) node.get(KEY_MODE).asText();
        Controller.Mode mode = Utils.enumIgnoreCase(Controller.Mode.class, modeString);
        controller.setMode(mode);
      } else {
        ctx.clientError(400);
      }
      
      return "{}";
    }));
  }
}
