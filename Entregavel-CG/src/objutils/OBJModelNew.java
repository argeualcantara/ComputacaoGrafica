package objutils;
// OBJModel.java

/* Load the OBJ model, centering and scaling it.
   The scale comes from the sz argument in the constructor, and
   is implemented by changing the vertices of the loaded model.

   The model can have vertices, normals and tex coordinates, and
   refer to materials in a MTL file.

   The OpenGL commands for rendering the model are stored in
   a display list (modelDispList), which is drawn by calls to
   draw().

   Information about the model is printed to stdout.
*/


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;


public class OBJModelNew
{
  private static final float DUMMY_Z_TC = -5.0f;

  // collection of vertices, normals and texture coords for the model
  private ArrayList<Tuple3> verts;
  private ArrayList<Tuple3> normals;
  private ArrayList<Tuple3> texCoords;
  private boolean hasTCs3D = true;
        // whether the model uses 3D or 2D tex coords

  private Faces faces;              // model faces
  private FaceMaterials faceMats;   // materials used by faces
  private Materials materials;      // materials defined in MTL file
  private ModelDimensions modelDims;  // model dimensions

  private String modelNm;    // without path or ".OBJ" extension
  private float maxSize;     // for scaling the model

  private int modelDispList;  // the model's display list


  public OBJModelNew(String nm, float sz, GL gl, boolean showDetails)
  {
    modelNm = nm;
    maxSize = sz;
    initModelData(modelNm);

    loadModel(modelNm);
    centerScale();
    drawToList(gl);

    if (showDetails){
      reportOnModel();
    }
  } // end of OBJModel()


  private void initModelData(String modelNm)
  {
    verts = new ArrayList<Tuple3>();
    normals = new ArrayList<Tuple3>();
    texCoords = new ArrayList<Tuple3>();

    faces = new Faces(verts, normals, texCoords);
    faceMats = new FaceMaterials();
    modelDims = new ModelDimensions();
  }  // end of initModelData()


  private void loadModel(String modelNm)
  {
    String fnm = modelNm + ".obj";
    try {
      System.out.println("Loading model from " + fnm + " ...");

	  FileInputStream fis_model = new FileInputStream(fnm);
	  BufferedReader br_model = new BufferedReader(new InputStreamReader(fis_model));
      readModel(br_model);
      br_model.close();
    }
    catch(IOException e){
		System.out.println(e.getMessage());
    	System.exit(1);
    }
  }  // end of loadModel()


  private void readModel(BufferedReader br)
  // parse the OBJ file line-by-line
  {
    boolean isLoaded = true;   // hope things will go okay

    int lineNum = 0;
    String line;
    boolean isFirstCoord = true;
    boolean isFirstTC = true;
    int numFaces = 0;

    try {
      while (((line = br.readLine()) != null) && isLoaded) {
        lineNum++;
        if (line.length() > 0) {
          line = line.trim(); //Returns a copy of the string, with leading and trailing whitespace omitted

          if (line.startsWith("v ")) {   // vertex
            isLoaded = addVert(line, isFirstCoord);
            if (isFirstCoord)
              isFirstCoord = false;
          } else if (line.startsWith("vt")) {   // tex coord
            isLoaded = addTexCoord(line, isFirstTC);
            if (isFirstTC)
              isFirstTC = false;
          } else if (line.startsWith("vn"))    // normal
            isLoaded = addNormal(line);
          else if (line.startsWith("f ")) {  // face
            isLoaded = faces.addFace(line);
            numFaces++;
          }


          else if (line.startsWith("mtllib "))   // load material
            materials = new Materials( line.substring(7) );
          else if (line.startsWith("usemtl "))   // use material
            faceMats.addUse( numFaces, line.substring(7));
          else if (line.charAt(0) == 'g') {  // group name
            // not implemented
          }
          else if (line.charAt(0) == 's') {  // smoothing group
            // not implemented
          }
          else if (line.charAt(0) == '#')   // comment line
            continue;
          else
            System.out.println("Ignoring line " + lineNum + " : " + line);
        }
      }
    }
    catch (IOException e) {
      System.out.println( e.getMessage() );
      System.exit(1);
    }

    if (!isLoaded) {
      System.out.println("Error loading model");
      System.exit(1);
    }
  } // end of readModel()



