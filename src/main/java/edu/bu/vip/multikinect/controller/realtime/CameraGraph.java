package edu.bu.vip.multikinect.controller.realtime;

import com.google.common.primitives.Doubles;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import java.util.List;
import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

/**
 * Graph of transforms between pairs of cameras. Each node of the grpah is a camera. Each edge
 * represents a transformation between two cameras learned during the calibration phase.
 */
public class CameraGraph {

  private DirectedGraph<String, CameraPairEdge> graph = new DirectedMultigraph<>(
      new ClassBasedEdgeFactory<String, CameraPairEdge>(CameraPairEdge.class));

  public CameraGraph(List<CameraPairCalibration> pairs) {
    for (CameraPairCalibration pair : pairs) {
      // TODO(doug) - Inverse edge?
      graph.addEdge(pair.getCameraA(), pair.getCameraB(), new CameraPairEdge(pair));
    }
  }

  /**
   * Calculates the transformation matrix for transforming cameraA's coordinate system into
   * cameraB's coordinate system.
   *
   * @param cameraA - Starting camera id
   * @param cameraB - Ending camera id
   */
  public DenseMatrix64F calculateTransform(String cameraA, String cameraB) {
    GraphPath<String, CameraPairEdge> path = DijkstraShortestPath
        .findPathBetween(graph, cameraA, cameraB);

    SimpleMatrix combined = SimpleMatrix.identity(4);
    for (CameraPairEdge edge : path.getEdgeList()) {
      combined = new SimpleMatrix(edge.getTransform()).mult(combined);
    }

    return combined.getMatrix();
  }

  private static class CameraPairEdge extends DefaultEdge {

    private static final long serialVersionUID = -2078620037807429905L;
    private CameraPairCalibration pair;

    public CameraPairEdge(CameraPairCalibration pair) {
      this.pair = pair;
    }

    public DenseMatrix64F getTransform() {
      return new DenseMatrix64F(4, 4, true, Doubles.toArray(pair.getTransformList()));
    }
  }
}
