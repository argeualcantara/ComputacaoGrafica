/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import objutils.OBJModelNew;

import com.sun.opengl.util.texture.Texture;

/**
 *
 * @author Argeu
 */
public class Pokemon {
    Texture tex1;
    Texture tex2;
    int posX;
    int posY;
    int posZ;
    public int centroX;
    public int centroY;
    public int centroZ;
    char dir;
    private int anguloRotacao;
    public int eyeX;
    public int eyeY;
    public int eyeZ;
    public int upx;
    public int upy;
    public char PROG = 'A';
    public OBJModelNew loader;
    int offset;
    //Matriz que armazena as posições ocupadas pelo tipo de objeto
    public Matriz mapa;
    public boolean isDamaged;
    public boolean isAttacking;
    public boolean isDead = false;
    private static final int CONSTANTE_CENTRO_FP = 3;
    private int lifesLeft = 3;
    public boolean isFirstPerson = false;
    private String basePath;
    public Pokemon(GL gl, Matriz mapa, String path, int iniX, int iniY, char dir, float scale) throws IOException {
        super();
        this.mapa = mapa;
        this.offset = mapa.ocupado.length/2;
        basePath = path;
        //Corrigir objetos importados de outro programa com o Z para cima ao inves do Y
        if(path.contains("BR_")){
            PROG = 'B';
        }
        this.loader = new OBJModelNew(path, scale, gl, true);
        
       	this.dir = dir;
	posX = iniX;
        posY = iniY;
        posZ = 4;
        
        //Marca a posi��o inicial do ator como ocupada
        mapa.ocupado[posX+offset] [posY+offset] = Matriz.POKEMON;
        
        switch(this.dir){
            case'N':centroX = eyeX; centroY = eyeY+CONSTANTE_CENTRO_FP;break;
            case'S':anguloRotacao = 180; centroX = eyeX;centroY = eyeY-CONSTANTE_CENTRO_FP;break;
            case'L':anguloRotacao = -90; centroX = eyeX +CONSTANTE_CENTRO_FP;centroY = eyeY;break;
            case'O':anguloRotacao = 90; centroX = eyeX -CONSTANTE_CENTRO_FP;centroY = eyeY;break;
        }
        eyeX = posX;
        eyeY = posY;
        eyeZ = posZ;
        
        centroZ = posZ;
        upx = 0;
        upy = 1;
    }

    public void inserir(GL gl) {
        if(!isDead){
            gl.glPushMatrix();
                gl.glTranslatef(posX, posY, posZ);
                gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(180.0f , 0.0f, 1.0f, 0.0f);
                int upx = 0;
                int upy = 1;
                int upz = 0;
                if(PROG == 'B'){
                    gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
                    upy = 0;
                    upz = 1;
                }
                gl.glRotatef(anguloRotacao, upx, upy, upz);
                loader.draw(gl);
            gl.glPopMatrix();
            loadLifeBar(gl);
        }
    }
    
