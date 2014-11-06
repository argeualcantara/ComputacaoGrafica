/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import objutils.ObjLoader;
import objutils.ObjModel;

/**
 *
 * @author Argeu
 */
public class Personagem {
    ObjModel obj;
    Texture tex;
    int posX;
    int posY;
    int posZ;
    public int centroX;
    public int centroY;
    public int centroZ;
    char dir = 'N';
//    int count = 0;
    private int anguloRotacao;
    public int eyeX;
    public int eyeY;
    public int eyeZ;
    public int upx;
    public int upy;
    //Matriz que armazena as posições ocupadas com true
    public boolean [][] ocupado;
    public Personagem(GL gl) throws IOException {
        super();
        this.ocupado = new boolean [27][27];
        this.obj = ObjLoader.loadObj("object/F-5E Tiger II (05).obj", gl);
        BufferedImage im = ImageIO.read(new File("src/object/F-5E Tiger II (05).bmp"));
        this.tex = TextureIO.newTexture(im, true);
        this.tex.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        this.tex.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        posX = 3;
        posY = -12;
        posZ = 4;
        
        //Marca a posição inicial do ator como ocupada
        ocupado[posX+(ocupado.length/2)] [posY+(ocupado.length/2)] = true;
        
        eyeX = posX;
        eyeY = posY-2;
        eyeZ = posZ+3;
        centroX = eyeX;
        centroY = eyeY+5;
        centroZ = 6;
        upx = 0;
        upy = 1;
    }

