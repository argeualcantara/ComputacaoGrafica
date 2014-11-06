/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import javazoom.jl.player.Player;
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
    char dir = 'N';
    int count = 0;
    private int anguloRotacao;
    public int eyeX;
    public int eyeY;
    public int eyeZ;
    public int upx;
    public int upy;
    public char PROG = 'A';
    public OBJModelNew loader;
    int offset;
    //Matriz que armazena as posi��es ocupadas com true
    public Matriz mapa;
	public boolean isDamaged;
	public boolean isAttacking;
    public Pokemon(GL gl, Matriz mapa, String path, int iniX, int iniY, char dir) throws IOException {
        super();
        this.mapa = mapa;
        this.offset = mapa.ocupado.length/2;
        //Corrigir objetos importados de outro programa com o Z para cima ao inves do Y
        if(path.contains("BR_")){
        	PROG = 'B';
        }
        this.loader = new OBJModelNew(path, 1.5f, gl, true);
        
        this.dir = dir;
        posX = iniX;
        posY = iniY;
        posZ = 4;
        
        //Marca a posi��o inicial do ator como ocupada
        mapa.ocupado[posX+offset] [posY+offset] = Matriz.POKEMON;
        
        switch(this.dir){
        case'N':centroX = eyeX; centroY = eyeY+10;break;
        case'S':anguloRotacao = 180; centroX = eyeX;centroY = eyeY-10;break;
        case'L':anguloRotacao = -90; centroX = eyeX +10;centroY = eyeY;break;
        case'O':anguloRotacao = 90; centroX = eyeX -10;centroY = eyeY;break;
        }
        
        eyeX = posX;
        eyeY = posY;
        eyeZ = posZ+3;
        
        centroZ = eyeZ+2;
        upx = 0;
        upy = 1;
    }

    public void inserir(GL gl) {
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
        
    }
    
    public void andar(){
        //Os IF's verificam se a posi��o a ser ocupada pelo ator est� ocupada por outro objeto.
        //Posi��o ocupada dos objetos � definida no metodo init da classe OpenGLEvent
        //Podendo andar a posi��o atual � marcada como desocupada e a pr�xima como ocupada.
        //Os magic numbers -12 e 12 s�o os limites do tabuleiro.
        switch(dir){
            case 'N':
                if(posY+3 <= 12 && mapa.ocupado[posX+offset] [posY+3+offset] != Matriz.POKEMON){
                    mapa.ocupado[posX+offset] [posY+offset] = ' ';
                    posY += 3;
                    mapa.ocupado[posX+offset] [posY+offset] = Matriz.POKEMON;
                    eyeX = posX;
                    eyeY = posY;
                    centroX = eyeX;
                    centroY = eyeY+10;
                    upx = 0;
                    upy = 1;
                }else{
                    if(posY+3 > 12){
                        System.out.println("Fora do tabuleiro.");
                    }else{
                        System.out.println("Posi��o ocupada.");
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
                    centroY = eyeY-10;
                    upx = 0;
                    upy = 1;
                }else{
                    if(posY-3 < -12){
                        System.out.println("Fora do tabuleiro.");
                    }else{
                        System.out.println("Posi��o ocupada.");
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
                    centroX = posX - 20;
                    centroY = posY;
                    upx = 1;
                    upy = 0;
                }else{
                    if(posX-3 < -12){
                        System.out.println("Fora do tabuleiro.");
                    }else{
                        System.out.println("Posi��o ocupada.");
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
                    centroX = posX + 10;
                    centroY = posY;
                    upx = 1;
                    upy = 0;
                }else{
                    if(posX+3 > 12){
                        System.out.println("Fora do tabuleiro.");
                    }else{
                        System.out.println("Posi��o ocupada.");
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
                centroX = posX + 10;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
            case 'S':
                dir = 'L';
                eyeX = posX;
                eyeY = posY;
                centroX = posX - 10;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
            case 'O':
                dir = 'S';
                eyeX = posX;
                eyeY = posY;
                centroX = eyeX;
                centroY = eyeY-10;
                upx = 0;
                upy = -1;
                break;
            case 'L':
                dir = 'N';
                eyeX = posX;
                eyeY = posY;
                centroX = eyeX;
                centroY = eyeY+10;
                upx = 0;
                upy = 1;
                break;
        }
    
    }
    
    public void virarEsquerda(){
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
                centroX = posX - 10;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
            case 'S':
                dir = 'O';
                eyeX = posX;
                eyeY = posY;
                centroX = posX + 10;
                centroY = posY;
                upx = 1;
                upy = 0;
                break;
            case 'O':
                dir = 'N';
                eyeX = posX;
                eyeY = posY;
                centroX = eyeX;
                centroY = eyeY+10;
                upx = 0;
                upy = 1;
                break;
            case 'L':
                dir = 'S';
                eyeX = posX;
                eyeY = posY;
                centroX = eyeX;
                centroY = eyeY-10;
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
    
    public boolean atacar(GL gl, GLU glu){
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
    }
    
	public void damage(GL gl) {
		this.isDamaged = false;
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
    
}