    public void andar(){
        //Os IF's verificam se a posição a ser ocupada pelo ator está ocupada por outro objeto.
        //Posição ocupada dos objetos é definida no metodo init da classe OpenGLEvent
        //Podendo andar a posição atual é marcada como desocupada e a próxima como ocupada.
        //Os magic numbers -12 e 12 são os limites do tabuleiro.
        if(!isDead){
            switch(dir){
                case 'N':
                    if(posY+3 <= 12 && mapa.ocupado[posX+offset] [posY+3+offset] != Matriz.POKEMON){
                        mapa.ocupado[posX+offset] [posY+offset] = ' ';
                        posY += 3;
                        mapa.ocupado[posX+offset] [posY+offset] = Matriz.POKEMON;
                        eyeX = posX;
                        eyeY = posY;
                        centroX = eyeX;
                        centroY = eyeY+CONSTANTE_CENTRO_FP;
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
                    if(posY-3 >= -12 && mapa.ocupado[posX+offset] [posY-3+offset] != Matriz.POKEMON){
                        mapa.ocupado[posX+offset] [posY+offset] = ' ';
                        posY -= 3;
                        mapa.ocupado[posX+offset] [posY+offset] = Matriz.POKEMON;
                        eyeX = posX;
                        eyeY = posY;
                        centroX = eyeX;
                        centroY = eyeY-CONSTANTE_CENTRO_FP;
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
                    if(posX-3 >= -12 && mapa.ocupado[posX-3+offset] [posY+offset] != Matriz.POKEMON){
                        mapa.ocupado[posX+offset] [posY+offset] = ' ';
                        posX -= 3;
                        mapa.ocupado[posX+offset] [posY+offset] = Matriz.POKEMON;
                        eyeX = posX;
                        eyeY = posY;
                        centroX = posX - CONSTANTE_CENTRO_FP;
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
                    if(posX+3 <= 12 && mapa.ocupado[posX+3+offset] [posY+offset] != Matriz.POKEMON){
                        mapa.ocupado[posX+offset] [posY+offset] = ' ';
                        posX += 3;
                        mapa.ocupado[posX+offset] [posY+offset] = Matriz.POKEMON;
                        eyeX = posX;
                        eyeY = posY;
                        centroX = posX + CONSTANTE_CENTRO_FP;
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
        
    }
    
    public void virarDireita(){
        if(!isDead){
            int valorAngulo = -90;
            if(PROG == 'B'){
                    valorAngulo *= -1;
            }
            anguloRotacao += valorAngulo;
            switch(dir){
                case 'N':
                    dir = 'O';
                    eyeX = posX;
                    eyeY = posY;
                    centroX = eyeX + CONSTANTE_CENTRO_FP;
                    centroY = eyeY;
                    upx = 1;
                    upy = 0;
                    break;
                case 'S':
                    dir = 'L';
                    eyeX = posX;
                    eyeY = posY;
                    centroX = eyeX - CONSTANTE_CENTRO_FP;
                    centroY = eyeY;
                    upx = 1;
                    upy = 0;
                    break;
                case 'O':
                    dir = 'S';
                    eyeX = posX;
                    eyeY = posY;
                    centroX = eyeX;
                    centroY = eyeY-CONSTANTE_CENTRO_FP;
                    upx = 0;
                    upy = 1;
                    break;
                case 'L':
                    dir = 'N';
                    eyeX = posX;
                    eyeY = posY;
                    centroX = eyeX;
                    centroY = eyeY+CONSTANTE_CENTRO_FP;
                    upx = 0;
                    upy = 1;
                    break;
            }
        }
    
    }
    
    public void virarEsquerda(){
        if(!isDead){
            int valorAngulo = 90;
            if(PROG == 'B'){
                    valorAngulo *= -1;
            }
            anguloRotacao += valorAngulo;
            switch(dir){
                case 'N':
                    dir = 'L';
                    eyeX = posX;
                    eyeY = posY;
                    centroX = eyeX - CONSTANTE_CENTRO_FP;
                    centroY = eyeY;
                    upx = 1;
                    upy = 0;
                    break;
                case 'S':
                    dir = 'O';
                    eyeX = posX;
                    eyeY = posY;
                    centroX = eyeX + CONSTANTE_CENTRO_FP;
                    centroY = eyeY;
                    upx = 1;
                    upy = 0;
                    break;
                case 'O':
                    dir = 'N';
                    eyeX = posX;
                    eyeY = posY;
                    centroX = eyeX;
                    centroY = eyeY+CONSTANTE_CENTRO_FP;
                    upx = 0;
                    upy = 1;
                    break;
                case 'L':
                    dir = 'S';
                    eyeX = posX;
                    eyeY = posY;
                    centroX = eyeX;
                    centroY = eyeY-CONSTANTE_CENTRO_FP;
                    upx = 0;
                    upy = 1;
                    break;
            }
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
    
    public boolean atacar(GL gl, GLU glu){
        if(!isDead){
            this.isAttacking = false;
            boolean acertou = false;
            int posx = posX+offset;
            int posy = posY+offset;
            char dir = this.dir;
            gl.glBegin(GL.GL_LINES);
            gl.glEnable(GL.GL_COLOR_MATERIAL);
            gl.glColor3f(0.0f,0.0f,1.0f); //Blue
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT,	FloatBuffer.wrap(new float[] { 0.0f, 0.0f, 0.8f, 0.0f }));
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float[] { .0f, .0f, .8f, 0.0f }));
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] { .0f, .0f, .8f, 0.0f }));
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float[] { 1f, 1f, 1.0f, 1.0f }));
            gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
            int posFim = 0;
            switch (dir) {
                            case 'N':
                                    posFim = posy;
                                    for (int i = posy+1; i < mapa.ocupado.length; i++) {
                                            if(mapa.ocupado[posx][i] == Matriz.POKEMON){
                                                    acertou = true;
                                                    posFim = i - offset;
                                            }
                                    }
                                    if(!acertou){
                                            posFim = 15;
                                    }
                                    gl.glVertex3i(posX,posY+1,posZ);
                                    gl.glVertex3i(posX,posFim,posZ);
                                    break;
                            case 'S':
                                    posFim = posy;
                                    for (int i = posy-1; i > 0; i--) {
                                            if(mapa.ocupado[posx][i] == Matriz.POKEMON){
                                                    acertou = true;
                                                    posFim = i - offset;
                                            }
                                    }
                                    if(!acertou){
                                            posFim = -15;
                                    }
                                    gl.glVertex3i(posX,posY-1,posZ);
                                    gl.glVertex3i(posX,posFim,posZ);
                                    break;
                            case 'O':
                                    for (int i = posx+1; i < mapa.ocupado.length; i++) {
                                            if(mapa.ocupado[i][posy] == Matriz.POKEMON){
                                                    acertou = true;
                                                    posFim = i - offset;
                                            }
                                    }
                                    if(!acertou){
                                            posFim = 15;
                                    }
                                    gl.glVertex3i(posX,posY,posZ);
                                    gl.glVertex3i(posFim,posY,posZ);
                                    break;
                            case 'L':
                                    for (int i = posx-1; i > 0; i--) {
                                            if(mapa.ocupado[i][posy] == Matriz.POKEMON){
                                                    acertou = true;
                                                    posFim = i - offset;
                                            }
                                    }
                                    if(!acertou){
                                            posFim = -15;
                                    }
                                    gl.glVertex3i(posX,posY,posZ);
                                    gl.glVertex3i(posFim,posY,posZ);
                                    break;
                    }
            gl.glEnd();
            gl.glColor3f(1.0f,1.0f,1.0f);
            try {
                    AudioInputStream soundIn = AudioSystem.getAudioInputStream(new File("src"+File.separator+"sound"+File.separator+"laser.wav"));
                    AudioFormat format = soundIn.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    Clip clip = (Clip)AudioSystem.getLine(info);
                    clip.open(soundIn);
                    clip.start();
                    Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return acertou;
        }else{
            return false;
        }
    }
    
    public void damage(GL gl) {
        if(!isDead){
            this.isDamaged = false;
            this.lifesLeft--;
            gl.glEnable(GL.GL_COLOR_MATERIAL);
            gl.glColor3f(1f, 0f, 0f);
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT,	FloatBuffer.wrap(new float[] { 0.9f, 0.5f, 0.5f, 0.0f }));
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float[] { .5f, .5f, .5f, 0.0f }));
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] { .5f, .5f, .5f, 0.0f }));
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float[] { 1f, 1f, 1.0f, 1.0f }));
            gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
            try {
                AudioInputStream soundIn = AudioSystem.getAudioInputStream(new File("src"+File.separator+"sound"+File.separator+"impact.wav"));
                AudioFormat format = soundIn.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip)AudioSystem.getLine(info);
                clip.open(soundIn);
                clip.start();
                clip.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(lifesLeft == 0){
                this.destruir(gl);
            }
        }
    }

    private void loadLifeBar(GL gl) {
            int posx;
            int posy;
            int posz;
            if(this.PROG == 'B'){
                posx = this.posX;
                posy = this.posY;
                posz = this.posZ;
            }else{
                posx = this.posX;
                posy = this.posY;
                posz = this.posZ;
            }
//            if(this.dir == 'L' || this.dir == 'O'){
//                posX = this.posY;
//                posY = this.posX;
//                posZ = this.posZ;
//            }
            
            if(lifesLeft > 0){
                gl.glEnable(GL.GL_COLOR_MATERIAL);
                gl.glColor3f(1f, 0.1f, 0.1f);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT,	FloatBuffer.wrap(new float[] { 0.9f, 0.3f, 0.3f, 0.0f }));
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float[] { .5f, .0f, .0f, 0.0f }));
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] { .5f, .0f, .0f, 0.0f }));
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float[] { 1f, 0f, 0f, 0.5f }));
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
                drawCube(gl, new Ponto(posx-1.5f, posy, posz+1), new Ponto(posx-0.5f, posy+0.5f, posz+1.5f));          
