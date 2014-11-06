/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;

import javax.media.opengl.GL;

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
    //Matriz que armazena as posiï¿½ï¿½es ocupadas com true
    public Matriz mapa;
    public Pokemon(GL gl, Matriz mapa, String path, int iniX, int iniY, char dir) throws IOException {
        super();
        this.mapa = mapa;
        this.offset = mapa.ocupado.length/2;
        //Corrigir objetos importados de outro programa com o Z para cima ao inves do Y
        if(path.contains("BR_")){
        	PROG = 'B';
        }
        this.loader = new OBJModelNew(path, 1.5f, gl, true);
        
//        this.obj = ObjLoader.loadObj("obj/gengar/Gengar.obj", "obj/gengar/Gangar.mtl", gl);
//        BufferedImage im = ImageIO.read(new File("src/obj/gengar/Textures/GengarDh.jpeg"));
//        this.tex1 = TextureIO.newTexture(im, true);
//        this.tex1.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
//        this.tex1.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
//        this.tex1.setTexParameterfv(GL.GL_AMBIENT, new float [] {0.5f, 0.5f, 0.5f}, 0);
//        this.tex1.setTexParameterfv(GL.GL_DIFFUSE, new float [] {0.9f, 0.9f, 0.9f}, 0);
//        this.tex1.setTexParameterfv(GL.GL_SPECULAR, new float [] {0.0f, 0.0f, 0.0f}, 0);
        
//        im = ImageIO.read(new File("src/obj/gengar/Textures/gengar_0_0.jpeg"));
//        this.tex2 = TextureIO.newTexture(im, true);
//        this.tex2.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
//        this.tex2.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
//        this.tex2.setTexParameterfv(GL.GL_AMBIENT, new float [] {0.5f, 0.5f, 0.5f}, 0);
//        this.tex2.setTexParameterfv(GL.GL_DIFFUSE, new float [] {0.9f, 0.9f, 0.9f}, 0);
//        this.tex2.setTexParameterfv(GL.GL_SPECULAR, new float [] {0.0f, 0.0f, 0.0f}, 0);
        this.dir = dir;
        switch(this.dir){
	        case'N':break;
	        case'S':anguloRotacao = 180;break;
	        case'L':anguloRotacao = -90;break;
	        case'O':anguloRotacao = 90;break;
        }
        posX = iniX;
        posY = iniY;
        posZ = 5;
        
        //Marca a posiï¿½ï¿½o inicial do ator como ocupada
        mapa.ocupado[posX+offset] [posY+offset] = Matriz.POKEMON;
        
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
//            gl.glScalef(0.22f, 0.22f, 0.22f);
            loader.draw(gl);
        gl.glPopMatrix();
        
    }
    
    public void andar(){
        //Os IF's verificam se a posição a ser ocupada pelo ator está ocupada por outro objeto.
        //Posição ocupada dos objetos é definida no metodo init da classe OpenGLEvent
        //Podendo andar a posição atual é marcada como desocupada e a próxima como ocupada.
        //Os magic numbers -12 e 12 são os limites do tabuleiro.
        switch(dir){
            case 'N':
                if(posY+3 <= 12 && mapa.ocupado[posX+offset] [posY+3+offset] != Matriz.POKEMON){
                    mapa.ocupado[posX+offset] [posY+offset] = ' ';
                    mapa.ocupado[posX+offset] [posY+3+offset] = Matriz.POKEMON;
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
                if(posY-3 >= -12 && mapa.ocupado[posX+offset] [posY-3+offset] != Matriz.POKEMON){
                    mapa.ocupado[posX+offset] [posY+offset] = ' ';
                    mapa.ocupado[posX+offset] [posY-3+offset] = Matriz.POKEMON;
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
                if(posX-3 >= -12 && mapa.ocupado[posX-3+offset] [posY+offset] != Matriz.POKEMON){
                    mapa.ocupado[posX+offset] [posY+offset] = ' ';
                    mapa.ocupado[posX-3+offset] [posY+offset] = Matriz.POKEMON;
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
                if(posX+3 <= 12 && mapa.ocupado[posX+3+offset] [posY+offset] != Matriz.POKEMON){
                    mapa.ocupado[posX+offset] [posY+offset] = ' ';
                    mapa.ocupado[posX+3+offset] [posY+offset] = Matriz.POKEMON;
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
    	int valorAngulo = -90;
        if(PROG == 'B'){
        	valorAngulo *= -1;
        }
        anguloRotacao += valorAngulo;
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
    	int valorAngulo = 90;
        if(PROG == 'B'){
        	valorAngulo *= -1;
        }
        anguloRotacao += valorAngulo;
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
    
}
