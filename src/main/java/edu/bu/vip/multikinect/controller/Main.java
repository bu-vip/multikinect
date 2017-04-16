package edu.bu.vip.multikinect.controller;

import com.beust.jcommander.JCommander;
import edu.bu.vip.multikinect.controller.webconsole.WebConsole;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws Exception {
    // Parse command line args
    MainArgs mainArgs = new MainArgs();
    JCommander commander = new JCommander();
    commander.addObject(mainArgs);
    commander.parse(args);

    // Check if help
    if (mainArgs.isHelp()) {
      commander.usage();
    } else {
      Controller controller = new Controller(mainArgs.getDataDirectory());
      WebConsole webConsole = new WebConsole(controller, mainArgs.getDataDirectory());
      webConsole.setEnableDevRedirect(mainArgs.getWebDevRedirect());

      // Must start the controller before the web console
      controller.start();
      webConsole.start();

      System.out.println("Press enter to stop");
      Scanner scanner = new Scanner(System.in);
      scanner.nextLine();
      scanner.close();

      webConsole.stop();
      controller.stop();
    }
  }
}