  private boolean addVert(String line, boolean isFirstCoord)
  /* Add vertex from line "v x y z" to vert ArrayList,
     and update the model dimension's info. */
  {
	Tuple3 vert = readTuple3(line);
    if (vert != null) {
      verts.add(vert);
      if (isFirstCoord)
        modelDims.set(vert);
      else
        modelDims.update(vert);
      return true;
    }
    return false;
  } // end of addVert()



  private Tuple3 readTuple3(String line)
  /* The line starts with an OBJ word ("v" or "vn"), followed
     by three floats (x, y, z) separated by spaces
  */
  {
    StringTokenizer tokens = new StringTokenizer(line, " ");
    tokens.nextToken();    // skip the OBJ word

    try {
      float x = Float.parseFloat(tokens.nextToken());
      float y = Float.parseFloat(tokens.nextToken());
      float z = Float.parseFloat(tokens.nextToken());

      return new Tuple3(x,y,z);
    }
    catch (NumberFormatException e)
    {  System.out.println(e.getMessage());  }

    return null;   // means an error occurred
  }  // end of readTuple3()



  private boolean addTexCoord(String line, boolean isFirstTC)
  /* Add the texture coordinate from the line "vt x y z" to
     the texCoords ArrayList. There may only be two tex coords
     on the line, which is determined by looking at the first
     tex coord line. */
  {
    if (isFirstTC) {
      hasTCs3D = checkTC3D(line);
      System.out.println("Using 3D tex coords: " + hasTCs3D);
    }

	Tuple3 texCoord = readTCTuple(line);
    if (texCoord != null) {
      texCoords.add( texCoord );
      return true;
    }
    return false;
  }  // end of addTexCoord()


  private boolean checkTC3D(String line)
  /* Check if the line has 4 tokens, which will be
     the "vt" token and 3 tex coords in this case. */
  {
    String[] tokens = line.split("\\s+");
    return (tokens.length == 4);
  }  // end of checkTC3D()


  private Tuple3 readTCTuple(String line)
  /* The line starts with a "vt" OBJ word and
     two or three floats (x, y, z) for the tex coords separated
     by spaces. If there are only two coords, then the z-value
     is assigned a dummy value, DUMMY_Z_TC.
  */
  {
    StringTokenizer tokens = new StringTokenizer(line, " ");
    tokens.nextToken();    // skip "vt" OBJ word

    try {
      float x = Float.parseFloat(tokens.nextToken());
      float y = Float.parseFloat(tokens.nextToken());

      float z = DUMMY_Z_TC;
      if (hasTCs3D)
        z = Float.parseFloat(tokens.nextToken());

      return new Tuple3(x,y,z);
    }
    catch (NumberFormatException e)
    {  System.out.println(e.getMessage());  }

    return null;   // means an error occurred
  }  // end of readTCTuple()


  private boolean addNormal(String line)
  // add normal from line "vn x y z" to the normals ArrayList
  {
	Tuple3 normCoord = readTuple3(line);
    if (normCoord != null) {
      normals.add( normCoord );
      return true;
    }
    return false;
 }  // end of addNormal()



