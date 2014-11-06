/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objutils;

import javax.media.opengl.GL;

/**
 *
 * @author Argeu
 */
public class ObjModel {
    /** The identifier of the display list held for this model */
    private int listID;
     
    /**
     * Create a new OBJ model that will render the object data 
     * specified in OpenGL
     * 
     * @param data The data to be rendered for this model
     */
    public ObjModel(ObjData data, GL gl) {
        // we're going to process the OBJ data and produce a display list
        // that will display the model. First we ask OpenGL to create
        // us a display list
        listID = gl.glGenLists(1);
         
        // next we start producing the contents of the list
        gl.glNewList(listID, gl.GL_COMPILE);
         
            // cycle through all the faces in the model data
             
        int faceCount = data.getFaceCount();
         
                for (int i=0;i<data.getFaceCount();i++) {
             
                    int fpoints = data.getFace(i).getPoints();
                    if( fpoints == 3)   
                        gl.glBegin(gl.GL_TRIANGLES);
                    else if (fpoints == 4)
                        gl.glBegin(gl.GL_QUADS);
                    for (int v=0;v<fpoints;v++) {
                        // a position, normal and texture coordinate
                        // for each vertex in the face
                        Tuple3 vert = data.getFace(i).getVertex(v);
                        Tuple3 norm = data.getFace(i).getNormal(v);
//                        Tuple2 tex = data.getFace(i).getTexCoord(v);
                        
                        gl.glNormal3f(norm.getX(), norm.getY(), norm.getZ());
//                        gl.glTexCoord2f(tex.getX(), tex.getY());
                        gl.glVertex3f(vert.getX(), vert.getY(), vert.getZ());
                        
                        
                    }
                    gl.glEnd();
                }
        gl.glEndList();
    }
     
    /**
     * Render the OBJ Model
     */
    public void render(GL gl) {
        // since we rendered our display list at construction we
        // can now just call this list causing it to be rendered
        // to the screen
        gl.glCallList(listID);
    }
}
