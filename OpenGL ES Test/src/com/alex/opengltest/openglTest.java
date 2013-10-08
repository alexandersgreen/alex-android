package com.alex.opengltest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
//import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;


public class openglTest extends Activity {
	
	static int seg = 60;
	GLSurfaceView opengl;
	float lat,lon,alt,x,y,z,r;
	//Random generator;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

       // generator = new Random();
        alt = 3000.0f;
        
        opengl = (GLSurfaceView) this.findViewById(R.id.opengl);
        opengl.setRenderer(new OpenGLRenderer());
    }
    
    public class OpenGLRenderer implements Renderer{

        FloatBuffer sphereSegmentBuffer;
		FloatBuffer positionBuffer;
        	
    		public void onDrawFrame(GL10 gl) {
    			//called when drawing openGL frame
    			//set the colour to clear with
    	        gl.glClearColor(0.0f,0.0f,0.0f, 1.0f);
    	        // clear the screen
    	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    	        //draw the lines for the wire frame sphere
    			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, sphereSegmentBuffer);
    	        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
    	        gl.glDrawArrays(GL10.GL_LINES, 0, seg*seg*4);
    	        
    	        /*
    	        //draw the current position on the sphere.
    	        //co-ordinates need to be converted to polar
    	        float phi = (lat*2*(float) Math.PI)/360.0f;
    	        float theta = (lon*2*(float) Math.PI)/360.0f;
    	        float epsilon = alt/10000.0f;
    	        float thisx = x + (r+epsilon)*((float) Math.cos(phi))*((float) Math.cos(theta));
    	        float thisy = y + (r+epsilon)*((float) Math.sin(phi));
    	        float thisz = z + (r+epsilon)*((float) Math.cos(phi))*((float) Math.sin(theta));
    	        float thisx1 = x + r*((float) Math.cos(phi))*((float) Math.cos(theta));
    	        float thisy1 = y + r*((float) Math.sin(phi));
    	        float thisz1 = z + r*((float) Math.cos(phi))*((float) Math.sin(theta));
    	        float thisx2 = x + (r-epsilon)*((float) Math.cos(phi))*((float) Math.cos(theta));
    	        float thisy2 = y + (r-epsilon)*((float) Math.sin(phi));
    	        float thisz2 = z + (r-epsilon)*((float) Math.cos(phi))*((float) Math.sin(theta));
    	        float[] pos = {thisx2,thisy2,thisz2,thisx1,thisy1,thisz1,thisx,thisy,thisz};
    	        ByteBuffer posB = ByteBuffer.allocateDirect(pos.length*4);
    	        posB.order(ByteOrder.nativeOrder());
    			positionBuffer = posB.asFloatBuffer();
    			positionBuffer.put(pos);
    			positionBuffer.position(0);
    			//now draw the position
    			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, positionBuffer);
    	        gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
    	        gl.glDrawArrays(GL10.GL_LINES, 0, 3);
    	        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    	        
    	        //for now, update co-ordinates randomly
    	        lat = (generator.nextFloat()*180.0f)-90.0f;
    	        lon = (generator.nextFloat()*360.0f)-180.0f;
    	        */
    		}

    		public void onSurfaceChanged(GL10 gl, int w, int h) {
    			// called when frame size changes
    			gl.glViewport(0, 0, w, h);
    		}

    		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    			// called upon creation
    			x = 1.6f;
    			y = 2.3f;
    			z = 0.0f;
    			r = 1.5f;
    			//segments,x,y,z,r
    			float[] segments = sphere(seg,x,y,z,r);
    			ByteBuffer bbS = ByteBuffer.allocateDirect(segments.length*4);
    	        bbS.order(ByteOrder.nativeOrder());
    			sphereSegmentBuffer = bbS.asFloatBuffer();
    			sphereSegmentBuffer.put(segments);
    			sphereSegmentBuffer.position(0);
    	        // left,right,bottom,top,near,far
    	        gl.glOrthof(0.0f, 3.2f, 0.0f, 4.6f, -1.6f, 1.6f);
    		}
    		    		
    		//returns a wireframe sphere, made with line segments
    		public float[] sphere(int segments, float x, float y, float z, float r){
    			//segments is number of segments between longitudes = i
    			//segments/2 is number of segments between latitudes = j
    			//want segments to be even
    			if(segments%2==1){segments++;}
    			//each segment is a square made with two triangles
    			float[] sphereSegments = new float[segments*segments*12];
    			//xi = x + r cos(phi) cos(theta)
    			//yi = y + r sin(phi)
    			//zi = z + r cos(phi) sin(theta)
    			// 0 <= theta < 2pi
    			// -pi/2 <= phi <= pi/2
    			//theta is longitude (pole to pole)
    			//phi is latitude (e.g equator = 0)
    			//cos(0) = 1, sin(0) = 0
    			for(int i=0;i<segments;i++){
    				double theta = ((2*Math.PI*i)/(float) segments);
    				double theta1 = ((2*Math.PI*(i+1))/(float) segments);
    				for(int j=0;j<(segments/2);j++){
    					int currentSquare = (12*segments*i)+(24*j);
    					double phi = ((Math.PI*j)/(float) (segments/2)) - (Math.PI / 2.0);
    					double phi1 = ((Math.PI*(j+1))/(float) (segments/2)) - (Math.PI / 2.0);
    					//clockwise
    					//bottom left
    					sphereSegments[currentSquare+0] = x + r*((float) Math.cos(phi))*((float) Math.cos(theta));
    					sphereSegments[currentSquare+1] = y + r*((float) Math.sin(phi));
    					sphereSegments[currentSquare+2] = z + r*((float) Math.cos(phi))*((float) Math.sin(theta));
    					//top left
    					sphereSegments[currentSquare+3] = x + r*((float) Math.cos(phi1))*((float) Math.cos(theta));
    					sphereSegments[currentSquare+4] = y + r*((float) Math.sin(phi1));
    					sphereSegments[currentSquare+5] = z + r*((float) Math.cos(phi1))*((float) Math.sin(theta));
    					//top left
    					sphereSegments[currentSquare+6] = x + r*((float) Math.cos(phi1))*((float) Math.cos(theta));
    					sphereSegments[currentSquare+7] = y + r*((float) Math.sin(phi1));
    					sphereSegments[currentSquare+8] = z + r*((float) Math.cos(phi1))*((float) Math.sin(theta));
    					//top right
    					sphereSegments[currentSquare+9] = x + r*((float) Math.cos(phi1))*((float) Math.cos(theta1));
    					sphereSegments[currentSquare+10] = y + r*((float) Math.sin(phi1));
    					sphereSegments[currentSquare+11] = z + r*((float) Math.cos(phi1))*((float) Math.sin(theta1));
    					//top right
    					sphereSegments[currentSquare+12] = x + r*((float) Math.cos(phi1))*((float) Math.cos(theta1));
    					sphereSegments[currentSquare+13] = y + r*((float) Math.sin(phi1));
    					sphereSegments[currentSquare+14] = z + r*((float) Math.cos(phi1))*((float) Math.sin(theta1));
    					//bottom right
    					sphereSegments[currentSquare+15] = x + r*((float) Math.cos(phi))*((float) Math.cos(theta1));
    					sphereSegments[currentSquare+16] = y + r*((float) Math.sin(phi));
    					sphereSegments[currentSquare+17] = z + r*((float) Math.cos(phi))*((float) Math.sin(theta1));
    					//bottom right
    					sphereSegments[currentSquare+18] = x + r*((float) Math.cos(phi))*((float) Math.cos(theta1));
    					sphereSegments[currentSquare+19] = y + r*((float) Math.sin(phi));
    					sphereSegments[currentSquare+20] = z + r*((float) Math.cos(phi))*((float) Math.sin(theta1));
    					//bottom left
    					sphereSegments[currentSquare+21] = x + r*((float) Math.cos(phi))*((float) Math.cos(theta));
    					sphereSegments[currentSquare+22] = y + r*((float) Math.sin(phi));
    					sphereSegments[currentSquare+23] = z + r*((float) Math.cos(phi))*((float) Math.sin(theta));
    				}
    			}
    			return sphereSegments;
    		}
        }
}