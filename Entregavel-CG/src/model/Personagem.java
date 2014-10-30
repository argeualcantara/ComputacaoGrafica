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
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import obj.ObjLoader;
import obj.ObjModel;

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
    int count = 0;
    private int anguloRotacao;
    public int eyeX;
    public int eyeY;
    public int eyeZ;
    public int upx;
    public int upy;
    public Personagem(GL gl) throws IOException {
        super();
        this.obj = ObjLoader.loadObj("object/F-5E Tiger II (05).obj", gl);
        BufferedImage im = ImageIO.read(new File("src/object/F-5E Tiger II (05).bmp"));
        this.tex = TextureIO.newTexture(im, true);
        this.tex.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        this.tex.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        posX = 1;
        posY = -12;
        posZ = 4;
        
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
        gl.glColor3f(1f, 1f, 1f);
        tex.enable();
        tex.bind();

        gl.glPushMatrix();
            gl.glTranslatef(posX, posY, posZ);
            gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(180.0f , 0.0f, 1.0f, 0.0f);
            gl.glRotatef(anguloRotacao, 0, 1, 0);
            gl.glScalef(0.004f, 0.004f, 0.004f);
            obj.render(gl);
        gl.glPopMatrix();
        
    }
    
    public void andar(){
        switch(dir){
            case 'N':
                posY += 1;
                eyeX = posX;
                eyeY = posY-2;
                centroX = eyeX;
                centroY = eyeY+5;
                upx = 0;
                upy = 1;
                break;
            case 'S':
                posY -= 1;
                eyeX = posX;
                eyeY = posY+2;
                centroX = eyeX;
                centroY = eyeY-5;
                upx = 0;
                upy = 1;
                break;
            case 'L':
                posX -= 1;
                eyeX = posX+2;
                eyeY = posY;
                centroX = posX - 5;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
            case 'O':
                posX += 1;
                eyeX = posX - 2;
                eyeY = posY;
                centroX = posX + 5;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
        }
        
        if(posY > 11){
            posY = 11;
        }
        if(posY < -12){
            posY = -12;
        }
        
        if(posX > 11){
            posX = 11;
        }
        if(posX < -11){
            posX = -11;
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
    
    public void insereDummy(GL gl, int x, int y){
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glColor3f(1f, 1f, 1f);
            tex.enable();
            tex.bind();

            gl.glPushMatrix();
                gl.glTranslatef(x, y, 4);
                gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(180.0f , 0.0f, 1.0f, 0.0f);
                gl.glScalef(0.004f, 0.004f, 0.004f);
                obj.render(gl);
            gl.glPopMatrix();
            
    }
}
