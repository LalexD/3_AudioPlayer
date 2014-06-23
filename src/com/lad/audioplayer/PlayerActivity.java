package com.lad.audioplayer;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayerActivity extends Activity {

	Button btnPlay;
	TextView tvStatus, tvName;
	PlayService playService;
	Intent intent;
	ServiceConnection sCon;
	boolean connecting;
	SeekBar volumeControl;
	AudioManager audioManager;
	BroadcastReceiver Br_status;
	public final static String PARAM_STATUS="status";
	public final static int STATUS_PLAY=1;
	public final static int STATUS_PAUSE=2;
	public final static int STATUS_IDLE=3;
	public final static String BROADCAST_STATUS_ACTION="com.lad.audioplayer.broadcast_status";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_layout);

		btnPlay=(Button) findViewById(R.id.btnPlay);
		tvStatus=(TextView) findViewById(R.id.tvStatusCheck);
		tvName=(TextView) findViewById(R.id.tvName);
		tvName.setText("Music.mp3");
		
		intent=new Intent(this,PlayService.class);
		startService(intent);
		
		sCon=new ServiceConnection(){
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				playService=((PlayService.MyBinder) service).getService();
				connecting=true;
				playService.checkStatus();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				connecting=false;
				
			}
		};
		
		// BroadcastReceiver
		
		Br_status=new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				int status=intent.getIntExtra(PARAM_STATUS, 0);
				
				switch(status){
				case STATUS_PLAY:
					btnPlay.setText(R.string.pause);
					tvStatus.setText(R.string.play);
					break;
				case STATUS_PAUSE:
					btnPlay.setText(R.string.play);
					tvStatus.setText(R.string.pause);
					break;
				case STATUS_IDLE:
					btnPlay.setText(R.string.play);
					tvStatus.setText(R.string.idle);
					break;
				}
			}
		};
		
		IntentFilter intFilter=new IntentFilter(BROADCAST_STATUS_ACTION);
		registerReceiver(Br_status,intFilter);
	
		//Volume control
		
		volumeControl = (SeekBar) findViewById(R.id.barVolume);
		audioManager = (AudioManager)getSystemService(this.AUDIO_SERVICE);
	     int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	     int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);	    
	     volumeControl.setMax(maxVolume);
	     volumeControl.setProgress(curVolume);
		 
        volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {            
 
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){                
            	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            	}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
 
           
        });
		
		
	}
	
	@Override 
	public void onStart(){
	super.onStart();
	bindService(intent, sCon,0);
	volumeControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(connecting)unbindService(sCon);
		unregisterReceiver(Br_status);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_player,
					container, false);
			return rootView;
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnPlay:
			if (playService.isPlaying()){
				playService.Pause();				
			}
			else{
				playService.Play();				
			}			
				
			break;
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if((keyCode==KeyEvent.KEYCODE_VOLUME_UP) || (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN) )
	    	volumeControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
	    return super.onKeyDown(keyCode, event);
	}

	
	

	
}