  private void centerScale()
  /* Position the model so it's center is at the origin,
     and scale it so its longest dimension is no bigger
     than maxSize. */
  {
    // get the model's center point
    Tuple3 center = modelDims.getCenter();

    // calculate a scale factor
    float scaleFactor = 1.0f;
    float largest = modelDims.getLargest();
    // System.out.println("Largest dimension: " + largest);
    if (largest != 0.0f)
      scaleFactor = (maxSize / largest);
    System.out.println("Scale factor: " + scaleFactor);

    // modify the model's vertices
    Tuple3 vert;
    float x, y, z;
    for (int i = 0; i < verts.size(); i++) {
      vert = (Tuple3) verts.get(i);
      x = (vert.getX() - center.getX()) * scaleFactor;
      vert.setX(x);
      y = (vert.getY() - center.getY()) * scaleFactor;
      vert.setY(y);
      z = (vert.getZ() - center.getZ()) * scaleFactor;
      vert.setZ(z);
    }
  } // end of centerScale()



  private void drawToList(GL gl)
  /* render the model to a display list, so it can be
     drawn quicker later */
  {
    modelDispList = gl.glGenLists(1);
    gl.glNewList(modelDispList, GL.GL_COMPILE);

    gl.glPushMatrix();
    // render the model face-by-face
    String faceMat;
    for (int i = 0; i < faces.getNumFaces(); i++) {
      faceMat = faceMats.findMaterial(i);       // get material used by face i
      if (faceMat != null)
        materials.renderWithMaterial(faceMat, gl);  // render using that material
      faces.renderFace(i, gl);                  // draw face i
    }
    materials.switchOffTex(gl);
    gl.glPopMatrix();

    gl.glEndList();
  } // end of drawToList()



  public void draw(GL gl)
  {  gl.glCallList(modelDispList);  }



  private void reportOnModel()
  {
    System.out.println("No. of vertices: " + verts.size());
    System.out.println("No. of normal coords: " + normals.size());
    System.out.println("No. of tex coords: " + texCoords.size());
    System.out.println("No. of faces: " + faces.getNumFaces());

    modelDims.reportDimensions();
        // dimensions of model (before centering and scaling)

    if (materials != null)
      materials.showMaterials();   // list defined materials
    faceMats.showUsedMaterials();  // show what materials have been used by faces
  }  // end of reportOnModel()



	//----------------  inner Class  --------------------

	/* This class calculates the 'edge' coordinates for the model
	   along its three dimensions.

	   The edge coords are used to calculate the model's:
	      * width, height, depth
	      * its largest dimension (width, height, or depth)
	      * (x, y, z) center point
	*/
	//inner class ModelDimensions
	public class ModelDimensions
	{
	  // edge coordinates
	  private float leftPt, rightPt;   // on x-axis
	  private float topPt, bottomPt;   // on y-axis
	  private float farPt, nearPt;     // on z-axis

	  // for reporting
	  private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp


	  public ModelDimensions()
	  {
	    leftPt = 0.0f;  rightPt = 0.0f;
	    topPt = 0.0f;  bottomPt = 0.0f;
	    farPt = 0.0f;  nearPt = 0.0f;
	  }  // end of ModelDimensions()


	  public void set(Tuple3 vert)
	  // initialize the model's edge coordinates
	  {
	    rightPt = vert.getX();
	    leftPt = vert.getX();

	    topPt = vert.getY();
	    bottomPt = vert.getY();

	    nearPt = vert.getZ();
	    farPt = vert.getZ();
	  }  // end of set()


	  public void update(Tuple3 vert)
	  // update the edge coordinates using vert
	  {
	    if (vert.getX() > rightPt)
	      rightPt = vert.getX();
	    if (vert.getX() < leftPt)
	      leftPt = vert.getX();

	    if (vert.getY() > topPt)
	      topPt = vert.getY();
	    if (vert.getY() < bottomPt)
	      bottomPt = vert.getY();

	    if (vert.getZ() > nearPt)
	      nearPt = vert.getZ();
	    if (vert.getZ() < farPt)
	      farPt = vert.getZ();
	 }  // end of update()


	  // ------------- use the edge coordinates ----------------------------

	  public float getWidth()
	  { return (rightPt - leftPt); }

	  public float getHeight()
	  {  return (topPt - bottomPt); }

