precision mediump float; 
uniform sampler2D u_Texture; 
varying vec2 v_TexCoordinate;
const vec3 W = vec3(0.2125, 0.7154, 0.0721);

vec3 BrightnessContrastSaturation(vec3 color, float brt, float con, float sat)
{
	vec3 black = vec3(0., 0., 0.);
	vec3 middle = vec3(0.5, 0.5, 0.5);
	
	vec3 brtColor = mix(black, color, brt);
	vec3 conColor = mix(middle, brtColor, con);
	vec3 satColor = mix(color, conColor, sat);
	return satColor;
}

void main() 
{ 

     const vec3 W = vec3(0.2125, 0.1754, 0.0721);

     vec3 irgb = texture2D(u_Texture, v_TexCoordinate).rgb;

     float luminance = dot(irgb, W);
	 vec3 gray = vec3(luminance, luminance, luminance);
	 vec3 result = BrightnessContrastSaturation(gray, 2.5, 1.1, 1.0);
	 
     gl_FragColor = vec4(result, 1.); 
}
