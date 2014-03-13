uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		             		
		  			
attribute vec4 a_Position;		// Per-vertex position information we will pass in.   							     
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in. 		
		      		          		
varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.    		
		  
// The entry point for our vertex shader.  
void main()                                                 	
{                                                         
	// Pass through the texture coordinate.
	v_TexCoordinate = a_TexCoordinate;                                      
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = u_MVPMatrix * a_Position;                       		  
}      