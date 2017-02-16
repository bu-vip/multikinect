package edu.bu.vip.kinect.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ejml.data.DenseMatrix64F;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import edu.bu.vip.multikinect.Protos.Frame;

import edu.bu.vip.kinect.sync.CoordinateTransform.Transform;

public class Calibration {

  public static DenseMatrix64F calculateTransform(ImmutableList<String> filesA,
      ImmutableList<String> filesB) throws IOException {

    // Holds the sets of data points to calculate the transform for
    List<double[]> dataXList = new ArrayList<>();
    List<double[]> dataYList = new ArrayList<>();

    // Iterate through all file sets
    for (int i = 0; i < filesA.size(); i++) {
      String fileA = filesA.get(i);
      String fileB = filesB.get(i);

      // Calculate the best offset of the files
      final int globalOffset = calculateOffset(fileA, fileB);

      // Extract the sets of data points
      List<double[]> dataX = new ArrayList<>();
      List<double[]> dataY = new ArrayList<>();
      extractData(fileA, fileB, globalOffset, dataX, dataY);
      dataXList.addAll(dataX);
      dataYList.addAll(dataY);

      double[] testX = concatList(dataX);
      double[] testY = concatList(dataY);
      DenseMatrix64F matX = new DenseMatrix64F(testX.length / 3, 3, true, testX);
      DenseMatrix64F matY = new DenseMatrix64F(testY.length / 3, 3, true, testY);
      Transform transform = CoordinateTransform.calculateTransform(matX, matY);
      
      System.out.println("i:" + i + " err: " + transform.getError());
    }

    // Concatenate all data point sets into a single array
    double[] allDataX = concatList(dataXList);
    double[] allDataY = concatList(dataYList);

    // Create matrices
    DenseMatrix64F matX = new DenseMatrix64F(allDataX.length / 3, 3, true, allDataX);
    DenseMatrix64F matY = new DenseMatrix64F(allDataY.length / 3, 3, true, allDataY);

    // Calculate transform
    Transform transform = CoordinateTransform.calculateTransform(matX, matY);
    System.out.println("all err: " + transform.getError());
    return transform.getTransform();
  }

  private static int calculateOffset(String fileA, String fileB) throws IOException {
    FrameReader readerA = new FrameReader(fileA);
    FrameReader readerB = new FrameReader(fileB);

    List<OffsetError> offsets = new ArrayList<>();
    int currentFrameAIndex = 0;
    while (readerA.hasNext()) {
      Frame currentFrameA = readerA.getNext();

      if (currentFrameA.getSkeletonsCount() > 0) {
        double minError = Double.MAX_VALUE;
        int minI = -1;
        int i = 0;
        readerB.reset();
        while (readerB.hasNext()) {
          Frame currentFrameB = readerB.getNext();

          if (currentFrameA.getSkeletonsCount() != currentFrameB.getSkeletonsCount()) {
            continue;
          }

          boolean[] jointMask = FrameUtils.joinJointMasks(FrameUtils.jointMasks(currentFrameA),
              FrameUtils.jointMasks(currentFrameB));

          double[] dataA = FrameUtils.jointMatrix(currentFrameA, jointMask);
          DenseMatrix64F denseA = new DenseMatrix64F(dataA.length / 3, 3, true, dataA);
          double[] dataB = FrameUtils.jointMatrix(currentFrameB, jointMask);
          DenseMatrix64F denseB = new DenseMatrix64F(dataB.length / 3, 3, true, dataB);
          Transform transform = CoordinateTransform.calculateTransform(denseA, denseB);
          if (transform.getError() < minError) {
            minI = i;
            minError = transform.getError();
          }

          i++;
        }

        if (minI != -1) {
          offsets.add(new OffsetError(minError, currentFrameAIndex - minI));
        }
      }

      currentFrameAIndex++;
    }

    Collections.sort(offsets);

    double meanOffset = 0;
    for (int i = 0; i < offsets.size(); i++) {
      OffsetError offset = offsets.get(i);
      meanOffset += offset.getOffset();
    }
    meanOffset = meanOffset / offsets.size();

    return (int) Math.round(meanOffset);
  }

  private static void extractData(String fileA, String fileB, final int offset,
      List<double[]> dataADest, List<double[]> dataBDest) throws IOException {
    FrameReader readerA = new FrameReader(fileA);
    FrameReader readerB = new FrameReader(fileB);

    int globalFrameIndex = 0;
    while (globalFrameIndex + offset < 0) {
      readerA.getNext();
      globalFrameIndex++;
    }

    while (readerA.hasNext() && readerB.hasNext()) {
      Frame frameA = readerA.getNext();
      Frame frameB = readerB.getNext();

      if (frameA.getSkeletonsCount() != frameB.getSkeletonsCount()
          || frameA.getSkeletonsCount() == 0) {
        continue;
      }

      boolean[] jointMask =
          FrameUtils.joinJointMasks(FrameUtils.jointMasks(frameA), FrameUtils.jointMasks(frameB));

      double[] dataA = FrameUtils.jointMatrix(frameA, jointMask);
      dataADest.add(dataA);
      double[] dataB = FrameUtils.jointMatrix(frameB, jointMask);
      dataBDest.add(dataB);
    }
  }

  private static double[] concatList(List<double[]> list) {
    double[][] dataXArray = new double[list.size()][];
    list.toArray(dataXArray);
    double[] allDataX = Doubles.concat(dataXArray);
    return allDataX;
  }

  private static class OffsetError implements Comparable<OffsetError> {
    private final double error;
    private final int offset;

    public OffsetError(double error, int offset) {
      this.error = error;
      this.offset = offset;
    }

    public double getError() {
      return error;
    }

    public int getOffset() {
      return offset;
    }

    @Override
    public int compareTo(OffsetError other) {
      return Double.compare(this.error, other.getError());
    }
  }


}