	  public float getDepth()
	  { return (nearPt - farPt); }


	  public float getLargest()
	  {
	    float height = getHeight();
	    float depth = getDepth();

	    float largest = getWidth();
	    if (height > largest)
	      largest = height;
	    if (depth > largest)
	      largest = depth;

	    return largest;
	  }  // end of getLargest()


	  public Tuple3 getCenter()
	  {
	    float xc = (rightPt + leftPt)/2.0f;
	    float yc = (topPt + bottomPt)/2.0f;
	    float zc = (nearPt + farPt)/2.0f;
	    return new Tuple3(xc, yc, zc);
	  } // end of getCenter()


	  public void reportDimensions()
	  {
	    Tuple3 center = getCenter();

	    System.out.println("x Coords: " + df.format(leftPt) + " to " + df.format(rightPt));
	    System.out.println("  Mid: " + df.format(center.getX()) +
	                       "; Width: " + df.format(getWidth()) );

	    System.out.println("y Coords: " + df.format(bottomPt) + " to " + df.format(topPt));
	    System.out.println("  Mid: " + df.format(center.getY()) +
	                       "; Height: " + df.format(getHeight()) );

	    System.out.println("z Coords: " + df.format(nearPt) + " to " + df.format(farPt));
	    System.out.println("  Mid: " + df.format(center.getZ()) +
	                       "; Depth: " + df.format(getDepth()) );
	  }  // end of reportDimensions()
	}  // end of inner class ModelDimensions



	//-------------------- inner Class ---------------------------
	/* Faces stores the information for each face of a model.


	   A face is represented by three arrays of indicies for
	   the vertices, normals, and tex coords used in that face.

	   facesVertIdxs, facesTexIdxs, and facesNormIdxs are ArrayLists of
	   those arrays; one entry for each face.

	   renderFace() is supplied with a face index, looks up the
	   associated vertices, normals, and tex coords indicies arrays,
	   and uses those arrays to access the actual vertices, normals,
	   and tex coords data for rendering the face.

	*/
	public class Faces
	{
	  private static final float DUMMY_Z_TC = -5.0f;

	  /* indicies for vertices, tex coords, and normals used
	     by each face */
	  private ArrayList<int[]> facesVertIdxs;
	  private ArrayList<int[]> facesTexIdxs;
	  private ArrayList<int[]> facesNormIdxs;

	  // references to the model's vertices, normals, and tex coords
	  private ArrayList<Tuple3> verts;
	  private ArrayList<Tuple3> normals;
	  private ArrayList<Tuple3> texCoords;


	  public Faces(ArrayList<Tuple3> vs, ArrayList<Tuple3> ns,
	                                         ArrayList<Tuple3> ts)
	  {
	    verts = vs;
	    normals = ns;
	    texCoords = ts;

	    facesVertIdxs = new ArrayList<int[]>();
	    facesTexIdxs = new ArrayList<int[]>();
	    facesNormIdxs = new ArrayList<int[]>();
	  }  // end of Faces()



	 public boolean addFace(String line)
	 /* get this face's indicies from line "f v/vt/vn ..."
	    with vt or vn index values perhaps being absent. */
	 {
	    try {
	      line = line.substring(2);   // skip the "f "
	      StringTokenizer st = new StringTokenizer(line, " ");
	      int numTokens = st.countTokens();   // number of v/vt/vn tokens
	      // create arrays to hold the v, vt, vn indicies
	      int v[] = new int[numTokens];
	      int vt[] = new int[numTokens];
	      int vn[] = new int[numTokens];

	      for (int i = 0; i < numTokens; i++) {
	        String faceToken = addFaceVals(st.nextToken());  // get a v/vt/vn token
	        // System.out.println(faceToken);

	        StringTokenizer st2 = new StringTokenizer(faceToken, "/");
	        int numSeps = st2.countTokens();  // how many '/'s are there in the token

	        v[i] = Integer.parseInt(st2.nextToken());
	        vt[i] = (numSeps > 1) ? Integer.parseInt(st2.nextToken()) : 0;
	        vn[i] = (numSeps > 2) ? Integer.parseInt(st2.nextToken()) : 0;
	            // add 0's if the vt or vn index values are missing;
	            // 0 is a good choice since real indicies start at 1
	      }
	      // store the indicies for this face
	      facesVertIdxs.add(v);
	      facesTexIdxs.add(vt);
	      facesNormIdxs.add(vn);
	    }
	    catch (NumberFormatException e) {
	      System.out.println("Incorrect face index");
	      System.out.println(e.getMessage());
	      return false;
	    }
	    return true;
	  }  // end of addFace()


