package org.yourorghere;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import com.sun.opengl.util.GLUT;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import javax.swing.JOptionPane;

/**
 * GLRenderer.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GLRenderer implements GLEventListener, MouseListener, MouseMotionListener {

    private float view_rotx = 20.0f, view_roty = 30.0f, view_rotz = 0.0f;
    private int icosaedro;
    static private float angle = 0.0f;
    private int prevMouseX, prevMouseY;
    private boolean mouseRButtonDown = true;
    static float q = 0.0f, w = 0.8f, e = 0.2f, r = 1.0f;

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.setSwapInterval(2);
        //gl.glEnable(GL.GL_CULL_FACE);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_DEPTH_TEST);

        GLUT glut = new GLUT();
        //  float[] color = {0.8f, 0.1f, 0.0f, 1.0f}; /*rojo*/
        //  float color[] = { 1f, 0.2f, 1.0f, 1.0f }; /*rosa*/
        float color[] = {q, w, e, r};/*verde*/

        //  float color[] = { 0.2f, 0.2f, 1.0f, 1.0f }; /*azul*/

        icosaedro = gl.glGenLists(1);
        gl.glNewList(icosaedro, GL.GL_COMPILE);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, color, 0);
        icosaedro_generado(glut);
        gl.glEndList();

        // Enable VSync
        gl.glEnable(GL.GL_NORMALIZE);

        drawable.addMouseListener(this);
        drawable.addMouseMotionListener(this);
        drawable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                dispatchKey(e.getKeyCode(), e.getKeyChar());
            }
        });

    }
    /*Cordenadas en X y Y*/
    static int x = 0, y = 0;
    static double Escala = 3;

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();

        float h = (float) height / (float) width;

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 80.0f);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        /*el ultimo valor es para acercarlo a la camara o alejarlo*/
        gl.glTranslatef(0.0f, 0.0f, -30.0f);
    }

    public static void icosaedro_generado(GLUT glut) {
        glut.glutSolidIcosahedron();
        //glut.glutSolidTeapot(2);
        //glut.glutSolidCone(10, 20, 3, 1);

    }

    public void display(GLAutoDrawable drawable) {
        //Rotacion
        angle += 0.1f;
        //angle=0;

        // Get the GL corresponding to the drawable we are animating
        GL gl = drawable.getGL();

        // Special handling for the case where the GLJPanel is translucent
        // and wants to be composited with other Java 2D content
        if ((drawable instanceof GLJPanel)
                && !((GLJPanel) drawable).isOpaque()
                && ((GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent()) {
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        } else {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        }
        gl.glPushMatrix();
        gl.glRotatef(view_rotx, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(view_roty, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(view_rotz, 0.0f, 0.0f, 1.0f);

        // Place the first gear and call its display list
        gl.glPushMatrix();
        gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(x, y, 0);
        gl.glScaled(Escala, Escala, Escala);
        gl.glCallList(icosaedro);
        gl.glPopMatrix();

        gl.glPopMatrix();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        prevMouseX = e.getX();
        prevMouseY = e.getY();
        if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
            mouseRButtonDown = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
            mouseRButtonDown = false;
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    // Methods required for the implementation of MouseMotionListener
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        Dimension size = e.getComponent().getSize();

        float thetaY = 360.0f * ((float) (x - prevMouseX) / (float) size.width);
        float thetaX = 360.0f * ((float) (prevMouseY - y) / (float) size.height);

        prevMouseX = x;
        prevMouseY = y;

        view_rotx += thetaX;
        view_roty += thetaY;
    }

    public void mouseMoved(MouseEvent e) {
    }
    //-+-------------------------------------------------------------------------------------------------------

    static private boolean[] b = new boolean[256];
    private int program = 2;
    private int obj = 2;

    static private void dispatchKey(int key, char k) {
        if (k < 256) {
            b[k] = !b[k];
        }

        switch (key) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
                y = y + 1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                y = y - 1;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                x = x - 1;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
                x = x + 1;
                break;
            case KeyEvent.VK_K:
                Escala = Escala - 1;
                break;
            case KeyEvent.VK_L:
                Escala = Escala + 1;
                break;
            case KeyEvent.VK_1:
            case KeyEvent.VK_NUMPAD1:
                angle += 10;
                break;
            case KeyEvent.VK_0:
            case KeyEvent.VK_NUMPAD0:
                angle -= 0.9;
                break;

            case KeyEvent.VK_H:
                String endl = System.getProperty("line.separator");
                endl = endl + endl;
                String msg
                        = ("No se como hice esto pero funciona" + endl
                        + "Usar letras K(+) o  L(-) para Escalar" + endl
                        + "Usar Flechas para Trasladar" + endl
                        + "Mueve el raton para rotar el objeto" + endl
                        + "Mantener sumido 0 o 1 para cambiar velocidad de la animacion" + endl
                        + "Para salir presiona Esc " + endl);
                JOptionPane.showMessageDialog(null, msg, "Help", JOptionPane.INFORMATION_MESSAGE);
                break;

            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_Q:
                System.exit(0);
                return;

        }

    }
}
