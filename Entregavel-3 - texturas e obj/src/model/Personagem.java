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
    char dir = 'N';
    private int anguloRotacao;
    public Personagem(GL gl) throws IOException {
        super();
        this.obj = ObjLoader.loadObj("object/F-5E Tiger II (05).obj", gl);
        BufferedImage im = ImageIO.read(new File("src/object/F-5E Tiger II (05).bmp"));
        this.tex = TextureIO.newTexture(im, false);
        this.tex.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        this.tex.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        posX = 1;
        posY = -12;
    }
     
    public void inserir(GL gl) {
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1f, 1f, 1f);
        tex.enable();
        tex.bind();
        
        gl.glPushMatrix();
            gl.glTranslatef(posX, posY, 4);
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
                break;
            case 'S':
                posY -= 1;
                break;
            case 'L':
                posX -= 1;
                break;
            case 'O':
                posX += 1;
                break;
        }

        System.out.println(posX);
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
                break;
            case 'S':
                dir = 'L';
                break;
            case 'O':
                dir = 'S';
                break;
            case 'L':
                dir = 'N';
                break;
        }
    
    }
    
    public void virarEsquerda(){
        anguloRotacao += 90;
        switch(dir){
            case 'N':
                dir = 'L';
                break;
            case 'S':
                dir = 'O';
                break;
            case 'O':
                dir = 'N';
                break;
            case 'L':
                dir = 'S';
                break;
        }
    }
}
