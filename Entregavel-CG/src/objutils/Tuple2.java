/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objutils;

/**
 *
 * @author Argeu
 */
class Tuple2 {
    /** The x element in this tuple */
    private float x;
    /** The y element in this tuple */
    private float y;
     
    /**
     * Create a new Tuple of 2 elements
     * 
     * @param x The X element value
     * @param y The Y element value
     */
    public Tuple2(float x,float y) {
        this.x = x;
        this.y = y;
    }
     
    /**
     * Get the X element value from this tuple
     * 
     * @return The X element value from this tuple
     */
    public float getX() {
        return x;
    }
 
    /**
     * Get the Y element value from this tuple
     * 
     * @return The Y element value from this tuple
     */
    public float getY() {
        return y;
    }
     
}
