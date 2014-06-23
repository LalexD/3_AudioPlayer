package com.lad.audioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;

public class PlayService extends Service implements OnCompletionListener {

	MediaPlayer mp;
	MyBinder binder=new MyBinder();
	AudioManager am;
	Intent intent;
	int status=PlayerActivity.STATUS_IDLE;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

	public void onCreate(){
		super.onCreate();
		mp=MediaPlayer.create(this, R.raw.just_do_it);
		mp.setOnCompletionListener(this);
		//am = (AudioManager) getSystemService(AUDIO_SERVICE);
		intent=new Intent(PlayerActivity.BROADCAST_STATUS_ACTION);
		
	}	
	
	
	public void Play(){
		mp.start();
		status=PlayerActivity.STATUS_PLAY;
		checkStatus();
	}
	
	public void Pause(){
		mp.pause();
		status=PlayerActivity.STATUS_PAUSE;	
		checkStatus();
		
	}
	
	public boolean isPlaying(){
		return mp.isPlaying();
	}
	
	public void checkStatus(){
		switch(status){
		case PlayerActivity.STATUS_PLAY:
			intent.putExtra(PlayerActivity.PARAM_STATUS, PlayerActivity.STATUS_PLAY);
			sendBroadcast(intent);
			break;
		case PlayerActivity.STATUS_PAUSE:
			intent.putExtra(PlayerActivity.PARAM_STATUS, PlayerActivity.STATUS_PAUSE);
			sendBroadcast(intent);
			break;
		case PlayerActivity.STATUS_IDLE:
			intent.putExtra(PlayerActivity.PARAM_STATUS, PlayerActivity.STATUS_IDLE);
			sendBroadcast(intent);
			break;
		}
	}
	
	

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		status=PlayerActivity.STATUS_IDLE;
		checkStatus();
	}
	
	
	class MyBinder extends Binder{
	
		PlayService getService(){
			return PlayService.this;
		}
	}
}
