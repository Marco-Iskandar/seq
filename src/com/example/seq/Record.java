package com.example.seq;

import java.io.File;
import java.io.IOException;

import com.example.seq.Home.PlaceholderFragment;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class Record extends ActionBarActivity{
	
	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;
	File dest;
	Button play;
	Button save;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		dest = new File(Environment.getExternalStorageDirectory().getPath()+"/seq/");
    	dest.mkdirs();
		outputFile = dest.toString() + "/temp.3gpp";
		play = (Button) findViewById(R.id.button4);
		save = (Button) findViewById(R.id.button5);
		play.setEnabled(false);
		save.setEnabled(false);
	}
	
	public void record(View v) throws InterruptedException{
		myRecorder = new MediaRecorder();
	    myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
	    myRecorder.setOutputFile(outputFile);
			try {
				myRecorder.prepare();
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
		myRecorder.start();
		
		Thread.sleep(1000);
		myRecorder.stop();
		myRecorder.release();
		myRecorder = null;
		play.setEnabled(true);
		save.setEnabled(true);
	}
	
	public void playOld(View v){
		Spinner spin = (Spinner) findViewById(R.id.spinner1);
		System.out.println(spin.getSelectedItemPosition());
		StringBuilder filename= new StringBuilder();
		filename.append(Environment.getExternalStorageDirectory().toString());
		filename.append("/seq/rec_");
		filename.append(spin.getSelectedItemPosition()+1);
		filename.append(".3gpp");
		
		myPlayer = new MediaPlayer();
		try {
			myPlayer.setDataSource(filename.toString());
		} catch (IllegalArgumentException e) {
			Toast.makeText(getApplicationContext(), "No previous recording.",
	 	    		   Toast.LENGTH_LONG).show();
		} catch (SecurityException e) {
			Toast.makeText(getApplicationContext(), "No previous recording.",
	 	    		   Toast.LENGTH_LONG).show();
		} catch (IllegalStateException e) {
			Toast.makeText(getApplicationContext(), "No previous recording.",
	 	    		   Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "No previous recording.",
	 	    		   Toast.LENGTH_LONG).show();
		}
		try {
			myPlayer.prepare();
		} catch (IllegalStateException e) {
			Toast.makeText(getApplicationContext(), "No previous recording.",
	 	    		   Toast.LENGTH_LONG).show();
			return;
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "No previous recording.",
	 	    		   Toast.LENGTH_LONG).show();
			return;
		}
		myPlayer.start();
	}
	
	public void playNew(View v){
		myPlayer = new MediaPlayer();
		try {
			myPlayer.setDataSource(outputFile);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			myPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myPlayer.start();
	}
	
	public void saveOver(View v){
		Spinner spin = (Spinner) findViewById(R.id.spinner1);
		System.out.println(spin.getSelectedItemPosition());
		StringBuilder filename= new StringBuilder();
		filename.append("/seq/rec_");
		filename.append(spin.getSelectedItemPosition()+1);
		filename.append(".3gpp");
		
		File from = new File (Environment.getExternalStorageDirectory(), "seq/temp.3gpp");
		File to = new File (Environment.getExternalStorageDirectory(), filename.toString());
		from.renameTo(to);
		play.setEnabled(false);
		save.setEnabled(false);
	}
	
	public void back(View v){
		super.onBackPressed();
	}
}
