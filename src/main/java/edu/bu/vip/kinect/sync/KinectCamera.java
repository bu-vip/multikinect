package edu.bu.vip.kinect.sync;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ejml.data.DenseMatrix64F;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

public class KinectCamera {

  public static KinectCamera buildTree(int rootId, List<int[]> pairs) {
    List<int[]> pairsLeft = new LinkedList<int[]>();
    pairsLeft.addAll(pairs);

    KinectCamera root = new KinectCamera(rootId);
    Set<KinectCamera> currentNodes = new HashSet<>();
    currentNodes.add(root);
    while (pairsLeft.size() > 0) {
      // Set to hold the next level of nodes
      Set<KinectCamera> nextNodes = new HashSet<>();
      // Simple boolean to check for an unsolvable pair set
      boolean madeProgress = false;

      // Loop through current level
      for (KinectCamera currentNode : currentNodes) {
        // Iterate over all pairs
        int pairIndex = 0;
        while (pairIndex < pairsLeft.size()) {
          // Check if pair contains the current node
          int[] pair = pairsLeft.get(pairIndex);
          int index = Ints.indexOf(pair, currentNode.getId());
          if (index >= 0) {
            // Create new camera and add it to the currentNode
            int newCameraId = pair[(index == 0 ? 1 : 0)];
            KinectCamera newCamera = new KinectCamera(newCameraId);
            currentNode.addChild(newCamera);
            // New node is on next level
            nextNodes.add(newCamera);
            // Made some progress
            madeProgress = true;
            // Remove pair as handled
            pairsLeft.remove(pairIndex);
          } else {
            // Next pair
            pairIndex++;
          }
        }
      }

      if (!madeProgress) {
        throw new RuntimeException("Didn't make progress...");
      }

      // Go to next level of tree
      currentNodes = nextNodes;
    }

    return root;
  }

  private final int id;
  private KinectCamera parent = null;
  private final List<KinectCamera> children = new LinkedList<>();
  private DenseMatrix64F tranform;

  public KinectCamera(int id) {
    this.id = id;
  }

  public void setParent(KinectCamera parent) {
    if (this.parent != null) {
      throw new RuntimeException("Camera " + id + " already has a parent");
    }

    this.parent = parent;
  }

  public void addChild(KinectCamera child) {
    children.add(child);
    child.setParent(this);
  }

  public int getId() {
    return this.id;
  }

  public KinectCamera getParent() {
    return this.getParent();
  }

  public ImmutableList<KinectCamera> getChildren() {
    return ImmutableList.copyOf(this.children);
  }

  public DenseMatrix64F getTranform() {
    return tranform;
  }

  public void setTranform(DenseMatrix64F tranform) {
    this.tranform = tranform;
  }

  @Override
  public String toString() {

    return this.toString(1);
  }

  private String toString(int tabAmount) {
    String result = "Id: " + this.id;
    result += " parent: " + (this.parent == null ? "NONE" : this.parent.getId());

    for (KinectCamera child : this.children) {
      result += "\n";
      for (int i = 0; i < tabAmount; i++) {
        result += "\t";
      }
      result += child.toString(tabAmount + 1);
    }

    return result;
  }
}
