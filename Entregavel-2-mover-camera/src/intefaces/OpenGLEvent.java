package intefaces;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import model.Ponto;

/**
 *
 * @author Argeu
 */
public class OpenGLEvent implements GLEventListener{
    private final InputEvent key;
    private final Ponto p1;
    private final Ponto p2;
    private GLU glu;
    
    public OpenGLEvent(InputEvent key, Ponto p1, Ponto p2){
        this.key = key;
        this.p1 = p1;
        this.p2 = p2;
        this.key.centroX = (p1.x + p2.x) / 2;
        this.key.centroY = (p1.y + p2.y) / 2;
        this.key.centroZ = (p1.z + p2.z) / 2;
        
    }
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.setSwapInterval(1);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); 
    }

    public void display(GLAutoDrawable drawable) {
       GL gl = drawable.getGL();
       gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
       gl.glLoadIdentity();
        
       if (key.isWireFrame) {
           gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
           gl.glLineWidth(1.0f);
       } else {
          gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );
          gl.glLineWidth(0.0f);
       }
       gl.glEnable(GL.GL_DEPTH_TEST);
       
       glu.gluLookAt(key.eyeX, key.eyeY, key.eyeZ, key.centroX, key.centroY, key.centroZ, key.upx, key.upy, key.upz);

       drawCube(gl);
       
       gl.glFlush();
    }

    public void drawCube(GL gl) {
        //cima
        Ponto topRightCima = p2;
        Ponto topLeftCima = new Ponto(p1.x, p2.y, p2.z);
        Ponto botLeftCima = new Ponto(p1.x, p2.y, p1.z);
        Ponto botRightCima = new Ponto(p2.x, p2.y, p1.z);
        //baixo
        Ponto topRightBaixo = new Ponto(p2.x, p1.y, p2.z);
        Ponto topLeftBaixo = new Ponto(p1.x, p1.y, p2.z);
        Ponto botLeftBaixo = p1;
        Ponto botRightBaixo = new Ponto(p2.x, p1.y, p1.z);
        
        //Face Bottom
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            drawFace(gl, topLeftBaixo, topRightBaixo, botRightBaixo, botLeftBaixo);
        gl.glEnd();
        
        //Face Back
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.9f, 0.5f, 1.0f);
            drawFace(gl, botRightCima, botLeftCima, botLeftBaixo, botRightBaixo);
        gl.glEnd();
        
        //Face Right
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.5f, 0.9f, 1.0f);
            drawFace(gl, topRightCima, botRightCima, botRightBaixo, topRightBaixo);
        gl.glEnd();
        
        //Face Left
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.5f, 0.9f, 0.0f);
            drawFace(gl, botLeftCima, topLeftCima, topLeftBaixo, botLeftBaixo);
        gl.glEnd();
        
        //Face Top
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(0.0f, 0.0f, 1.0f);
            drawFace(gl, topLeftCima, topRightCima, botRightCima, botLeftCima);
        gl.glEnd();
        
        //Face Front
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            drawFace(gl, topRightCima, topLeftCima, topLeftBaixo, topRightBaixo);
        gl.glEnd();
        
    }
    
    public void drawFace(GL gl, Ponto topRight, Ponto topLeft, Ponto bottomLeft, Ponto bottomRight){
        gl.glVertex3f(topRight.x, topRight.y, topRight.z);   // Top Right
        gl.glVertex3f(topLeft.x, topLeft.y, topLeft.z);  // Top Left
        gl.glVertex3f(bottomLeft.x, bottomLeft.y, bottomLeft.z); // Bottom Left
        gl.glVertex3f(bottomRight.x, bottomRight.y, bottomRight.z);  // Bottom Right
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!
            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu.gluPerspective(60.0f, h, 1.0, 500.0);
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        
    }
    
}