    public void inserir(GL gl) {
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glColor3f(1f, 1f, 1f);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, FloatBuffer.wrap(new float [] {0.5f,0.5f,0.5f,0.0f}));
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float [] {.5f,.5f,.5f,0.0f}));
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, FloatBuffer.wrap(new float [] {.5f,.5f,.5f,0.0f}));
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float [] {1f,1f,1.0f,1.0f}));

        gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
        tex.enable();
        tex.bind();

        gl.glPushMatrix();
            gl.glTranslatef(posX, posY, posZ);
            gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(180.0f , 0.0f, 1.0f, 0.0f);
            gl.glRotatef(anguloRotacao, 0, 1, 0);
            gl.glScalef(0.002f, 0.002f, 0.002f);
            obj.render(gl);
        gl.glPopMatrix();
        
    }
    
    public void andar(){
        //Os IF's verificam se a posição a ser ocupada pelo ator está ocupada por outro objeto.
        //Posição ocupada dos objetos é definida no metodo init da classe OpenGLEvent
        //Podendo andar a posição atual é marcada como desocupada e a próxima como ocupada.
        //Os magic numbers -12 e 12 são os limites do tabuleiro.
        switch(dir){
            case 'N':
                if(posY+3 <= 12 && !ocupado[posX+(ocupado.length/2)] [posY+3+(ocupado.length/2)]){
                    ocupado[posX+(ocupado.length/2)] [posY+(ocupado.length/2)] = false;
                    ocupado[posX+(ocupado.length/2)] [posY+3+(ocupado.length/2)] = true;
                    posY += 3;
                    eyeX = posX;
                    eyeY = posY-2;
                    centroX = eyeX;
                    centroY = eyeY+5;
                    upx = 0;
                    upy = 1;
                }else{
                    if(posY+3 > 12){
                        System.out.println("Fora do tabuleiro.");
                    }else{
                        System.out.println("Posição ocupada.");
                    }
                }
                break;
            case 'S':
                if(posY-3 >= -12 && !ocupado[posX+(ocupado.length/2)] [posY-3+(ocupado.length/2)]){
                    ocupado[posX+(ocupado.length/2)] [posY+(ocupado.length/2)] = false;
                    ocupado[posX+(ocupado.length/2)] [posY-3+(ocupado.length/2)] = true;
                    posY -= 3;
                    eyeX = posX;
                    eyeY = posY+2;
                    centroX = eyeX;
                    centroY = eyeY-5;
                    upx = 0;
                    upy = 1;
                }else{
                    if(posY-3 < -12){
                        System.out.println("Fora do tabuleiro.");
                    }else{
                        System.out.println("Posição ocupada.");
                    }
                }
                
                break;
            case 'L':
                if(posX-3 >= -12 && !ocupado[posX-3+(ocupado.length/2)] [posY+(ocupado.length/2)]){
                    ocupado[posX+(ocupado.length/2)] [posY+(ocupado.length/2)] = false;
                    ocupado[posX-3+(ocupado.length/2)] [posY+(ocupado.length/2)] = true;
                    posX -= 3;
                    eyeX = posX+2;
                    eyeY = posY;
                    centroX = posX - 5;
                    centroY = posY;
                    upx = 1;
                    upy = 0;
                }else{
                    if(posX-3 < -12){
                        System.out.println("Fora do tabuleiro.");
                    }else{
                        System.out.println("Posição ocupada.");
                    }
                }
                break;
            case 'O':
                if(posX+3 <= 12 && !ocupado[posX+3+(ocupado.length/2)] [posY+(ocupado.length/2)]){
                    ocupado[posX+(ocupado.length/2)] [posY+(ocupado.length/2)] = false;
                    ocupado[posX+3+(ocupado.length/2)] [posY+(ocupado.length/2)] = true;
                    posX += 3;
                    eyeX = posX - 2;
                    eyeY = posY;
                    centroX = posX + 5;
                    centroY = posY;
                    upx = 1;
                    upy = 0;
                }else{
                    if(posX+3 > 12){
                        System.out.println("Fora do tabuleiro.");
                    }else{
                        System.out.println("Posição ocupada.");
                    }
                }
                break;
        }
        
        if(posY > 12){
            posY = 12;
        }
        if(posY < -12){
            posY = -12;
        }
        
        if(posX > 12){
            posX = 12;
        }
        if(posX < -12){
            posX = -12;
        }
        
    }
    
    public void virarDireita(){
        anguloRotacao += -90;
        switch(dir){
            case 'N':
                dir = 'O';
                eyeX = posX - 2;
                eyeY = posY;
                centroX = posX + 5;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
            case 'S':
                dir = 'L';
                eyeX = posX+2;
                eyeY = posY;
                centroX = posX - 5;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
            case 'O':
                dir = 'S';
                eyeX = posX;
                eyeY = posY+2;
                centroX = eyeX;
                centroY = eyeY-5;
                upx = 0;
                upy = -1;
                break;
            case 'L':
                dir = 'N';
                eyeX = posX;
                eyeY = posY-2;
                centroX = eyeX;
                centroY = eyeY+5;
                upx = 0;
                upy = 1;
                break;
        }
    
    }
    
    public void virarEsquerda(){
        anguloRotacao += 90;
        switch(dir){
            case 'N':
                dir = 'L';
                eyeX = posX+2;
                eyeY = posY;
                centroX = posX - 5;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
            case 'S':
                dir = 'O';
                eyeX = posX - 2;
                eyeY = posY;
                centroX = posX + 5;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
            case 'O':
                dir = 'N';
                eyeX = posX;
                eyeY = posY-2;
                centroX = eyeX;
                centroY = eyeY+5;
                upx = 0;
                upy = 1;
                break;
            case 'L':
                dir = 'S';
                eyeX = posX;
                eyeY = posY+2;
                centroX = eyeX;
                centroY = eyeY-5;
                upx = 0;
                upy = -1;
                break;
        }
    }

    public void sobe() {
        posZ += 1;
        if(posZ > 10){
            posZ = 10;
        }
    }

    public void desce() {
        posZ -= 1;
        if(posZ < 4){
            posZ = 4;
        }
    }
    
    public void insereDummy(GL gl, int x, int y, char dir){
            tex.enable();
            tex.bind();
            gl.glColor3f(0.2f,1f,0.0f);
            gl.glPushMatrix();
                gl.glTranslatef(x, y, 4);
                gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(180.0f , 0.0f, 1.0f, 0.0f);
                switch(dir){
                    case 'N': break;
                    case 'S': gl.glRotatef(180.0f , 0.0f, 1.0f, 0.0f); break;
                    case 'L': gl.glRotatef(-90.0f , 0.0f, 1.0f, 0.0f); break;
                    case 'O': gl.glRotatef(90.0f , 0.0f, 1.0f, 0.0f); break;
                }
                gl.glScalef(0.002f, 0.002f, 0.002f);
                gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, FloatBuffer.wrap(new float [] {0.5f,0.5f,0.0f,0.2f}));
                gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float [] {0.5f,0.5f,0.0f,0.6f}));
                gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, FloatBuffer.wrap(new float [] {1f,1f,0.9f,0.9f}));
                gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float [] {0.4f,1f,0.0f,0.8f}));
                
                gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
                gl.glEnable(GL.GL_COLOR_MATERIAL);
                obj.render(gl);
            gl.glPopMatrix();
            
    }
}
