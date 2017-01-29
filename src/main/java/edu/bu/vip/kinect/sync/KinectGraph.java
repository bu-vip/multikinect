package edu.bu.vip.kinect.sync;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import edu.bu.vip.kinect.sync.CoordinateTransform.Transform;

public class KinectGraph {

  private DirectedGraph<String, FileEdge> graph =
      new DirectedMultigraph<>(new ClassBasedEdgeFactory<String, FileEdge>(FileEdge.class));

  public KinectGraph() {

  }

  public void addCalibrationFrame(String kinectIdA, String fileA, String kinectIdB, String fileB) {
    graph.addVertex(kinectIdA);
    graph.addVertex(kinectIdB);
    addFileToEdge(kinectIdA, kinectIdB, fileA, fileB);
    addFileToEdge(kinectIdB, kinectIdA, fileB, fileA);
  }

  private void addFileToEdge(String start, String end, String fileA, String fileB) {
    FileEdge edge = graph.getEdge(start, end);
    if (edge == null) {
      edge = new FileEdge(start, end);
      graph.addEdge(start, end, edge);
    }
    edge.addCalibrationFile(fileA, fileB);
  }

  public SimpleMatrix transformBetweenKinects(String startId, String endId) {
    List<FileEdge> path = DijkstraShortestPath.findPathBetween(graph, startId, endId);
   
    SimpleMatrix combined = SimpleMatrix.identity(4);
    for (FileEdge edge : path) {
      combined = new SimpleMatrix(edge.getTransform()).mult(combined);
    }
    
    return combined;
  }

  public static class FileEdge extends DefaultEdge {
    private static final long serialVersionUID = -2078620037807429905L;
    private String v1;
    private String v2;
    private final List<String> calibrationFilesStart = new LinkedList<String>();
    private final List<String> calibrationFilesEnd = new LinkedList<String>();
    private boolean transformValid = false;
    private DenseMatrix64F transform;

    public FileEdge(String v1, String v2) {
      this.v1 = v1;
      this.v2 = v2;
    }

    public String getV1() {
      return v1;
    }

    public String getV2() {
      return v2;
    }

    public void addCalibrationFile(String startFile, String endFile) {
      transformValid = false;
      this.calibrationFilesStart.add(startFile);
      this.calibrationFilesEnd.add(endFile);
    }

    public DenseMatrix64F getTransform() {
      if (!transformValid) {
        try {
          // Calculate best transform for each calibration frame
          Iterator<String> startFiles = calibrationFilesStart.iterator();
          Iterator<String> endFiles = calibrationFilesEnd.iterator();
          List<Transform> transforms = new LinkedList<>();
          int numOfRows = 0;
          while (startFiles.hasNext() && endFiles.hasNext()) {
            Transform transform = CoordinateTransform.calculateTransform(startFiles.next(), endFiles.next());
            transforms.add(transform);
            numOfRows += transform.getxData().numRows;
          }
          
          // Combine all calibration frame data
          SimpleMatrix xData = new SimpleMatrix(numOfRows, 3);
          SimpleMatrix yData = new SimpleMatrix(numOfRows, 3);
          int curRow = 0;
          for (Transform transform : transforms) {
            xData.insertIntoThis(curRow, 0, new SimpleMatrix(transform.getxData()));
            yData.insertIntoThis(curRow, 0, new SimpleMatrix(transform.getyData()));
            curRow += transform.getxData().numRows;
          }
          
          // Calculate the transform for all calibration frames
          transform = CoordinateTransform.calculateTransform(xData.getMatrix(), yData.getMatrix()).getTransform();
          transformValid = true;
        } catch (IOException e) {
          throw new RuntimeException(e.getMessage());
        }
      }

      return transform;
    }
  }
}
