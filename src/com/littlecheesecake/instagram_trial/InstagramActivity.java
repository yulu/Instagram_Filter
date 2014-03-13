package com.littlecheesecake.instagram_trial;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class InstagramActivity extends Activity {
	GLSurfaceView mView;
	private MenuItem			mItemCapture0;
	private MenuItem			mItemCapture1;
	private MenuItem			mItemCapture2;
	private MenuItem			mItemCapture3;
	private MenuItem			mItemCapture4;
	private MenuItem			mItemCapture5;
	private MenuItem			mItemCapture6;
	private MenuItem			mItemCapture7;
	private MenuItem			mItemCapture8;
	private MenuItem			mItemCapture9;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);   
         requestWindowFeature(Window.FEATURE_NO_TITLE);
        
         mView = new GLSurfaceView(this);
         mView.setEGLContextClientVersion(2);
         mView.setRenderer(new GLLayer(this));
         
         setContentView(mView);
    }

    /** Called when the activity is first created. */
    @Override
    public void onResume() {
        super.onResume();
        mView.onResume();
       
    }
    protected void onPause() {
    	super.onPause(); 
    	mView.onPause();
    }
    
    /**menu button setup*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	mItemCapture0 = menu.add("origin");
    	mItemCapture1 = menu.add("Amaro");
    	mItemCapture2= menu.add("Earlybird");
    	mItemCapture3 = menu.add("Hefe");
    	mItemCapture4 = menu.add("Hudson");
    	mItemCapture5 = menu.add("Mayfair");
    	mItemCapture6= menu.add("Rise");
    	mItemCapture7 = menu.add("Toaster");
    	mItemCapture8= menu.add("Willow");
    	mItemCapture9 = menu.add("Xpro");
        return true;
       
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item == mItemCapture0){		
    		GLLayer.shader_selection = 0;
    		return true;
    	}
    	if (item == mItemCapture1){		
    		GLLayer.shader_selection = GLLayer.AMARO;
    		return true;
    	}
    	if	(item == mItemCapture2){
    		GLLayer.shader_selection = GLLayer.EARLYBIRD;
    		return true;
    	}
    	if	(item == mItemCapture3){
    		GLLayer.shader_selection = GLLayer.HEFE;
    		return true;
    	}
    	if  (item == mItemCapture4){
    		GLLayer.shader_selection = GLLayer.HUDSON;
    		return true;
    	}
    	if (item == mItemCapture5){		
    		GLLayer.shader_selection = GLLayer.MAYFAIR;
    		return true;
    	}
    	if	(item == mItemCapture6){
    		GLLayer.shader_selection = GLLayer.RISE;  		
    		return true;
    	}
    	if	(item == mItemCapture7){
    		GLLayer.shader_selection = GLLayer.TOASTER;   		
    		return true;
    	}
    	if	(item == mItemCapture8){
    		GLLayer.shader_selection = GLLayer.WILLOW;  		
    		return true;
    	}
    	if	(item == mItemCapture9){
    		GLLayer.shader_selection = GLLayer.XPRO;		
    		return true;
    	}
    	
    	return false;
    }
    
}
