package com.roeper.bu.kinectdisplay;

import com.beust.jcommander.Parameter;

public class KinectDisplayArgs {
  @Parameter(names = "--help", help = true, description = "Displays usage info.")
  private boolean help = false;

  public boolean isHelp() {
    return help;
  }
}
