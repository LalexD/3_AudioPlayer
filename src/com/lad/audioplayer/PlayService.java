package com.lad.audioplayer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;


public class PlayService extends Service implements OnCompletionListener {

	private MediaPlayer mediaPlayer;
	private MyBinder binder = new MyBinder();
	private Intent intentBroadcast;

	public enum Status_Player {
		PLAY, PAUSE, IDLE
	};

	private Status_Player Current_Status = Status_Player.PLAY;
	

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void onCreate() {
		super.onCreate();
		mediaPlayer = MediaPlayer.create(this, R.raw.just_do_it);
		mediaPlayer.setOnCompletionListener(this);
		intentBroadcast = new Intent(PlayerActivity.BROADCAST_STATUS_ACTION);		
		mediaPlayer.start();
		sendNotif();
	}

	public void Play() {
		mediaPlayer.start();
		Current_Status = Status_Player.PLAY;
		sendBroadcast(intentBroadcast);
	}

	public void Pause() {
		mediaPlayer.pause();
		Current_Status = Status_Player.PAUSE;
		sendBroadcast(intentBroadcast);

	}

	public void callBroadcast() {
		sendBroadcast(intentBroadcast);
	}

	public void playPause() {
		if (Current_Status == Status_Player.PLAY)
			Pause();
		else
			Play();
	}

	public void stopService() {
		if (Current_Status == Status_Player.IDLE) {			
			stopSelf();
		}

	}

	public Status_Player checkStatus() {
		return Current_Status;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Current_Status = Status_Player.IDLE;
		sendBroadcast(intentBroadcast);
		stopService();
	}
	
	class MyBinder extends Binder {
		PlayService getService() {
			return PlayService.this;
		}
	}

	private void sendNotif() {
		Intent notificationIntent = new Intent(this, PlayerActivity.class);
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Playing music")
				.setContentText("Music.mp3")
				.setContentIntent(
						PendingIntent.getActivity(this, 0, notificationIntent,
								PendingIntent.FLAG_CANCEL_CURRENT))
				.setAutoCancel(false);			
		startForeground (1, nBuilder.build());
	}
}
