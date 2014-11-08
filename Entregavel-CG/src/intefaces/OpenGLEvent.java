package intefaces;

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import javazoom.jl.player.Player;
import model.Matriz;
import model.Pokemon;
import model.Ponto;

/**
 *
 * @author Argeu
 */
public class OpenGLEvent implements GLEventListener{
    private final InputEvent key;
    private final Ponto p1;
    private final Ponto p2;
    private Texture texturaCubo;
    private Pokemon diglett;
    private Pokemon mew;
    private float luzX;
    private float luzY;
    int dirX = 1;
    int dirY = 1;

    public OpenGLEvent(InputEvent key, Ponto p1, Ponto p2){
        this.key = key;
        this.p1 = p1;
        this.p2 = p2;
        this.key.centroX = (p1.x + p2.x) / 2;
        this.key.centroY = (p1.y + p2.y) / 2;
        this.key.centroZ = (p1.z + p2.z) / 2;
        this.luzX = 0;
        this.luzY = 0;
    }
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.setSwapInterval(1);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
        GLU glu = new GLU();
        carregarTextura(gl, glu);
        newGame(gl);
        new Thread(){public void run(){
            try{
                while(true){
                    InputStream is = getClass().getClassLoader().getResourceAsStream("sound"+File.separator+"battle_theme.mp3");
                    BufferedInputStream bis = new BufferedInputStream(is);
                    final Player mp3Player;
                    mp3Player = new Player(bis);
                    mp3Player.play();
                    mp3Player.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }}.start();
    }

    @SuppressWarnings("empty-statement")
    public void display(GLAutoDrawable drawable) {
       GL gl = drawable.getGL();
       GLU glu = new GLU();
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
       gl.glEnable(GL.GL_TEXTURE_2D);
       
       //Controle para ligar/Desligar as luzes no botão O, pois deixa muito lento.
       if(key.isLightOn){
            gl.glEnable(GL.GL_LIGHTING);
            gl.glEnable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHT0);
            addLight(gl);
       }else{
           gl.glDisable(GL.GL_LIGHTING);
       }
       
       if(key.isFirstPerson){
           if(key.diglett.isFirstPerson){
               glu.gluLookAt(diglett.eyeX, diglett.eyeY, diglett.eyeZ, diglett.centroX, diglett.centroY, diglett.centroZ, diglett.upx, diglett.upy, key.upz);
           }else if(key.mew.isFirstPerson){
               glu.gluLookAt(mew.eyeX, mew.eyeY, mew.eyeZ, mew.centroX, mew.centroY, mew.centroZ, mew.upx, mew.upy, key.upz);
           }
       }else{
           glu.gluLookAt(key.eyeX, key.eyeY, key.eyeZ, key.centroX, key.centroY, key.centroZ, key.upx, key.upy, key.upz);
       }
       
       if(key.isNewGame){
           key.isNewGame = false;
           newGame(gl);
       }
       
       drawCube(gl);
       
       if(diglett.isAttacking){
    	   mew.isDamaged = diglett.atacar(gl, glu);
       }
       if(mew.isAttacking){
    	   diglett.isDamaged = mew.atacar(gl, glu);
       }
       
       if(diglett.isDamaged){
    	  diglett.damage(gl);
       }else{
    	   gl.glDisable(GL.GL_COLOR_MATERIAL);
    	   gl.glColor3f(1f, 1f, 1f);
       }
       diglett.inserir(gl);
       if(mew.isDamaged){
    	   mew.damage(gl);
       }else{
    	   gl.glDisable(GL.GL_COLOR_MATERIAL);
    	   gl.glColor3f(1f, 1f, 1f);
       }
       mew.inserir(gl);
       try {
		Thread.sleep(100);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}

       if(mew.isDead){
           GLUT glut = new GLUT();
           gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT,	FloatBuffer.wrap(new float[] { 0.8f, 0.8f, 0.8f, 0.0f }));
           gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float[] { .8f, .8f, .8f, 0.0f }));
           gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] { .8f, .8f, .8f, 0.0f }));
           gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float[] { 0.0f, 1f, 1.0f, 0.5f }));
           gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
           gl.glColor3f (1.0f, 1.0f, 1.0f);  // Set text e.color to black
           gl.glRasterPos3i(-3, -16, 20); // set position
           glut.glutBitmapString(5, "Diglett Wins");

       }else if (diglett.isDead){
           GLUT glut = new GLUT();
           gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT,	FloatBuffer.wrap(new float[] { 0.8f, 0.8f, 0.8f, 0.0f }));
           gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float[] { .8f, .8f, .8f, 0.0f }));
           gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] { .8f, .8f, .8f, 0.0f }));
           gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float[] { 0.0f, 1f, 1.0f, 0.5f }));
           gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
           gl.glColor3f (1.0f, 1.0f, 1.0f);  // Set text e.color to black
           gl.glRasterPos3i(-3, -16, 20); // set position
           glut.glutBitmapString(5, "Mew Wins");
       }
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
        
        gl.glDisable(GL.GL_TEXTURE_2D);
        
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
        texturaCubo.enable();
        texturaCubo.bind();
        gl.glBegin(GL.GL_QUADS);
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3f(topRightCima.x, topRightCima.y, topRightCima.z);   // Top Right
            gl.glTexCoord2d(0, 1);
            gl.glVertex3f(topLeftCima.x, topLeftCima.y, topLeftCima.z);  // Top Left
            gl.glTexCoord2d(0, 0);
            gl.glVertex3f(topLeftBaixo.x, topLeftBaixo.y, topLeftBaixo.z); // Bottom Left
            gl.glTexCoord2d(1, 0);
            gl.glVertex3f(topRightBaixo.x, topRightBaixo.y, topRightBaixo.z);  // Bottom Right
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
        GLU glu = new GLU();
        if (height <= 0) { // avoid a divide by zero error!
            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu.gluPerspective(35.0f, h, 1.0, 500.0);
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        
    }

    private void carregarTextura(GL gl, GLU glu) {

        try {
            BufferedImage im = ImageIO.read(new File("src/img/stadium.jpg"));
            this.texturaCubo = TextureIO.newTexture(im, true);
            this.texturaCubo.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
            this.texturaCubo.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
            this.texturaCubo.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
            this.texturaCubo.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
        } catch (Exception ex) {
            Logger.getLogger(OpenGLEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addLight(GL gl){
        if(luzX > 10 && dirX == 1){
            dirX = -1;
        }
        if(luzX < -10  && dirX == -1){
            dirX = 1;
        }
        luzX += 2.5 * dirX;
        
        if(luzY > 10 &&  dirY == 1){
            dirY = -1;
        }
        if(luzY < -10 && dirY == -1){
            dirY = 1;
        }
        luzY += 1.6 * dirY;
        
        float [] valueArray =  {luzX, luzY, key.luzZ, 0.4f};
        FloatBuffer values = FloatBuffer.wrap(valueArray);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, values);

        gl.glLighti(GL.GL_LIGHT0, GL.GL_SPOT_CUTOFF, 10);
        
        gl.glLighti(GL.GL_LIGHT0, GL.GL_SPOT_EXPONENT, 25);
        
        valueArray = new float [] {-luzX, -luzY, -key.luzZ, .0f};
        
        
        values = FloatBuffer.wrap(valueArray);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPOT_DIRECTION, values);
        
        valueArray = new float [] {10f, 10f, 1f, 0.6f};
        values = FloatBuffer.wrap(valueArray);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, values);
        
        valueArray = new float [] {1f, 1f, 1f, 0f};
        values = FloatBuffer.wrap(valueArray);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, values);

        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, FloatBuffer.wrap(new float [] {0.3f, 0.3f, 0.3f, 0.2f}));
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float [] {0.3f, 0.5f, 0.5f, 0.6f}));
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float [] {0.1f, 1.0f, 0.4f, 0.9f}));
    }

    private void newGame(GL gl) {
        Matriz mapa = new Matriz();
        try {
            diglett = new Pokemon(gl, mapa, "src"+File.separator+"obj"+File.separator+"diglett"+File.separator+"Diglett", 0, 9, 'S', 1.5f);
            mew = new Pokemon(gl, mapa, "src"+File.separator+"obj"+File.separator+"mew"+File.separator+"BR_Mew", 0, -12, 'N', 2.0f);
        } catch (Exception ex) {
            Logger.getLogger(OpenGLEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.key.diglett = diglett;
        this.key.mew = mew;
    }
}
