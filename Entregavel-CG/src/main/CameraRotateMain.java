package main;

import com.sun.opengl.util.Animator;
import interfaces.OpenGLEvent;
import interfaces.InputEvent;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCanvas;
import model.Ponto;

/**
 *
 * @author Argeu
 */
public class CameraRotateMain{
    private static final int L = 30;
    private static GLCanvas canvas;
    
    public static void main(String[] args) {
        Frame frame = new Frame("Pokemon Stadium");
        canvas = new GLCanvas();
        
        //Setando os pontos para o cento ficar no 0,0,0
        Ponto p1 = new Ponto(-L/2, -L/2, (int) (-L/2 * 0.2));
        Ponto p2 = new Ponto(L/2, L/2, (int) (L/2 * 0.2));

        InputEvent inputListener = new InputEvent(-45);

        OpenGLEvent glListener = new OpenGLEvent(inputListener, p1, p2);
        
        canvas.addKeyListener(inputListener);
        canvas.addGLEventListener(glListener);
        canvas.requestFocus();
        
        frame.add(canvas);
        frame.setSize(640, 480);
        
        final Animator animator = new Animator(canvas);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
    }
}
