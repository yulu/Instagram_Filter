precision mediump float; 
uniform sampler2D u_Texture0; 
uniform sampler2D u_Texture1;
varying vec2 v_TexCoordinate; 
const vec3 W = vec3(0.2125, 0.7154, 0.0721);

vec3 BrightnessContrastSaturation(vec3 color, float brt, float con, float sat)
{
	vec3 black = vec3(0., 0., 0.);
	vec3 middle = vec3(0.5, 0.5, 0.5);
	float luminance = dot(color, W);
	vec3 gray = vec3(luminance, luminance, luminance);
	
	vec3 brtColor = mix(black, color, brt);
	vec3 conColor = mix(middle, brtColor, con);
	vec3 satColor = mix(gray, conColor, sat);
	return satColor;
}

vec3 multiplyBlender(vec3 Color, vec3 filter){
	vec3 filter_result;
	float luminance = dot(filter, W);
	
	if(luminance < 0.5)
		filter_result = 2. * filter * Color;
	else
		filter_result = Color;
			
	return filter_result;
}

vec3 ovelayBlender(vec3 Color, vec3 filter){
	vec3 filter_result;

	float luminance = dot(filter, W);
	
	if(luminance < 0.5)
		filter_result = 2. * filter * Color;
	else
		filter_result = 1. - (1. - (2. *(filter - 0.5)))*(1. - Color);
		
	return filter_result;
}

void main()
{
	 //get the pixel
     vec2 st = v_TexCoordinate.st;
     vec3 irgb = texture2D(u_Texture0, st).rgb;
     vec3 filter = texture2D(u_Texture1, st).rgb;
     
     //adjust the brightness/contrast/saturation
     float T_bright = 1.2;
     float T_contrast = 1.2;
     float T_saturation = 1.3;
     vec3 bcs_result = BrightnessContrastSaturation(irgb, T_bright, T_contrast, T_saturation);
     
     
     //add filter (overlay blending)
     vec3 after_filter = mix(bcs_result, multiplyBlender(bcs_result, filter), 0.7);
     	
     gl_FragColor = vec4(after_filter, 1.);
}