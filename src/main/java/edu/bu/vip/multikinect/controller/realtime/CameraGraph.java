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

      graph.addVertex(pair.getCameraA());
      graph.addVertex(pair.getCameraB());

      DenseMatrix64F transform = new DenseMatrix64F(4, 4, true, Doubles.toArray(pair.getTransformList()));
      graph.addEdge(pair.getCameraA(), pair.getCameraB(), new CameraPairEdge(pair, transform, false));

      DenseMatrix64F inverse = inverseTransformationMatrix(transform);
      graph.addEdge(pair.getCameraB(), pair.getCameraA(), new CameraPairEdge(pair, inverse, true));
    }
  }

  private DenseMatrix64F inverseTransformationMatrix(DenseMatrix64F orig) {
    SimpleMatrix origMat = new SimpleMatrix(orig);

    SimpleMatrix rotationMat = origMat.extractMatrix(0, 3, 0, 3);
    SimpleMatrix translation = origMat.extractMatrix(0, 3, 3, 4);

    // For rotation matrices, the inverse is the transpose
    SimpleMatrix inverseRot = rotationMat.transpose();
    SimpleMatrix newTranslation = inverseRot.scale(-1).mult(translation);

    SimpleMatrix finalMat = new SimpleMatrix(4, 4);
    finalMat = finalMat.combine(0, 0, inverseRot);
    finalMat = finalMat.combine(0, 3, newTranslation);

    return finalMat.getMatrix();
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
      SimpleMatrix edgeTransform = new SimpleMatrix(edge.getTransform());
      combined = edgeTransform.mult(combined);
    }

    return combined.getMatrix();
  }

  private static class CameraPairEdge extends DefaultEdge {

    private static final long serialVersionUID = -2078620037807429905L;

    private final CameraPairCalibration pair;
    private final DenseMatrix64F transform;
    private final boolean inverse;


    public CameraPairEdge(CameraPairCalibration pair, DenseMatrix64F transform, boolean inverse) {
      this.pair = pair;
      this.transform = transform;
      this.inverse = inverse;
    }

    public DenseMatrix64F getTransform() {
      return transform;
    }
  }
}