	  private String addFaceVals(String faceStr)
	  /* A face token (v/vt/vn) may be missing vt or vn
	     index values; add 0's in those cases.
	  */
	  {
	    char chars[] = faceStr.toCharArray();
	    StringBuffer sb = new StringBuffer();
	    char prevCh = 'x';   // dummy value

	    for (int k = 0; k < chars.length; k++) {
	      if (chars[k] == '/' && prevCh == '/')   // if no char between /'s
	        sb.append('0');   // add a '0'
	      prevCh = chars[k];
	      sb.append(prevCh);
	    }
	    return sb.toString();
	  }  // end of addFaceVals()



	  public void renderFace(int i, GL gl)
	  /* Render the ith face by getting the vertex, normal, and tex
	     coord indicies for face i. Use those indicies to access the
	     actual vertex, normal, and tex coord data, and render the face.

	     Each face uses 3 array of indicies; one for the vertex
	     indicies, one for the normal indicies, and one for the tex
	     coord indicies.

	     If the model doesn't use normals or tex coords then the indicies
	     arrays will contain 0's.
	  */
	  {
	    if (i >= facesVertIdxs.size())   // i out of bounds?
	      return;

	    int[] vertIdxs = (int[]) (facesVertIdxs.get(i));
	         // get the vertex indicies for face i

	    int polytype;
	    if (vertIdxs.length == 3)
	      polytype = gl.GL_TRIANGLES;
	    else if (vertIdxs.length == 4)
	      polytype = gl.GL_QUADS;
	    else
	      polytype = gl.GL_POLYGON;


	    gl.glBegin(polytype);

	    // get the normal and tex coords indicies for face i
	    int[] normIdxs = (int[]) (facesNormIdxs.get(i));
	    int[] texIdxs = (int[]) (facesTexIdxs.get(i));

	    /* render the normals, tex coords, and vertices for face i
	       by accessing them using their indicies */
	    Tuple3 vert, norm, texCoord;
	    for (int f = 0; f < vertIdxs.length; f++) {

	      if (normIdxs[f] != 0) {  // if there are normals, render them
	        norm = (Tuple3) normals.get(normIdxs[f] - 1);
	        gl.glNormal3f(norm.getX(), norm.getY(), norm.getZ());
	      }

	      if (texIdxs[f] != 0) {   // if there are tex coords, render them
	        texCoord = (Tuple3) texCoords.get(texIdxs[f] - 1);
	        if (texCoord.getZ() == DUMMY_Z_TC)  // using 2D tex coords
	          gl.glTexCoord2f(texCoord.getX(), texCoord.getY());
	        else // 3D tex coords
	          gl.glTexCoord3f(texCoord.getX(), texCoord.getY(), texCoord.getZ());
	      }

	      vert = (Tuple3) verts.get(vertIdxs[f] - 1);  // render the vertices
	      gl.glVertex3f(vert.getX(), vert.getY(), vert.getZ());
	    }

	    gl.glEnd();
	  } // end of renderFace()

	  public int getNumFaces()
	  {  return facesVertIdxs.size();  }
	}  // end of inner Class Faces



