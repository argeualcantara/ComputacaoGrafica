package intefaces;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import model.Personagem;
import model.Pokemon;

/**
 *
 * @author Argeu
 */
public class InputEvent implements KeyListener{
    double eyeX = 0;
    double eyeY = 0;
    double eyeZ = 55;
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
     * Vari�veis contadoras para limitar os angulos entre 0 e 180
     */
    private double thxc = 0;
    private double thxc_INICIAL = 0;
    private double thyc = 0;
    private double thzc = 0;
    
    public boolean isWireFrame = false;
    
    public Pokemon diglett;
    public Pokemon mew;
    float luzZ;
    public boolean fp;
    public boolean isLightOn = true;

    /**
     * Posi��es iniciais dos vetores eye e up salvas para o bot�o R - reset
     */
    public InputEvent (){
        eyeX_INICIAL = eyeX;
        eyeY_INICIAL = eyeY;
        eyeZ_INICIAL = eyeZ;
        
        upx_INICIAL = upx;
        upy_INICIAL = upy;
        upz_INICIAL = upz;
    }
    public InputEvent(int angle){
        thxc = Math.toRadians(angle);
        thxc_INICIAL = thxc;
        double ey = eyeY;
        double ez = eyeZ;
        eyeY = ey * Math.cos(thxc) + ez * Math.sin(thxc);
        eyeZ = ey * -Math.sin(thxc) + ez * Math.cos(thxc);

        ey = upy;
        ez = upz;
        upy = ey * Math.cos(thxc) + ez * Math.sin(thxc);
        upz = ey * -Math.sin(thxc) + ez * Math.cos(thxc);
        
        eyeX_INICIAL = eyeX;
        eyeY_INICIAL = eyeY;
        eyeZ_INICIAL = eyeZ;
        
        upx_INICIAL = upx;
        upy_INICIAL = upy;
        upz_INICIAL = upz;
        
        luzZ = 20;
        fp = false;
        
    }
    public void keyTyped(KeyEvent e) {
        
    }

    /**
     * Para cada tecla de mover a camera s�o aplicadas as matrizes de rota��o
     * na posi��o da camera e do vetor up,
     * por isso o centro do objeto est� na posi��o 0,0,0.
     * @param e 
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        double angulo = Math.toRadians(1) * 5;
        double ex,ey,ez;
        switch(key){
            case KeyEvent.VK_A:
                if(thyc < (Math.toRadians(1) * 179)){
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
            case KeyEvent.VK_D:
                if(thyc > angulo){
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
            case KeyEvent.VK_S:
                if(thxc > -(Math.toRadians(1) * 179)){
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
            case KeyEvent.VK_W:
                if( thxc < -angulo){
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
            case KeyEvent.VK_E:
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
                
            case KeyEvent.VK_Q:
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
            case KeyEvent.VK_I:
                eyeX = 0;
                eyeY = 0;
                eyeZ = 55;
                upx = 0;
                upy = 1;
                upz = 0;
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
                
                thxc = thxc_INICIAL;
                thyc = 0;
                thzc = 0;
                break;
                
            case KeyEvent.VK_UP:
                diglett.andar();
                break;
            case KeyEvent.VK_RIGHT:
                diglett.virarDireita();
                break;
            case KeyEvent.VK_LEFT:
                diglett.virarEsquerda();
                break;
            case KeyEvent.VK_Y:
                mew.andar();
                break;
            case KeyEvent.VK_J:
                mew.virarDireita();
                break;
            case KeyEvent.VK_G:
                mew.virarEsquerda();
                break;
            case KeyEvent.VK_PAGE_UP:
                diglett.sobe();
                break;
            case KeyEvent.VK_PAGE_DOWN:
                diglett.desce();
                break;
            case KeyEvent.VK_1: luzZ = 10; break;
            case KeyEvent.VK_2: luzZ = 15; break;
            case KeyEvent.VK_3: luzZ = 20; break;
            case KeyEvent.VK_4: luzZ = 25; break;
            case KeyEvent.VK_8:
            case KeyEvent.VK_ASTERISK:
            case KeyEvent.VK_MULTIPLY: this.fp = !fp;break;
            case KeyEvent.VK_O: this.isLightOn = !isLightOn;break;
            case KeyEvent.VK_SPACE: diglett.isAttacking = true;break;
            case KeyEvent.VK_CONTROL: mew.isAttacking = true;break;
        }
        
    }

    public void keyReleased(KeyEvent e) {
        
    }
    
}
