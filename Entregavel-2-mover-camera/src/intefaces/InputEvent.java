package intefaces;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Argeu
 */
public class InputEvent implements KeyListener{
    double eyeX = 0;
    double eyeY = 0;
    double eyeZ = 40;
    double upx = 0;
    double upy = 1;
    double upz = 0;
    
    double centroX;
    double centroY;
    double centroZ;
    
    double eyeX_INICIAL;
    double eyeY_INICIAL;
    double eyeZ_INICIAL;
    double upx_INICIAL;
    double upy_INICIAL;
    double upz_INICIAL;
    
    /**
     * Angulos dos respectivos eixos
     */
    private double thx = 0;
    private double thy = 0;
    private double thz = 0;
    
    /**
     * Variáveis contadoras para limitar os angulos entre 0 e 180
     */
    private double thxc = 0;
    private double thyc = 0;
    private double thzc = 0;
    
    public boolean isWireFrame = false;

    /**
     * Posições iniciais dos vetores eye e up salvas para o botão R - reset
     */
    public InputEvent (){
        eyeX_INICIAL = eyeX;
        eyeY_INICIAL = eyeY;
        eyeZ_INICIAL = eyeZ;
        
        upx_INICIAL = upx;
        upy_INICIAL = upy;
        upz_INICIAL = upz;
    }
    public void keyTyped(KeyEvent e) {
        
    }

    /**
     * Para cada tecla de mover a camera são aplicadas as matrizes de rotação
     * na posição da camera e do vetor up,
     * por isso o centro do objeto está na posição 0,0,0.
     * @param e 
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        double angulo = 0.01745 * 5;
        double ex,ey,ez;
        switch(key){
            case KeyEvent.VK_LEFT:
                if(thyc < (0.01745 * 180)){
                    thy = angulo;
                    thyc += angulo;
                    ex = eyeX;
                    ez = eyeZ;
                    eyeX = ex * Math.cos(thy) - ez * Math.sin(thy);
                    eyeZ = ex * Math.sin(thy) + ez * Math.cos(thy);

                    ex = upx;
                    ez = upz;
                    upx = ex * Math.cos(thy) - ez * Math.sin(thy);
                    upz = ex * Math.sin(thy) + ez * Math.cos(thy);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if(thyc > 0){
                    thy = -angulo;
                    thyc -= angulo;
                    ex = eyeX;
                    ez = eyeZ;
                    eyeX = ex * Math.cos(thy) - ez * Math.sin(thy);
                    eyeZ = ex * Math.sin(thy) + ez * Math.cos(thy);

                    ex = upx;
                    ez = upz;
                    upx = ex * Math.cos(thy) - ez * Math.sin(thy);
                    upz = ex * Math.sin(thy) + ez * Math.cos(thy);
                }
                break;
            case KeyEvent.VK_DOWN:
                if(thxc > 0){
                    thx = -angulo;
                    thxc -= angulo;
                    ey = eyeY;
                    ez = eyeZ;
                    eyeY = ey * Math.cos(thx) + ez * Math.sin(thx);
                    eyeZ = ey * -Math.sin(thx) + ez * Math.cos(thx);

                    ey = upy;
                    ez = upz;
                    upy = ey * Math.cos(thx) + ez * Math.sin(thx);
                    upz = ey * -Math.sin(thx) + ez * Math.cos(thx);
                }
                break;
            case KeyEvent.VK_UP:
                if( thxc < (0.01745 * 180)){
                    thx = angulo;
                    thxc += angulo;
                    ey = eyeY;
                    ez = eyeZ;
                    eyeY = ey * Math.cos(thx) + ez * Math.sin(thx);
                    eyeZ = ey * -Math.sin(thx) + ez * Math.cos(thx);

                    ey = upy;
                    ez = upz;
                    upy = ey * Math.cos(thx) + ez * Math.sin(thx);
                    upz = ey * -Math.sin(thx) + ez * Math.cos(thx);
                }
                break;    
            case KeyEvent.VK_PAGE_UP:
                thz = angulo;
                thzc += angulo;
                ex = eyeX;
                ey = eyeY;
                eyeX = ex * Math.cos(thz) + ey * Math.sin(thz);
                eyeY = ex * -Math.sin(thz) + ey * Math.cos(thz);
                
                ex = upx;
                ey = upy;
                upx = ex * Math.cos(thz) + ey * Math.sin(thz);
                upy = ex * -Math.sin(thz) + ey * Math.cos(thz);
                break;
                
            case KeyEvent.VK_PAGE_DOWN:
                thz = -angulo;
                thzc -= angulo;
                ex = eyeX;
                ey = eyeY;
                eyeX = ex * Math.cos(thz) + ey * Math.sin(thz);
                eyeY = ex * -Math.sin(thz) + ey * Math.cos(thz);
                
                ex = upx;
                ey = upy;
                upx = ex * Math.cos(thz) + ey * Math.sin(thz);
                upy = ex * -Math.sin(thz) + ey * Math.cos(thz);
                break;    
                
            case KeyEvent.VK_L:
                isWireFrame = !isWireFrame;
                break;
            case KeyEvent.VK_R:
                upx = upx_INICIAL;
                upy = upy_INICIAL;
                upz = upz_INICIAL;
                
                eyeX = eyeX_INICIAL;
                eyeY = eyeY_INICIAL;
                eyeZ = eyeZ_INICIAL;
                
                thxc = 0;
                thyc = 0;
                thzc = 0;
                break;
        }
        
    }

    public void keyReleased(KeyEvent e) {
        
    }
    
}
