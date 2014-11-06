/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objutils;

import java.nio.FloatBuffer;

/**
 *
 * @author Argeu
 */
class Tuple3 {
    /** The x element in this tuple */
    private float x;
    /** The y element in this tuple */
    private float y;
    /** The z element in this tuple */
    private float z;
     
    /**
     * Create a new 3 dimensional tuple
     * 
     * @param x The X element value for the new tuple
     * @param y The Y element value for the new tuple
     * @param z The Z element value for the new tuple
     */
    public Tuple3(float x,float y,float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
 
  

	public FloatBuffer getFloatBuffer() {
		
		return FloatBuffer.wrap(new float [] {x,y,z});
	}



	public float getX() {
		return x;
	}



	public void setX(float x) {
		this.x = x;
	}



	public float getY() {
		return y;
	}



	public void setY(float y) {
		this.y = y;
	}



	public float getZ() {
		return z;
	}



	public void setZ(float z) {
		this.z = z;
	}
}