//                if(this.PROG == 'A'){
//                    gl.glRotatef(90f, 1, 0, 0);
//                }
            }
            if(lifesLeft > 1){
                gl.glEnable(GL.GL_COLOR_MATERIAL);
                
                gl.glColor3f(0.2f, 1f, 0.2f);
                gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT,	FloatBuffer.wrap(new float[] { 0.8f, 0.8f, 0.5f, 0.0f }));
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float[] { .5f, .5f, .0f, 0.0f }));
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] { .5f, .5f, .0f, 0.0f }));
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float[] { 1f, 1f, 0f, 0.5f }));
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
                drawCube(gl, new Ponto(posx-0.5f, posy, posz+1), new Ponto(posx+0.5f, posy+0.5f, posz+1.5f));
//                if(this.PROG == 'A'){
//                    gl.glRotatef(100f, 1, 0, 0);
//                }
                
            }
            if(lifesLeft > 2){
                gl.glEnable(GL.GL_COLOR_MATERIAL);
                gl.glColor3f(0.2f, 1f, 0.2f);
                gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT,	FloatBuffer.wrap(new float[] { 0.0f, 0.8f, 0.0f, 0.0f }));
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, FloatBuffer.wrap(new float[] { .0f, .8f, .0f, 0.0f }));
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] { .0f, .8f, .0f, 0.0f }));
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, FloatBuffer.wrap(new float[] { 0.0f, 1f, 0.0f, 0.5f }));
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
                drawCube(gl, new Ponto(posx+0.5f, posy, posz+1), new Ponto(posx +1.5f, posy+0.5f, posz+1.5f));