	//-------------------------  Inner Class  --------------------------------------
	/* FaceMaterials stores the face indicies where a material
	   is first used. At render time, this information is utilized
	   to change the rendering material when a given face needs
	   to be drawn.
	*/
	public class FaceMaterials
	{
	  private HashMap<Integer, String>faceMats;
		// the face index (integer) where a material is first used

	  // for reporting
	  private HashMap<String, Integer>matCount;
		 // how many times a material (string) is used

	  public FaceMaterials()
	  {
		faceMats = new HashMap<Integer, String>();
		matCount = new HashMap<String, Integer>();
	  } // end of FaceMaterials()


	  public void addUse(int faceIdx, String matName)
	  {
		// store the face index and the material it uses
		if (faceMats.containsKey(faceIdx))  // face index already present
		  System.out.println("Face index " + faceIdx +
						 " changed to use material " + matName);
		faceMats.put(faceIdx, matName);

		// store how many times matName has been used by faces
		if (matCount.containsKey(matName)) {
		  int i = (Integer) matCount.get(matName) + 1;
		  matCount.put(matName, i);
		}
		else
		  matCount.put(matName, 1);
	  }  // end of addUse()


	  public String findMaterial(int faceIdx)
	  {  return (String) faceMats.get(faceIdx);  }


	  public void showUsedMaterials()
	  /* List all the materials used by faces, and the number of
		 faces that have used them. */
	  {
		System.out.println("No. of materials used: " + matCount.size());

		// build an iterator of material names
		Set<String> keys = matCount.keySet();
		Iterator<String> iter = keys.iterator();

		// cycle through the hashmap showing the count for each material
		String matName;
		int count;
		while(iter.hasNext()){
		  matName = iter.next();
		  count = (Integer) matCount.get( matName );

		  System.out.print( matName + ": " + count);
		  System.out.println();
		}
	  }  // end of showUsedMaterials()

	} // end of FaceMaterials class


	//------------------ inner class Materials -----------------------------------
	/* This class does two main tasks:
	      * it loads the material details from the MTL file, storing
	        them as Material objects in the materials ArrayList.

	      * it sets up a specified material's colours or textures
	        to be used when rendering -- see renderWithMaterial()
	*/
	public class Materials
	{
	  private static final String MODEL_DIR = "";

	  private ArrayList<Material> materials;
	     // stores the Material objects built from the MTL file data

	  // for storing the material currently being used for rendering
	  private String renderMatName = null;

	  private boolean usingTexture = false;

	  public Materials(String mtlFnm)
	  {
	    materials = new ArrayList<Material>();
	    
	    String mfnm = modelNm+".mtl";
	    try {
	      System.out.println("Loading material from " + mfnm);
	      BufferedReader br = new BufferedReader( new FileReader(mfnm));
	      readMaterials(br);
	      br.close();
	    }
	    catch (IOException e)
	    { System.out.println(e.getMessage());  }

	  } // end of Materials()



	  private void readMaterials(BufferedReader br)
	  /* Parse the MTL file line-by-line, building Material
	     objects which are collected in the materials ArrayList. */
	  {
	    try {
	      String line;
	      Material currMaterial = null;  // current material

	      while (((line = br.readLine()) != null)) {
	        line = line.trim();
	        if (line.length() == 0)
	          continue;

	        if (line.startsWith("newmtl ")) {  // new material
	          if (currMaterial != null)   // save previous material
	            materials.add(currMaterial);

	          // start collecting info for new material
	          currMaterial = new Material(line.substring(7));
	        }
	        else if (line.startsWith("map_Kd ")) {  // texture filename
	          String fileName = MODEL_DIR + line.substring(7);
	          currMaterial.loadTexture( fileName );
	        }
	        else if (line.startsWith("Ka "))    // ambient colour
	          currMaterial.setKa( readTuple3(line) );
	        else if (line.startsWith("Kd "))    // diffuse colour
	          currMaterial.setKd( readTuple3(line) );
	        else if (line.startsWith("Ks "))    // specular colour
	          currMaterial.setKs( readTuple3(line) );
	        else if (line.startsWith("Ns ")) {  // shininess
	          float val = Float.valueOf(line.substring(3)).floatValue();
	          currMaterial.setNs( val );
	        }
	        else if (line.charAt(0) == 'd') {    // alpha
	          float val = Float.valueOf(line.substring(2)).floatValue();
	          currMaterial.setD( val );
	        }
	        else if (line.startsWith("illum ")) { // illumination model
	          // not implemented
	        }
	        else if (line.charAt(0) == '#')   // comment line
	          continue;
	        else
	          System.out.println("Ignoring MTL line: " + line);

	      }
	      materials.add(currMaterial);
	    }
	    catch (IOException e)
	    { System.out.println(e.getMessage());  }
	  } // end of readMaterials()


