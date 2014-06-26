package com.iamstt.main;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


public class MatrixImageViewListener implements OnTouchListener{
	private MainActivity ACT;
	private ImageView iv;
	private Point center;
	private float startX;
	private float startY;

	public MatrixImageViewListener(MainActivity act,ImageView v) {
		this.ACT = act;
		this.iv = v;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_CANCEL:
			break;
		case MotionEvent.ACTION_DOWN:
		{
			startX = event.getX();
			startY = event.getY();
			//Startを記録
		}	break;
		case MotionEvent.ACTION_MOVE:
			{
//				Log.d("STTTouch","Move getRotate:"+v.getRotation());
//				Log.d("STTTouch","Move startX:"+startX +" startY:" + startY);

				if(v.getRotation() < -45){
					v.setRotation(-44);
					return true;
				}else if(v.getRotation() > 225){
					v.setRotation(224);
					return true;
				}
			boolean isRevers = false;
			float beforeX = 0.0f;
			if(startX > center.x){
				beforeX = startX - center.x;
			}else{
				beforeX = center.x - startX;
			}
			float beforeY = 0.0f;
			if(startY > center.y){
				beforeY = startY - center.y;
			}else{
				beforeY = center.y - startY;
			}
			float nowX = 0.0f;
			if(event.getX() > center.x){
				nowX = event.getX() - center.x;
			}else{
				nowX = center.x - event.getX();
			}
			float nowY = 0.0f;
			if(nowY > center.y){
				nowY = event.getY() - center.y;
			}else{
				nowY = center.y - event.getY();
			}
			//半径
			double radius  = Math.sqrt(Math.pow(nowX,2)+Math.pow(nowY, 2));
			//垂線の長さはYそのもので、外側にできる三角形の底辺は半径-X
			double outPolybase = radius - beforeX;
			//外側にできる三角形の斜辺*2
			double oblique = Math.sqrt(Math.pow(beforeY,2)+Math.pow(outPolybase,2));
			//2分割した三角形の底辺*2
			double innerbase = Math.sqrt(Math.pow(radius,2) - Math.pow(oblique/2, 2));
			//外側にできる三角刑の斜辺/2でできる直角三角形のなす角*2
			double angle = Math.atan(oblique/innerbase*2)*2;
			//+-は45以上ならXの±で判定、以下だったらYの±で判定
			isRevers = Math.atan(nowY/nowX) <0;
			Log.d("STTTouch","Move getRotate:"+v.getRotation());
			Log.d("STTTouch","Move nasu:"+(Math.atan(nowY/nowX)*180/Math.PI)+" isRevers:" + isRevers + " oblique:"+oblique +"  innerbase:"+innerbase + "  angle:" + angle);
			//求めた円周の差の長さ分の角度回転する

//			startX = event.getX();
//			startY = event.getY();
			v.setRotation((float) (v.getRotation()+(isRevers? -angle*2:angle*2)));//動きが悪く感じたので*2
		}	break;
		case MotionEvent.ACTION_UP:
		{	Log.d("STTTouch","Up ImageX:"+event.getX() +"  imageY:"+event.getY());
			//v.getRotationが-44～+224の間に来るのでそれでVolを設定
			ACT.setVolume(v.getId(),(v.getRotation()+44)/268);
		}	break;
		}
		return true;
	}

	public ImageView getImageView() {
		return iv;
	}

	public void setCenter(Point p){
		this.center = p;
	}

	}