//                if(this.PROG == 'A'){
//                    gl.glRotatef(100f, 1, 0, 0);
//                }
            }
        
        
    }
    
    public void drawCube(GL gl, Ponto p1, Ponto p2) {
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
            drawFace(gl, topLeftBaixo, topRightBaixo, botRightBaixo, botLeftBaixo);
        gl.glEnd();
        
        //Face Back
        gl.glBegin(GL.GL_QUADS);
            drawFace(gl, botRightCima, botLeftCima, botLeftBaixo, botRightBaixo);
        gl.glEnd();
        
        //Face Right
        gl.glBegin(GL.GL_QUADS);
            drawFace(gl, topRightCima, botRightCima, botRightBaixo, topRightBaixo);
        gl.glEnd();
        
        //Face Left
        gl.glBegin(GL.GL_QUADS);
            drawFace(gl, botLeftCima, topLeftCima, topLeftBaixo, botLeftBaixo);
        gl.glEnd();
        
        //Face Top
        gl.glBegin(GL.GL_QUADS);
            drawFace(gl, topLeftCima, topRightCima, botRightCima, botLeftCima);
        gl.glEnd();
        
        
        gl.glBegin(GL.GL_QUADS);
            drawFace(gl, topRightCima, topLeftCima, topLeftBaixo, topRightBaixo);
        gl.glEnd();
        
    }
    
    public void drawFace(GL gl, Ponto topRight, Ponto topLeft, Ponto bottomLeft, Ponto bottomRight){
        gl.glVertex3f(topRight.x, topRight.y, topRight.z);   // Top Right
        gl.glVertex3f(topLeft.x, topLeft.y, topLeft.z);  // Top Left
        gl.glVertex3f(bottomLeft.x, bottomLeft.y, bottomLeft.z); // Bottom Left
        gl.glVertex3f(bottomRight.x, bottomRight.y, bottomRight.z);  // Bottom Right
    }

    private void destruir(GL gl) {
        try {
            String audioPath = basePath+".wav";
            AudioInputStream soundIn = AudioSystem.getAudioInputStream(new File(audioPath));
            AudioFormat format = soundIn.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip)AudioSystem.getLine(info);
            clip.open(soundIn);
            clip.start();
            while(clip.isActive()){
                if(!clip.isRunning())
                    clip.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        float posx = posX, posy = posY, posz = posZ;
        for (float i = posZ; i > -1; i-= 0.1f) {
            gl.glPushMatrix();
                gl.glColor3f(0f, 0f, 0f);
                gl.glTranslatef(posx, posy, i);
                gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(180.0f , 0.0f, 1.0f, 0.0f);
                int upx = 0;
                int upy = 1;
                int upz = 0;
                if(PROG == 'B'){
                    gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
                    upy = 0;
                    upz = 1;
                }
                gl.glRotatef(anguloRotacao, upx, upy, upz);
                loader.draw(gl);
            gl.glPopMatrix();
            posx = (float) (Math.sin(posz));
        }
        posX = -1;
        posY = -1;
        posZ = -1;
        isDead = true;
    }
    
}
