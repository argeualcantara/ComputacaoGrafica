/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.io.IOException;
import java.io.InputStream;
import javax.media.opengl.GL;

/**
 *
 * @author Argeu
 */
public class ObjLoader {
     public static ObjModel loadObj(String ref, GL gl) throws IOException {
        InputStream in = ObjLoader.class.getClassLoader().getResourceAsStream(ref);
     
        if (in == null) {
            throw new IOException("Unable to find: "+ref);
        }
         
        return new ObjModel(new ObjData(in), gl);
    }
}
