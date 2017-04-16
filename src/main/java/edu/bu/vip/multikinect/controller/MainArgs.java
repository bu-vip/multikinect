package edu.bu.vip.multikinect.controller;

import com.beust.jcommander.Parameter;

public class MainArgs {
  @Parameter(names = {"--data_dir"}, description = "Directory for storing data", required = true)
  private String dataDirectory;

  @Parameter(names = {"--web_dev"}, description = "Development flag")
  private boolean webDevRedirect = false;

  @Parameter(names = {"--help", "-h"}, help = true, description = "Displays this help message")
  private boolean help;

  public String getDataDirectory() {
    return dataDirectory;
  }

  public boolean getWebDevRedirect() {
    return webDevRedirect;
  }

  public boolean isHelp() {
    return help;
  }

}
