package org.yourorghere;

import com.sun.opengl.util.Animator;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import model.Ponto;



/**
 * JOGJTest.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class JOGJTest implements GLEventListener {
    public static Ponto p1;
    public static Ponto p2;
    public static Ponto eye;
    public static int dir = 1;
    public static float up = 1;

    public static void main(String[] args) throws InterruptedException {
        Frame frame = new Frame("Simple JOGL Application");
        GLCanvas canvas = new GLCanvas();

        canvas.addGLEventListener(new JOGJTest());
        frame.add(canvas);
        frame.setSize(640, 480);
        final Animator animator = new Animator(canvas);
        
        //TODO Ler os postos no console
        Scanner s = new Scanner(System.in);
//        System.out.println("Digite as coordenadas do ponto inicial separados por vírgula (ex: x,y,z)");
//        
//        String xyz [] = s.nextLine().split(",");
//        int x1 = Integer.parseInt(xyz[0]);
//        int y1 = Integer.parseInt(xyz[1]);
//        int z1 = Integer.parseInt(xyz[2]);
//        
//        System.out.println("Digite as coordenadas do ponto inicial separados por vírgula (ex: x,y,z)");
//        
//        xyz = s.nextLine().split(",");
//        
//        int x2 = Integer.parseInt(xyz[0]);
//        int y2 = Integer.parseInt(xyz[1]);
//        int z2 = Integer.parseInt(xyz[2]);
//        
//        p1 = new Ponto(x1,y1,z1);
//        p2 = new Ponto(x2,y2,z2);
//        
//        if(p1.x > p2.x && p1.y > p2.y && p1.z > p2.z){
//            Ponto aux = p2;
//            p2 = p1;
//            p1 = aux;
//        }
        
        p1 = new Ponto(0,0,0);
        p2 = new Ponto(2,2,2);
        
        eye = new Ponto(1,7,0);
        
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
        

    }

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.
        
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!
        
            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu.gluPerspective(60f, h, 1.0, 20.0);
        
        float centroX = (p2.x + p1.x) / 2;
        float centroY = (p2.y + p1.y) / 2;
        float centroZ = (p2.z + p1.z) / 2;
        
        glu.gluLookAt(
                      p2.x*2, p2.y*2, p2.z*2,
//                        eye.x, eye.y, eye.z,
//                      1,1,-6,
//                      1f,1f,2,
                      centroX, centroY, centroZ,
                      0, 1, 0);
        
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        gl.glEnable(GL.GL_DEPTH_TEST);
        
        // Draw A Cube
        drawCube(gl);
        
        moveCamera(gl);
//        gl.glBegin(GL.GL_QUADS);
//            gl.glColor3f(1.0f, 0.0f, 0.0f);    // Set the current drawing color to light blue
//            gl.glVertex3f(0, 2, 2);  // Top Left
//            gl.glVertex3f(2,2,2);   // Top Right
//            gl.glVertex3f(2,0,2);  // Bottom Right
//            gl.glVertex3f(0,0,2); // Bottom Left
//        // Done Drawing The Quad
//        gl.glEnd();

        // Flush all drawing operations to the graphics card
        gl.glFlush();
        
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void drawCube(GL gl) {
        //Cima
        Ponto topRightCima = p2;
        Ponto topLeftCima = new Ponto(p1.x, p2.y, p2.z);
        Ponto botLeftCima = new Ponto(p1.x, p2.y, p1.z);
        Ponto botRightCima = new Ponto(p2.x, p2.y, p1.z);
        //Baixo
        Ponto topRightBaixo = new Ponto(p2.x, p1.y, p2.z);
        Ponto topLeftBaixo = new Ponto(p1.x, p1.y, p2.z);
        Ponto botLeftBaixo = p1;
        Ponto botRightBaixo = new Ponto(p2.x, p1.y, p1.z);
        
        //Face Bottom
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(1.0f, 0.0f, 0.0f);    // Set the current drawing color to light blue
            gl.glVertex3f(topLeftBaixo.x, topLeftBaixo.y, topLeftBaixo.z);  // Top Left
            gl.glVertex3f(topRightBaixo.x, topRightBaixo.y, topRightBaixo.z);   // Top Right
            gl.glVertex3f(botRightBaixo.x, botRightBaixo.y, botRightBaixo.z);  // Bottom Right
            gl.glVertex3f(botLeftBaixo.x, botLeftBaixo.y, botLeftBaixo.z); // Bottom Left
        // Done Drawing The Quad
        gl.glEnd();
        
        //Face Back
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.9f, 0.5f, 1.0f);    // Set the current drawing color to light blue
            gl.glVertex3f(botRightCima.x, botRightCima.y, botRightCima.z);   // Top Right
            gl.glVertex3f(botLeftCima.x, botLeftCima.y, botLeftCima.z);  // Top Left
            gl.glVertex3f(botLeftBaixo.x, botLeftBaixo.y, botLeftBaixo.z); // Bottom Left
            gl.glVertex3f(botRightBaixo.x, botRightBaixo.y, botRightBaixo.z);  // Bottom Right
        // Done Drawing The Quad
        gl.glEnd();
        
        //Face Right
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.5f, 0.9f, 1.0f);    // Set the current drawing color to light blue
            gl.glVertex3f(topRightCima.x, topRightCima.y, topRightCima.z);   // Top Right
            gl.glVertex3f(botRightCima.x, botRightCima.y, botRightCima.z);  // Top Left
            gl.glVertex3f(botRightBaixo.x, botRightBaixo.y, botRightBaixo.z); // Bottom Left
            gl.glVertex3f(topRightBaixo.x, topRightBaixo.y, topRightBaixo.z);  // Bottom Right
        // Done Drawing The Quad
        gl.glEnd();
        
        //Face Left
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.5f, 0.9f, 0.0f);    // Set the current drawing color to light blue
            gl.glVertex3f(botLeftCima.x, botLeftCima.y, botLeftCima.z);   // Top Right
            gl.glVertex3f(topLeftCima.x, topLeftCima.y, topLeftCima.z);  // Top Left
            gl.glVertex3f(topLeftBaixo.x, topLeftBaixo.y, topLeftBaixo.z);  // Bottom Left
            gl.glVertex3f(botLeftBaixo.x, botLeftBaixo.y, botLeftBaixo.z); // Bottom Right
        // Done Drawing The Quad
        gl.glEnd();
        
        //Face Top
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.0f, 0.0f, 1.0f);    // Set the current drawing color to light blue
            gl.glVertex3f(topLeftCima.x, topLeftCima.y, topLeftCima.z);  // Top Left
            gl.glVertex3f(topRightCima.x, topRightCima.y, topRightCima.z);   // Top Right
            gl.glVertex3f(botRightCima.x, botRightCima.y, botRightCima.z);  // Bottom Right
            gl.glVertex3f(botLeftCima.x, botLeftCima.y, botLeftCima.z); // Bottom Left
        // Done Drawing The Quad
        gl.glEnd();
        
        //Face Front
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(1.0f, 1.0f, 1.0f);    // Set the current drawing color to light blue
            gl.glVertex3f(topRightCima.x, topRightCima.y, topRightCima.z);   // Top Right
            gl.glVertex3f(topLeftCima.x, topLeftCima.y, topLeftCima.z);  // Top Left
             gl.glVertex3f(topLeftBaixo.x, topLeftBaixo.y, topLeftBaixo.z); // Bottom Left
            gl.glVertex3f(topRightBaixo.x, topRightBaixo.y, topRightBaixo.z);  // Bottom Right
        // Done Drawing The Quad
        gl.glEnd();
        
    }
    
    public static void moveCamera(GL gl){
                GLU glu = new GLU();
                float andaH = eye.x;
                float andaV = eye.y;
                
                switch(dir){
                    case 1:
                        up = 1;
                        andaH += 1;
                        andaV -= 1;
                        if(andaH == 7){
                            dir = 2;
                        }
                        break;
                    case 2:
                        up = 1;
                        andaH -= 1;
                        andaV += 1;
                        if(andaH == 0){
                            dir = 1;
                        }
                        break;
                    case 3:
                        up = -1;
                        andaH -= 1;
                        andaV -= 1;
                        if(andaH == -7){
                            dir = 4;
                        }
                        break;
                    case 4:
                        up = -1;
                        andaH += 1;
                        andaV += 1;
                        if(andaH == 0){
                            dir = 1;
                        }
                        break;
                }
                eye.x = andaH;
                eye.y = andaV;
                
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(JOGJTest.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu.gluPerspective(60f, 640/480, 1.0, 20.0);
                
                glu.gluLookAt(
                      eye.x, eye.y, eye.z,
                      1, 1, 1,
                      0, 1, 0);
        
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
//        gl.glLoadIdentity();
        
        }
}

