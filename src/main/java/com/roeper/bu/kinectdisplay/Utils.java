package edu.bu.vip.multikinectdisplay;

public class Utils {

  public static double[] rowMajorToColumnMajor(double[] rowMajor) {
    double[] columnMajor = new double[16];
      for (int j = 0; j < 4; j++) {
        columnMajor[j * 4 + 0] = rowMajor[j];
        columnMajor[j * 4 + 1] = rowMajor[4 + j];
        columnMajor[j * 4 + 2] = rowMajor[8 + j];
        columnMajor[j * 4 + 3] = rowMajor[12 + j];
      }
      return columnMajor;
  }
}