	  private Tuple3 readTuple3(String line)
	  /* The line starts with an MTL word such as Ka, Kd, Ks, and
	     the three floats (x, y, z) separated by spaces
	  */
	  {
	    StringTokenizer tokens = new StringTokenizer(line, " ");
	    tokens.nextToken();    // skip MTL word

	    try {
	      float x = Float.parseFloat(tokens.nextToken());
	      float y = Float.parseFloat(tokens.nextToken());
	      float z = Float.parseFloat(tokens.nextToken());

	      return new Tuple3(x,y,z);
	    }
	    catch (NumberFormatException e)
	    {  System.out.println(e.getMessage());  }

	    return null;   // means an error occurred
	  }  // end of readTuple3()


	  public void showMaterials()
	  // list all the Material objects
	  {
	    System.out.println("No. of materials: " + materials.size());
	    Material m;
	    for (int i=0; i < materials.size(); i++) {
	      m = (Material) materials.get(i);
	      m.showMaterial();
	      // System.out.println();
	    }
	  }  // end of showMaterials()



	  // ----------------- using a material at render time -----------------


	  public void renderWithMaterial(String faceMat, GL gl)
	  /* Render using the texture or colours associated with the
	     material, faceMat. But only change things if faceMat is
	     different from the current rendering material, whose name
	     is stored in renderMatName.
	  */
	  {
	    if (!faceMat.equals(renderMatName)) {   // is faceMat is a new material?
	      renderMatName = faceMat;
	      switchOffTex(gl);   // switch off any previous texturing

	      // set up new rendering material
	      Texture tex = getTexture(renderMatName);
	      if (tex != null) {   // use the material's texture
	        // System.out.println("Using texture with " + renderMatName);
	        switchOnTex(tex, gl);
	      }
	      else   // use the material's colours
	        setMaterialColors(renderMatName, gl);
	    }
	  }  // end of renderWithMaterial()


	  public void switchOffTex(GL gl)
	  // switch texturing off and put the lights on;
	  // also called from ObjModel.drawToList()
	  {
	    if (usingTexture) {
	      gl.glDisable(GL.GL_TEXTURE_2D);
	      usingTexture = false;
	      gl.glEnable(GL.GL_LIGHTING);
	    }
	  } // end of resetMaterials()


	  private void switchOnTex(Texture tex, GL gl)
	  // switch the lights off, and texturing on
	  {
	    gl.glDisable(GL.GL_LIGHTING);
	    gl.glEnable(GL.GL_TEXTURE_2D);
	    usingTexture = true;
	    tex.bind();
	  } // end of resetMaterials()


	  private Texture getTexture(String matName)
	  // return the texture associated with the material name
	  {
	    Material m;
	    for (int i = 0; i < materials.size(); i++) {
	      m = (Material) materials.get(i);
	      if (m.hasName(matName))
	        return m.getTexture();
	    }
	    return null;
	  } // end of getTexture()


