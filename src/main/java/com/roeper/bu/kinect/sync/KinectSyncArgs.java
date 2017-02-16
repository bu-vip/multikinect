package edu.bu.vip.multikinect.sync;

import com.beust.jcommander.Parameter;

public class KinectSyncArgs {
  @Parameter(names = {"--file"}, required = true, description = "Input file path")
  private String filePath;
  
  @Parameter(names = "--help", help = true, description = "Displays usage info.")
  private boolean help = false;

  public String getFilePath() {
    return filePath;
  }

  public boolean isHelp() {
    return help;
  }
}
