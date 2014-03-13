##Having More Fun: Explore Instagram-like Filters with Photoshop and OpenGL ES Shaders
By [LittleCheeseCake](http://littlecheesecake.me)

I used to spend quite some time working with `Photoshop®`, to edit photos or do some digital painting. However I became too lazy to PS since I started to use smartphones. Apps like `Instagram®` seems to be able to do the processing job more efficiently and produce results that look surprisingly amazing (not considering the quality loss of the photo). After studying the OpenGL Shaders, I thought I should be enable to do mimic the filter effect using shaders, with those previous experience working with `Photoshop®`. I could first explore in `Photoshop®` to figure out the steps to take to produce the filter effect. Then I could implement it using OpenGL ES Shaders. After spending some time in research, I kind of found the way to duplicate the filter effect, might not be exactly the same, but the feeling is captured. Take the **Hudson** filter as an example, I summarized the work I have done.

![hudson_in](http://littlecheesecake.files.wordpress.com/2013/02/hudson_in.png?w=260 "hudson_in")

### Working with `Photoshop®`

Learned from a digital photography course in the college, the first thing we need to do when loading an photo into the Photoshop, is to adjust the level and curve. However I think level and curve adjusting is not that easy to implement in the shaders. How about a less effective but simpler way: adjust Brightness/Contrast/Saturation. All these three steps can be easily realized by blending the image with a base image (either a constant image or the luminanced image of itself). By simply doing these, the quality of the image can be improved quite a lot.

![bright and contrast](http://littlecheesecake.files.wordpress.com/2013/02/screen-shot-2013-02-17-at-11-49-56-pm.png "bright_and_contrast")

![hue and satu](http://littlecheesecake.files.wordpress.com/2013/02/screen-shot-2013-02-17-at-11-48-58-pm.png?w=300 "hue_and_satu")

Then if the filter favors a particular color, can adjust the color balance a bit. Or can also add a filter layer on top then blend it with the base image. __Hudson__ is a filter to make the image have an icy look, the slight tint and altered lighting give the images a colder feel. Therefore a radial gradient mask with blue to black is used for blending. With a lot trials, I found that "overlay" blending mode is best suitable to produce the vignetting effect (darkens the corners). _Overlay_ blending is a combination of _multiply_ and _screen_. It darkens the darker part and brightens the lighter part of the base image. And we can adjust the opacity of the top filtering layer to make the effect less strong.

![color](http://littlecheesecake.files.wordpress.com/2013/02/screen-shot-2013-02-17-at-11-50-27-pm.png?w=300 "color")

![blend mode](http://littlecheesecake.files.wordpress.com/2013/02/screen-shot-2013-02-18-at-10-06-04-am.png "blend mode")

Basically that's it! Just simply three steps: 1) adjust B/C/S, 2) adjust color balance, 3) _overlay_ blend a radial gradient layer with some opacity. Let's see the result. Emm.. not exactly, but similar.

![husdon_ps](http://littlecheesecake.files.wordpress.com/2013/02/husdon_ps1.png?w=300 "husdon_ps")

### Implement using OpenGL ES Shaders

So, I have three things to do: adjust the B/C/S, change the color balance and add a new layer of texture to overlay blend on top.

__1. Adjust the B/C/S__

In my previous post, I have discussed all the three processes, I just combine them into a single function in the Fragment Shader.

```c
vec3 BrightnessContrastSaturation(vec3 color, float brt, float con, float sat){
    vec3 black = vec3(0., 0., 0.);
    vec3 middle = vec3(0.5, 0.5, 0.5);
    float luminance = dot(color, W);
    vec3 gray = vec3(luminance, luminance, luminance);
    vec3 brtColor = mix(black, color, brt);
    vec3 conColor = mix(middle, brtColor, con);
    vec3 satColor = mix(gray, conColor, sat);
    return satColor;
}

```
__2. Adjust color__

I slightly adjust the single channel.

```c
//add blue
vec3 blue_result = vec3(bcs_result.r, bcs_result.g, bcs_result.b * 1.1);
```

__3. Overlay blending__

The function of overlay blending:

![overlay_eqn](http://media.virbcdn.com/files/6a/f700a71dc09cc92e-overlay_eqn.png "overlay_eqn")

```c
vec3 ovelayBlender(vec3 Color, vec3 filter)
{
    vec3 filter_result;
    float luminance = dot(filter, W); 
    if(luminance < 0.5)
        filter_result = 2. * filter * Color;
    else
        filter_result = 1. - (1. - (2. *(filter - 0.5)))*(1. - Color); 
    return filter_result;
}
```

The result from shader implementation:

![husdon_gl](http://littlecheesecake.files.wordpress.com/2013/02/husdon_gl.png?w=260 "husdon_gl")

###Some Other Exploration

Similarly I create more filters to mimic the `Instagram®` style.

* I add noise at the edge of the top filter lay, to produce the over- exposured film effect in __Hefe__ filter

* Give high saturation to vibrate the color and to emphasize the dark corners to get __X-pro__

* Reduce the saturation and add a lot of red and yellow, to give the feel of __Rise__ filter

* Add a red to purple filter to get the __Toaster__ effect


![hefe_gl](http://littlecheesecake.files.wordpress.com/2013/02/hefe_gl.png?w=160 "hefe_gl")  ![xpro_gl](http://littlecheesecake.files.wordpress.com/2013/02/xpro_gl.png?w=160 "xpro_gl")  ![rise_gl](http://littlecheesecake.files.wordpress.com/2013/02/rise_gl.png?w=160 "rise_gl")  ![toaster_gl](http://littlecheesecake.files.wordpress.com/2013/02/toaster_gl.png?w=160 "toaster_gl")

###Closure

This is so fun! Weeks ago I was told by my friend of the words 

>__CONNECTING THE DOTS__ 

I start to realize what it means. Find the things you are good at and try to make connection and produce better work on it. I will definitely continue with this work. I am also glad to share with anyone who are interested in this area, find the full implementation of the basic image processing using OpenGL ES Shaders in Android from my github.

####Reference
[1] [Texture in OpenGL ES](http://www.learnopengles.com/android-lesson-four-introducing-basic-texturing/)

[2] [Image Processing using Shaders](http://books.google.com.sg/books/about/Graphics_Shaders.html?id=29YSpc-aOlgC&redir_esc=y)

