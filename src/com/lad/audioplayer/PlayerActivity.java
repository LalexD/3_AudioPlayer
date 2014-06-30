package com.lad.audioplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayerActivity extends Activity {

	private Button playButton;
	private TextView statusTextView, nameTrackTextView;
	private PlayService playService;
	private Intent intent;
	private ServiceConnection playServiceCon;
	private boolean connecting = false;
	private SeekBar volumeControl;
	private AudioManager audioManager;
	private BroadcastReceiver statusReceiver;
	public final static String BROADCAST_STATUS_ACTION = "com.lad.audioplayer.broadcast_status";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_layout);

		playButton = (Button) findViewById(R.id.btnPlay);
		statusTextView = (TextView) findViewById(R.id.tvStatusCheck);
		nameTrackTextView = (TextView) findViewById(R.id.tvName);
		nameTrackTextView.setText("Music.mp3");

		intent = new Intent(this, PlayService.class);

		playServiceCon = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {				
				playService = ((PlayService.MyBinder) service).getService();
				connecting = true;
				playService.callBroadcast();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				connecting = false;
			}
		};

		statusReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				switch (playService.checkStatus()) {
				case PLAY:
					playButton.setText(R.string.pause);
					statusTextView.setText(R.string.play);
					break;
				case PAUSE:
					playButton.setText(R.string.play);
					statusTextView.setText(R.string.pause);
					break;
				case IDLE:
					playButton.setText(R.string.play);
					statusTextView.setText(R.string.idle);
					break;
				}
			}
		};

		IntentFilter intFilter = new IntentFilter(BROADCAST_STATUS_ACTION);
		registerReceiver(statusReceiver, intFilter);

		volumeControl = (SeekBar) findViewById(R.id.barVolume);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		volumeControl.setMax(maxVolume);
		volumeControl.setProgress(curVolume);

		volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

		});

	}

	@Override
	public void onStart() {
		super.onStart();
		bindService(intent, playServiceCon, 0);
		volumeControl.setProgress(audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC));

	}

	@Override
	public void onStop() {
		super.onStop();
		if (connecting)
			unbindService(playServiceCon);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(statusReceiver);
		super.onDestroy();

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPlay:
			if (!connecting) {
				startService(intent);
				bindService(intent, playServiceCon, 0);
			} else
				playService.playPause();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)
				|| (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN))
			volumeControl.setProgress(audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));
		return super.onKeyDown(keyCode, event);
	}

}
