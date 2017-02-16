package edu.bu.vip.multikinect.sync;

import org.ejml.data.DenseMatrix64F;

import com.google.common.collect.ImmutableList;

public class KinectSync {
  public static void main(String[] args) throws Exception {
    /*
     * KinectReaderArgs config = new KinectReaderArgs(); JCommander jCommander = new
     * JCommander(config, args); jCommander.setProgramName("KinectReader"); if (config.isHelp()) {
     * jCommander.usage(); return; }
     */

    ImmutableList<String> kinect1Files = ImmutableList.of(
        "/home/doug/Desktop/kinectdata/9-24/data-window/take-2016-09-24_20-42-11-227.dat",
        "/home/doug/Desktop/kinectdata/9-24/data-window/take-2016-09-24_20-42-47-131.dat",
        "/home/doug/Desktop/kinectdata/9-24/data-window/take-2016-09-24_20-43-44-667.dat",
        "/home/doug/Desktop/kinectdata/9-24/data-window/take-2016-09-24_20-43-12-987.dat",
        "/home/doug/Desktop/kinectdata/9-30/data-window/take-2016-09-30_12-50-42-573.dat",
        "/home/doug/Desktop/kinectdata/9-30/data-window/take-2016-09-30_12-51-05-052.dat",
        "/home/doug/Desktop/kinectdata/9-30/data-window/take-2016-09-30_12-51-47-779.dat",
        "/home/doug/Desktop/kinectdata/9-30/data-window/take-2016-09-30_12-52-23-571.dat");
    ImmutableList<String> kinect2Files = ImmutableList.of(
        "/home/doug/Desktop/kinectdata/9-24/data-corner/take-2016-09-24_20-42-07-628.dat",
        "/home/doug/Desktop/kinectdata/9-24/data-corner/take-2016-09-24_20-42-46-363.dat",
        "/home/doug/Desktop/kinectdata/9-24/data-corner/take-2016-09-24_20-43-12-427.dat",
        "/home/doug/Desktop/kinectdata/9-24/data-corner/take-2016-09-24_20-43-43-972.dat",
        "/home/doug/Desktop/kinectdata/9-30/data-corner/take-2016-09-30_12-50-37-750.dat",
        "/home/doug/Desktop/kinectdata/9-30/data-corner/take-2016-09-30_12-50-59-261.dat",
        "/home/doug/Desktop/kinectdata/9-30/data-corner/take-2016-09-30_12-51-42-293.dat",
        "/home/doug/Desktop/kinectdata/9-30/data-corner/take-2016-09-30_12-52-18-109.dat");

    //DenseMatrix64F transform = CalibrationRep.calculateTransform(kinect1Files, kinect2Files);
    DenseMatrix64F transform = new DenseMatrix64F(4, 4, true,  0.621,0.194,0.759,-2.524  ,
        -0.123,0.981, -0.150,0.471  ,
        -0.774,-0.000,0.633,3.280  ,
         0.000,0.000,0.000,1.000);
    System.out.println(transform.toString());

    int offset = 120;
    String fileA =
        "/home/doug/Desktop/kinectdata/9-30/data-window/take-2016-09-30_17-47-36-795.dat";
    String fileB =
        "/home/doug/Desktop/kinectdata/9-30/data-corner/take-2016-09-30_17-47-34-521.dat";
    double testError = CoordinateTransform.calculateError(fileA, fileB, transform, offset);
    System.out.println("Test error: " + testError);
  }
}
