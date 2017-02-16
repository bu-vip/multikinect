package edu.bu.vip.multikinectdisplay;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import com.google.common.collect.ImmutableList;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;// for new version of gl
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinectdisplay.math.Quaternion;
import edu.bu.vip.multikinectdisplay.math.Vector3;

import edu.bu.vip.multikinect.sync.FrameReader;

public class KinectDisplay extends JFrame
    implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

  private static final long serialVersionUID = -6029116962099051639L;
  private static final double[] RED = {1, 0, 0};
  private static final double[] GREEN = {0, 1, 0};

  public static void main(String[] args) throws Exception {
    KinectDisplay display = new KinectDisplay();
    display.run();
  }

  private GLCapabilities capabilities;
  private GLCanvas canvas;
  private FPSAnimator animator;
  private GLU glu;
  private ImmutableList<KinectDrawer> kinects;
  private long globalFrame = 0;
  private long maxFrames = 0;
  private List<Integer> frameOffsets;
  private Quaternion _cameraRotation = Quaternion.identity;
  private int mouseLastX = 0, mouseLastY = 0;

  public KinectDisplay() throws IOException {
    ImmutableList<String> files = ImmutableList.of(
        // "/home/doug/Desktop/kinectdata/9-24/data-window/take-2016-09-24_20-44-13-818.dat",
        // "/home/doug/Desktop/kinectdata/9-24/data-corner/take-2016-09-24_20-44-13-707.dat");

        // "/home/doug/Desktop/kinectdata/9-24/data-window/take-2016-09-24_20-42-11-227.dat",
        // "/home/doug/Desktop/kinectdata/9-24/data-corner/take-2016-09-24_20-42-07-628.dat");

        // "/home/doug/Desktop/kinectdata/9-30/data-window/take-2016-09-30_17-47-36-795.dat",
        // "/home/doug/Desktop/kinectdata/9-30/data-corner/take-2016-09-30_17-47-34-521.dat");

        //"/home/doug/Desktop/kinectdata/9-30/data-window/take-2016-09-30_12-53-07-779.dat",
        //"/home/doug/Desktop/kinectdata/9-30/data-corner/take-2016-09-30_12-52-59-340.dat");
        
        "/home/doug/Desktop/kinect/calibration/session-2016-12-09_09:06:43/0.dat",
        "/home/doug/Desktop/kinect/calibration/session-2016-12-09_09:06:43/1.dat");


    /*
    SimpleMatrix mat = new SimpleMatrix(4, 4, true, 
        0.621, 0.194, 0.759, -2.524, 
        -0.123, 0.981, -0.150, 0.471, 
        -0.774, -0.000, 0.633, 3.280, 
        0.000, 0.000, 0.000, 1.000);
        */
    
    // System.out.println("Error: "
    // + CoordinateTransform.calculateError(files.get(0), files.get(1), mat.getMatrix(), -50));

    /*
    double[] matA = {0.6050562, 0.2141562, 0.7668403, -2.49758, -0.1261595, 0.9767661, -0.1732393,
        0.56566, -0.7861239, 0.0080753, 0.6180162, 3.36984, 0, 0, 0, 1};
    */
    double[] matA = { 0.964,-0.037,-0.264, 2.537,
        0.084, 0.982, 0.168,-0.015,
        0.253,-0.184, 0.950,-0.019,
        0.000, 0.000, 0.000, 1.000};
    
    double[] matB = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
    //ImmutableList<double[]> matrices = ImmutableList.of(mat.getMatrix().data, matB);
    ImmutableList<double[]> matrices = ImmutableList.of(matA, matB);


    // System.out.println("Error: " + CoordinateTransform.calculateError(files.get(0), files.get(1),
    // new DenseMatrix64F(4, 4, true, matA), -50));

    ImmutableList<double[]> colors = ImmutableList.of(RED, GREEN);

    ImmutableList.Builder<KinectDrawer> builder = ImmutableList.builder();
    for (int i = 0; i < files.size(); i++) {
      String file = files.get(i);
      long count = FrameReader.numberOfFrames(file);
      if (count > maxFrames) {
        maxFrames = count;
      }
      
      FrameReader test = new FrameReader(file);
      long c = 0;
      while (test.hasNext()) {
        Frame frame = test.getNext();
        if (frame.getSkeletonsCount() > 1) {
          System.out.println(c);
        }
        c++;
      }
      
      FrameReader reader = new FrameReader(file);
      builder.add(new KinectDrawer(reader, matrices.get(i), colors.get(i)));
    }
    kinects = builder.build();

    frameOffsets = new LinkedList<>();
    frameOffsets.add(0); // -40, -90, 50
    frameOffsets.add(0);

    capabilities = new GLCapabilities(null);
    capabilities.setDoubleBuffered(true);
    glu = new GLU();

    canvas = new GLCanvas(capabilities);
    canvas.addGLEventListener(this);
    canvas.addMouseListener(this);
    canvas.addMouseMotionListener(this);
    canvas.addKeyListener(this);
    canvas.setFocusable(true);
    getContentPane().add(canvas);

    animator = new FPSAnimator(canvas, 60);

    setTitle("Kinect Display");
    setSize(1000, 1000);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
    setResizable(true);
  }

  public void run() {
    animator.start();
  }

  public void init(GLAutoDrawable drawable) {
    GL gl = drawable.getGL();
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glLineWidth(1.0f);
  }

  private void setProjection(GL gl) {
    GL2 gl2 = gl.getGL2();
    // Change to projection matrix.
    gl2.glMatrixMode(GL2.GL_PROJECTION);
    gl2.glLoadIdentity();

    // Perspective.
    float widthHeightRatio = (float) getWidth() / (float) getHeight();
    glu.gluPerspective(45, widthHeightRatio, 1, 1000);
    glu.gluLookAt(-7, 4, -5, 0, 0, 2, 0, 1, 0);

  }

  public void display(GLAutoDrawable drawable) {
    GL gl = drawable.getGL();
    GL2 gl2 = gl.getGL2();

    setProjection(gl);

    // Change back to model view matrix.
    gl2.glMatrixMode(GL2.GL_MODELVIEW);
    gl2.glLoadIdentity();

    gl2.glMultMatrixf(_cameraRotation.toMatrix().columnMajor, 0);

    gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

    gl2.glColor3d(0.2, 0.2, 0.2);
    gl2.glBegin(GL2.GL_QUADS);
    gl2.glVertex3f(3, 0, -3);
    gl2.glVertex3f(3, 0, 7);
    gl2.glVertex3f(-3, 0, 7);
    gl2.glVertex3f(-3, 0, -3);
    gl2.glEnd();

    for (int i = 0; i < kinects.size(); i++) {
      KinectDrawer drawer = kinects.get(i);
      drawer.drawFrame(drawable.getGL(), glu, globalFrame + frameOffsets.get(i));
    }
    globalFrame++;

    if (globalFrame > maxFrames) {
      globalFrame = 0;
    }
  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

  public void keyTyped(KeyEvent key) {}

  public void keyPressed(KeyEvent key) {
    System.out.println("Global frame: " + globalFrame);
  }

  public void keyReleased(KeyEvent key) {}

  public void mouseClicked(MouseEvent mouse) {}

  public void mousePressed(MouseEvent mouse) {}

  public void mouseReleased(MouseEvent mouse) {}

  public void mouseMoved(MouseEvent mouse) {}

  public void mouseDragged(MouseEvent mouse) {
    // calculate delta
    Vector3 delta = new Vector3(mouse.getY() - mouseLastY, mouse.getX() - mouseLastX, 0);
    // see if delta is large enough
    if (delta.length() > 0.0001) {
      // calculate delta rotation
      Quaternion deltaRot = Quaternion.fromAxisAngle(delta, delta.length() * 0.01f);
      this._cameraRotation = deltaRot.multiply(_cameraRotation);

      // normalize
      this._cameraRotation.normalize();

      // save mouse coordinates
      this.mouseLastX = mouse.getX();
      this.mouseLastY = mouse.getY();
    }
  }

  public void mouseEntered(MouseEvent mouse) {}

  public void mouseExited(MouseEvent mouse) {}

  public void dispose(GLAutoDrawable drawable) {}
}