	  private void setMaterialColors(String matName, GL gl)
	  // start rendering using the colours specifies by the named material
	  {
	    Material m;
	    for (int i = 0; i < materials.size(); i++) {
	      m = (Material) materials.get(i);
	      if (m.hasName(matName))
	        m.setMaterialColors(gl);
	    }
	  }  // end of setMaterialColors()


	  //-------------------------  inner Class  -----------------------------
	  /* A Material object holds colour and texture information
	     for a named material.

	     The Material object also manages the rendering using its
	     colours (see setMaterialColors()). The rendering using the
	     texture is done by the Materials object.
	  */
	  public class Material
	  {
	    private String name;

	    // colour info
	    private Tuple3 ka, kd, ks;   // ambient, diffuse, specular colours
	    private float ns, d;   // shininess and alpha

	    // texture info
	    private String texFnm;
	    private Texture texture;


	    public Material(String nm)
	    {
	      name = nm;

	      d = 1.0f; ns = 0.0f;
	      ka = null; kd = null; ks = null;

	      texFnm = null;
	      texture = null;
	    }  // end of Material()


	    public void showMaterial()
	    {
	      System.out.println(name);
	      if (ka != null)
	        System.out.println("  Ka: " + ka.toString());
	      if (kd != null)
	        System.out.println("  Kd: " + kd.toString());
	      if (ks != null)
	        System.out.println("  Ks: " + ks.toString());
	      if (ns != 0.0f)
	        System.out.println("  Ns: " + ns);
	      if (d != 1.0f)
	        System.out.println("  d: " + d);
	      if (texFnm != null)
	        System.out.println("  Texture file: " + texFnm);
	    }  // end of showMaterial()


	    public boolean hasName(String nm)
	    {  return name.equals(nm);  }


	    // --------- set/get methods for colour info --------------

	    public void setD(float val)
	    {  d = val;  }

	    public float getD()
	    {  return d;  }


	    public void setNs(float val)
	    {  ns = val;  }

	    public float getNs()
	    {  return ns;  }


	    public void setKa(Tuple3 t)
	    {  ka = t;  }

	    public Tuple3 getKa()
	    {  return ka;  }


	    public void setKd(Tuple3 t)
	    {  kd = t;  }

	    public Tuple3 getKd()
	    {  return kd;  }


	    public void setKs(Tuple3 t)
	    {  ks = t;  }

	    public Tuple3 getKs()
	    {  return ks;  }


	    public void setMaterialColors(GL gl)
	    // start rendering using this material's colour information
	    {

	  	  System.out.println(" --- SET MATERIAL COLOR ---");
	      if (ka != null) {   // ambient color
	        float[] colorKa = { ka.getX(), ka.getY(), ka.getZ(), 1.0f };
	        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, colorKa, 0);
	      }
	      if (kd != null) {  // diffuse color
	        float[] colorKd = { kd.getX(), kd.getY(), kd.getZ(), 1.0f };
	        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, colorKd, 0);
	      }
	      if (ks != null) {   // specular color
	        float[] colorKs = { ks.getX(), ks.getY(), ks.getZ(), 1.0f };
	        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, colorKs, 0);
	      }

	      if (ns != 0.0f) {   // shininess
	        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, ns);
	      }

	      if (d != 1.0f) {   // alpha
	        // not implemented
	      }
	    } // end of setMaterialColors()


	    // --------- set/get methods for texture info --------------


	    public void loadTexture(String fnm)
	    {
	      try {
	        texFnm = fnm;
	        texture = TextureIO.newTexture( new File(texFnm), false);
	        texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
	        texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
	      }
	      catch(Exception e)
	      { System.out.println("Error loading texture " + texFnm);  
	      e.printStackTrace();}
	    }  // end of loadTexture()


	    public void setTexture(Texture t)
	    {  texture = t;  }

	    public Texture getTexture()
	    {  return texture;  }

	  }  // end of inner Class Material
	} // end of Inner class Materials

} // end of OBJModel class
