package com.roeper.bu.kinectdisplay;

import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.roeper.bu.kinect.Protos.Frame;
import com.roeper.bu.kinect.Protos.Joint.JointType;
import com.roeper.bu.kinect.Protos.Position;
import com.roeper.bu.kinect.Protos.Skeleton;
import com.roeper.bu.kinectdisplay.math.Vector3;

import edu.bu.vip.kinect.sync.FrameReader;

public class KinectDrawer {
  private static final float SPHERE = 0.05f;
  private static final float RADIUS = 0.02f;

  private FrameReader reader;
  private long readerIndex = 0;
  private final double[] columnMatrix;
  private final double[] color;

  public KinectDrawer(FrameReader reader, double[] matrix, double[] color) {
    this.reader = reader;
    this.columnMatrix = Utils.rowMajorToColumnMajor(matrix);
    this.color = color;
  }

  public void drawFrame(GL gl, GLU glu, long frameIndex) {
    GL2 gl2 = gl.getGL2();

    gl2.glPushMatrix();
    gl2.glMultMatrixd(this.columnMatrix, 0);
    gl2.glColor3dv(this.color, 0);

    drawSphere(gl, glu, Vector3.zero);
    try {
      if (frameIndex >= 0) {
        if (frameIndex < readerIndex) {

          reader.reset();
          readerIndex = 0;
        }
      }

      while (readerIndex < frameIndex && reader.hasNext()) {
        reader.getNext();
        readerIndex++;
      }

      if (reader.hasNext()) {
        Frame frame = reader.getNext();
        for (Skeleton skel : frame.getSkeletonsList()) {
          drawSkeleton(gl, glu, skel);
        }
        readerIndex++;
      }

    } catch (IOException e) {
      // TODO: handle exception
      e.printStackTrace();
    }

    gl2.glPopMatrix();
  }

  private void drawSkeleton(GL gl, GLU glu, Skeleton skel) {


    int[][] drawOrder = {
        // Torso
        {JointType.HEAD__VALUE, JointType.NECK__VALUE},
        {JointType.NECK__VALUE, JointType.SPINE_SHOULDER_VALUE},
        {JointType.SPINE_SHOULDER_VALUE, JointType.SPINE_MID_VALUE},
        {JointType.SPINE_MID_VALUE, JointType.SPINE_BASE_VALUE},
        // Left leg
        {JointType.SPINE_BASE_VALUE, JointType.HIP_LEFT_VALUE},
        {JointType.HIP_LEFT_VALUE, JointType.KNEE_LEFT_VALUE},
        {JointType.KNEE_LEFT_VALUE, JointType.ANKLE_LEFT_VALUE},
        {JointType.ANKLE_LEFT_VALUE, JointType.FOOT_LEFT_VALUE},
        // Right left
        {JointType.SPINE_BASE_VALUE, JointType.HIP_RIGHT_VALUE},
        {JointType.HIP_RIGHT_VALUE, JointType.KNEE_RIGHT_VALUE},
        {JointType.KNEE_RIGHT_VALUE, JointType.ANKLE_RIGHT_VALUE},
        {JointType.ANKLE_RIGHT_VALUE, JointType.FOOT_RIGHT_VALUE},
        // Left arm
        {JointType.SPINE_SHOULDER_VALUE, JointType.SHOULDER_LEFT_VALUE},
        {JointType.SHOULDER_LEFT_VALUE, JointType.ELBOW_LEFT_VALUE},
        {JointType.ELBOW_LEFT_VALUE, JointType.WRIST_LEFT_VALUE},
        {JointType.WRIST_LEFT_VALUE, JointType.HAND_LEFT_VALUE},
        // Right arm
        {JointType.SPINE_SHOULDER_VALUE, JointType.SHOULDER_RIGHT_VALUE},
        {JointType.SHOULDER_RIGHT_VALUE, JointType.ELBOW_RIGHT_VALUE},
        {JointType.ELBOW_RIGHT_VALUE, JointType.WRIST_RIGHT_VALUE},
        {JointType.WRIST_RIGHT_VALUE, JointType.HAND_RIGHT_VALUE},

    };


    for (int i = 0; i < drawOrder.length; i++) {
      Position posA = skel.getJoints(drawOrder[i][0]).getPosition();
      Position posB = skel.getJoints(drawOrder[i][1]).getPosition();

      drawCylinder(gl, glu, new Vector3(posA.getX(), posA.getY(), posA.getZ()),
          new Vector3(posB.getX(), posB.getY(), posB.getZ()));
    }
  }

  private void drawSphere(GL gl, GLU glu, Vector3 pos) {
    GL2 gl2 = gl.getGL2();

    gl2.glPushMatrix();

    gl2.glTranslatef(pos.x, pos.y, pos.z);

    GLUquadric prim = glu.gluNewQuadric();
    glu.gluSphere(prim, SPHERE, 32, 32);
    glu.gluDeleteQuadric(prim);

    gl2.glPopMatrix();
  }

  private void drawCylinder(GL gl, GLU glu, Vector3 a, Vector3 b) {
    GL2 gl2 = gl.getGL2();

    Vector3 center = a.subtract(b);
    Vector3 axis = Vector3.k.crossProduct(center);
    float angle = 180 / 3.14f * (float) Math.acos(Vector3.k.dotProduct(center) / center.length());

    gl2.glPushMatrix();

    gl2.glTranslatef(b.x, b.y, b.z);
    gl2.glRotatef(angle, axis.x, axis.y, axis.z);

    GLUquadric prim = glu.gluNewQuadric();
    glu.gluCylinder(prim, RADIUS, RADIUS, center.length(), 16, 16);
    glu.gluDeleteQuadric(prim);

    gl2.glPopMatrix();
  }
}
