package com.iamstt.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;
//TEST:変更のテストだよ
public class MainActivity extends Activity {
	private ArrayList<MediaPlayer> rPlayerList;
	private ArrayList<MediaPlayer> lPlayerList;
	private ArrayList<String> rList;
	private ArrayList<String> lList;
	private MainActivity ACT;
	private Button selectr_bt;
	private String m_strInitialDir_r = "/sdcard/";
	private String m_strInitialDir_l = "/sdcard/";
	private boolean layouted = false;
	private TableRow gifParent;
	private MatrixImageViewListener rLis;
	private MatrixImageViewListener lLis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		rPlayerList = new ArrayList<MediaPlayer>();
		lPlayerList = new ArrayList<MediaPlayer>();
		rList = new ArrayList<String>();
		lList = new ArrayList<String>();
		ACT = this;
		View parent = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
		selectr_bt = (Button)parent.findViewById(R.id.selectr_bt);
		selectr_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FileSelectionDialog dlg = new FileSelectionDialog( ACT, "mp3;wav" ,true);
				dlg.show( new File( m_strInitialDir_r ) );
			}
		});
		final Button selectl_bt = (Button) parent.findViewById(R.id.selectl_bt);
		selectl_bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FileSelectionDialog dlg = new FileSelectionDialog( ACT, "mp3;wav" ,false);
				dlg.show( new File( m_strInitialDir_l ) );
			}
		});

		gifParent = (TableRow)parent.findViewById(R.id.gif_parent);
		GifView gv = new GifView(ACT);
		gv.setGif(R.drawable.test);
		gifParent.addView(gv,new TableRow.LayoutParams(-1,250));
		ImageView rVol = (ImageView)parent.findViewById(R.id.volimg_r);
		rVol.setImageDrawable(getResources().getDrawable(R.drawable.vol_r));
		rLis = new MatrixImageViewListener(ACT,rVol);
		rVol.setOnTouchListener(rLis);
		ImageView lVol = (ImageView)parent.findViewById(R.id.volimg_l);
		lVol.setImageResource(R.drawable.vol_r);
		lLis = new MatrixImageViewListener(ACT,lVol);
		lVol.setOnTouchListener(lLis);

		Button play = (Button) parent.findViewById(R.id.play_bt);
		play.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				//再生しちゃう
				try {

					for(MediaPlayer m:rPlayerList){
					  if (!m.isPlaying())
					  {
						  m.prepare();
						  m.seekTo(0);
						  m.start();
					  }
					}
					for(MediaPlayer m:lPlayerList){
					  if (!m.isPlaying())
					  {
						  m.prepare();
						  m.seekTo(0);
						  m.start();
					  }
					}

				} catch (IllegalArgumentException e) {
					Toast.makeText(ACT, "再生処理のところでエラー code:0", Toast.LENGTH_LONG).show();;
					e.printStackTrace();
				} catch (SecurityException e) {
					Toast.makeText(ACT, "再生処理のところでエラー code:1", Toast.LENGTH_LONG).show();;
					e.printStackTrace();
				} catch (IllegalStateException e) {
					Toast.makeText(ACT, "再生処理のところでエラー code:2", Toast.LENGTH_LONG).show();;
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(ACT, "再生処理のところでエラー code:3", Toast.LENGTH_LONG).show();;
					e.printStackTrace();
				}

			}
		});

		Button stop = (Button) parent.findViewById(R.id.stop_bt);
		stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {


				for(MediaPlayer m:rPlayerList){
				  if (m.isPlaying())
				  {
					  m.stop();
					  m.prepareAsync();
					  m.reset();
				  }
				}
				for(MediaPlayer m:lPlayerList){
				  if (m.isPlaying())
				  {
					  m.stop();
					  m.prepareAsync();
					  m.reset();
				  }
				}

			}
		});


		Button list_bt = (Button) parent.findViewById(R.id.list_bt);
		list_bt.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				String rStr = "";
				String lStr = "";
				for(int i = 0 ; i  < rList.size();i++){
					rStr += rList.get(i) + "\n";
				}
				for(int i = 0 ; i  < lList.size();i++){
					lStr += lList.get(i) + "\n";
				}
				AlertDialog.Builder dialog = new AlertDialog.Builder(ACT);
				dialog.setMessage("RIGHT\n"+rStr + "\n\nLEFT\n"+lStr)
				.create().show();
			}
		});
				setContentView(parent);
	}

	@Override
	public void onWindowFocusChanged(boolean isFocus){
		if(!layouted){
			Display disp = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			int width = disp.getWidth()/3;
			Log.d("STT","onWindow W:"+width);
			rLis.setCenter(new Point(width/2,width/2));
			lLis.setCenter(new Point(width/2,width/2));
			rLis.getImageView().setLayoutParams(new TableRow.LayoutParams(width,width));
			lLis.getImageView().setLayoutParams(new TableRow.LayoutParams(width,width));
			layouted = true;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
		        		Uri uri = data.getData();
		        		Log.d("SYOUTOKU","RETURND_PATH:" + uri.toString());
		        		//パスを設定して初期化する

	        			try {
		        			MediaPlayer m = new MediaPlayer();
								m.setDataSource(uri.toString());
		        		if(requestCode == 10){
							m.setVolume(0, (float) 1.0);
		        			rPlayerList.add(m);
		        			rList.add(uri.toString());
		        		}else{
							m.setVolume( (float) 1.0,0);
		        			lPlayerList.add(m);
		        			lList.add(uri.toString());
		        		}

						} catch (IllegalArgumentException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						} catch (IllegalStateException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						} catch (IOException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
		        }
		}

	public void onFileSelect( File file ,boolean isRight){
		try {
			MediaPlayer m = new MediaPlayer();
			m.setDataSource(file.getAbsolutePath());
		if(isRight){
			rPlayerList.add(m);
			rList.add(file.getAbsolutePath());
		}else{
			lPlayerList.add(m);
			lList.add(file.getAbsolutePath());

		}
		} catch (IllegalArgumentException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void setVolume(int resourceId, float volume) {
		if(resourceId == R.id.volimg_r){
			for(MediaPlayer m:rPlayerList){
				if(m.isPlaying())m.setVolume(0.0f, volume);
			}
		}else{
			for(MediaPlayer m:lPlayerList){
				if(m.isPlaying())m.setVolume(volume,0.0f);
			}
		}
	}
}
