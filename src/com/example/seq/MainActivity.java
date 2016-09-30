package com.example.seq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	Song song;
	
	boolean tick=false;
	boolean playing = false;
	boolean playingLocal = false;
	boolean erase = false;
	
	ArrayList<Integer> buttons = new ArrayList<Integer>();
	ArrayList<ButtonInfo> buttonInfoList = new ArrayList<ButtonInfo>();

	final int numInst = 6;
	final int numSounds = 9;
	String[] instList = {"Drum", "Guitar","Bass", "Steel Guitar","Piano","Recording"};
	String[] soundList = {"Sound 1","Sound 2","Sound 3", "Sound 4", "Sound 5", "Sound 6", "Sound 7", "Sound 8", "Sound 9"};
	int[][] soundIndex = new int[numInst][numSounds];
	
	final int numBeats = 360;
	final int concurSound = 8;
	int[][] songArray = new int[numBeats][concurSound];
	
	int page = 0;
	int numPageMax = numBeats/8;
	int numPage = 1;
	int track = 1;
	
	Spinner spin1, spin2;
	String iChar, sChar;
	
	int inst, sound;
	
	SoundPool sp = new SoundPool (999, AudioManager.STREAM_MUSIC,0);
	
	Timer timer;
	TimerTask playBeat;
	TimerTask moveMarker;
	int count = 0;
	int pageCount = 0;
	
	int per = 250;
	int bpm = 120;
	
	Handler handler = new Handler();
	TextView[] tv;
	
	Button playPage;
	Button playAll;
	Button left;
	Button right;
	Button removePage;
	Button bpmPlus;
	Button bpmMinus;
	Button save;
	Button load;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
	
    @Override
    public void onDestroy(){
    	if (timer != null){
    		timer.cancel();
    		timer.purge();
    		timer = null;
    		count=0;
    		playing=false;
    		playingLocal = false;
    	}
    	sp.release();
    	super.onDestroy();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        spin1 = (Spinner) findViewById(R.id.s1);
        spin2 = (Spinner) findViewById(R.id.s2);
        
        spin1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (erase){
					erase=false;
					Button button = (Button) findViewById(R.id.b35);
					button.setText("Erase: Off");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				if (erase){
					erase=false;
					Button button = (Button) findViewById(R.id.b35);
					button.setText("Erase: Off");
				}	
			}

        });
        
        spin2.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (erase){
					erase=false;
					Button button = (Button) findViewById(R.id.b35);
					button.setText("Erase: Off");
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				if (erase){
					erase=false;
					Button button = (Button) findViewById(R.id.b35);
					button.setText("Erase: Off");
				}	
			}

        });
        
        tv = new TextView[8];
        
        tv[0] = (TextView) findViewById(R.id.TV0);
        tv[1] = (TextView) findViewById(R.id.tV01);
        tv[2] = (TextView) findViewById(R.id.tV02);
        tv[3] = (TextView) findViewById(R.id.tV03);
        tv[4] = (TextView) findViewById(R.id.tV04);
        tv[5] = (TextView) findViewById(R.id.tV05);
        tv[6] = (TextView) findViewById(R.id.tV06);
        tv[7] = (TextView) findViewById(R.id.tV07);
        
        for (int i=0; i<8; i++){
        	tv[i].setText("");
        }
        
        for (int i=0; i<numBeats; i++){
        	for (int j=0; j<concurSound; j++){
        		songArray[i][j]=0;
        	}
        }
        
        loadSounds();
        
        playPage = (Button) findViewById(R.id.b7);
        playAll = (Button) findViewById(R.id.b3);
        left = (Button) findViewById(R.id.b5);
        right = (Button) findViewById(R.id.b6);
        removePage = (Button) findViewById(R.id.b34);
        bpmPlus = (Button) findViewById(R.id.b32);
        bpmMinus = (Button) findViewById(R.id.b9);
        save = (Button) findViewById(R.id.b36);
        load = (Button) findViewById(R.id.b39);
        
        song = new Song();
        
//        List<>
//        buttonInfoList.buttonInfoList = new ArrayList<ButtonInfo>();
    }
    
    public void playPage(View v){
    	if (playing)
    		return;
    	
    	if (timer != null){
    		timer.cancel();
    		timer.purge();
    		timer = null;
    		Button button = (Button) v;
    		playAll.setEnabled(true);
    		bpmPlus.setEnabled(true);
    		bpmMinus.setEnabled(true);
    		removePage.setEnabled(true);
    		save.setEnabled(true);
    		load.setEnabled(true);
    		button.setText("Play Page");
    		playingLocal = false;
    		for (int i=0; i<8; i++){
    			tv[i].setText("");
    		}
    	}
    	
    	else{
    		playingLocal = true;
    		count=page*8;
    		makeTimer();
    		Button button = (Button) v;
    		playAll.setEnabled(false);
    		bpmPlus.setEnabled(false);
    		bpmMinus.setEnabled(false);
    		removePage.setEnabled(false);
    		save.setEnabled(false);
    		load.setEnabled(false);
    		button.setText("Stop");
    	}
    	
    }
    
    public void startStop(View v){
    	if (playingLocal)
    		return;
    	
    	if (timer != null){
    		timer.cancel();
    		timer.purge();
    		timer = null;
    		Button button = (Button) v;
    		playPage.setEnabled(true);
    		bpmPlus.setEnabled(true);
    		bpmMinus.setEnabled(true);
    		removePage.setEnabled(true);
    		left.setEnabled(true);
    		right.setEnabled(true);
    		save.setEnabled(true);
    		load.setEnabled(true);
    		button.setText("Play All");
    		playing = false;
    		for (int i=0; i<8; i++){
    			tv[i].setText("");
    		}
    	}
    	else{
    		playing = true;
    		page=0;
    		count=0;
    		fillButtons();
    		makeTimer();
    		Button button = (Button) v;
    		playPage.setEnabled(false);
    		bpmPlus.setEnabled(false);
    		bpmMinus.setEnabled(false);
    		removePage.setEnabled(false);
    		left.setEnabled(false);
    		right.setEnabled(false);
    		save.setEnabled(false);
    		load.setEnabled(false);
    		button.setText("Stop");
    	}
    }
    
    public void makeTimer(){
	    timer = new Timer();
	    playBeat = new TimerTask(){
		    @Override
		    public void run(){
			    playSound();
		    }
	    };
	    
	    moveMarker = new TimerTask(){
	    	@Override
	    	public void run(){
	    		updateMarker();
	    	}
	    };
	    
	    timer.schedule(playBeat, per, per);
	    timer.schedule(moveMarker, 0, per/2);
    }
    
    public void playSound(){
    	for (int i=0; i<concurSound; i++){
    		if (songArray[count][i] != 0)
    			sp.play(songArray[count][i], 1, 1, 1, 0, 1);
    	}
    	count++;
    	if (playingLocal){
    		count = count%8 + page*8;
    	}else{
    		count = count%(numPage*8);
    	}
    }
    
    public void updateMarker(){
    	handler.post(runnable);
    }
    
    Runnable runnable = new Runnable() {
		@Override
		public void run() {
			for (int i=0; i<8; i++){
				tv[i].setText("");
			}
			tv[count%8].setText("|");
			if (count%8==7&&tick&&!playingLocal){
				pageRight();
			}
			tick=!tick;
		}    	
    };
    
    public void click(View v){
    	String tag = (String) v.getTag();
    	int soundPos = Character.getNumericValue(tag.charAt(0));
    	int timePos = Character.getNumericValue(tag.charAt(2));
    	Button button = (Button) v;
    	if (!erase){
    		getSpinVal();
    		songArray[page*8+timePos][(track-1)*4+soundPos] = soundIndex[inst][sound];
    		button.setText(iChar+sChar);
    	}else{
    		songArray[page*8+timePos][(track-1)*4+soundPos] = 0;
    		button.setText("--");
    	}
    	if (!buttons.contains(button.getId())){
    		buttons.add(button.getId());
    	}
    	for (int i=0; i<buttonInfoList.size(); i++){
    		if (buttonInfoList.get(i).button==button.getId() && buttonInfoList.get(i).page==page && buttonInfoList.get(i).track == track){
    			buttonInfoList.remove(i);
    			break;
    		}
    	}
    	ButtonInfo info = new ButtonInfo();
    	info.button=button.getId();
    	info.page=page;
    	info.track=track;
    	info.text=(String) button.getText();
    	buttonInfoList.add(info);
    }
    
    public void playSample(View v){
    	getSpinVal();
    	sp.play(soundIndex[inst][sound], 1, 1, 0, 0, 1);
    }
    
    public void reset(View v){
    	if (timer != null){
    		timer.cancel();
    		timer.purge();
    		timer = null;
    		playAll.setText("Play All");
    		playPage.setText("Play Page");
    		playPage.setEnabled(true);
    		playAll.setEnabled(true);
    		bpmPlus.setEnabled(true);
    		bpmMinus.setEnabled(true);
    		removePage.setEnabled(true);
    		left.setEnabled(true);
    		right.setEnabled(true);
    		save.setEnabled(true);
    		load.setEnabled(true);
    	}
    	for (int i=0; i<numBeats; i++){
    		for (int j=0; j<concurSound; j++){
    			songArray[i][j]=0;
    			if (i<8)
    				tv[i].setText("");
    		}
    	}
    	while (!buttons.isEmpty()){
    		Button button = (Button) findViewById(buttons.get(0));
    		button.setText("--");
    		buttons.remove(0);	
    	}
    	buttonInfoList.clear();
    	count=0;
    	
    	tick=false;
    	page=0;
    	fillButtons();
    	playing=false;
    	playingLocal=false;
    }
    
    public void resetPage(View v){
    	if (timer != null){
    		timer.cancel();
    		timer.purge();
    		timer = null;
    		playAll.setText("Play All");
    		playPage.setText("Play Page");
    		playPage.setEnabled(true);
    		playAll.setEnabled(true);
    		bpmPlus.setEnabled(true);
    		bpmMinus.setEnabled(true);
    		removePage.setEnabled(true);
    		left.setEnabled(true);
    		right.setEnabled(true);
    		save.setEnabled(true);
    		load.setEnabled(true);
    	}
    	for (int i=0; i<8; i++){
    		for (int j=0; j<concurSound; j++){
    			songArray[i+(page*8)][j]=0;
    			if (i<8)
    				tv[i].setText("");
    		}
    	}
    	while (!buttons.isEmpty()){
    		Button button = (Button) findViewById(buttons.get(0));
    		button.setText("--");
    		buttons.remove(0);	
    	}
    	
    	for (int i=0; i<buttonInfoList.size(); i++){
    		while (i<buttonInfoList.size() && buttonInfoList.get(i).page == page){
    			buttonInfoList.remove(i);
    		}
    	}
    	
    	tick=false;
    	count=page*8;
    	playing=false;
    	playingLocal=false;
    }
    
    public void clickLeft(View v){
    	if(playing)
    		return;
    	pageLeft();
    }
    
    public void pageLeft (){
    	if (page!=0){
    		page--;
    		fillButtons();
    	}else{
    		page = numPage-1;
    		fillButtons();
    	}
    }
    
    public void clickRight(View v){
    	if (playing)
    		return;
    	pageRight();
    }
    
    public void pageRight (){
    	if (page!=numPage-1){
    		page++;
    		fillButtons();
    	}else{
    		page = 0;
    		fillButtons();
    	}
    }
    
    public void fillButtons(){
    	TextView tv = (TextView) findViewById(R.id.tV2);
    	TextView tv2 = (TextView) findViewById(R.id.tV6);
    	TextView tv3 = (TextView) findViewById(R.id.texV4);
		tv.setText(" Page " + Integer.toString(page+1) + "/" + Integer.toString(numPage));
		tv2.setText("Track " + Integer.toString(track));
		tv3.setText("bpm:\n" + bpm);
		per = 60000/(2*bpm);
    	while (!buttons.isEmpty()){
    		Button button = (Button) findViewById(buttons.get(0));
    		button.setText("--");
    		buttons.remove(0);	
    	}
    	for (int i=0; i<buttonInfoList.size(); i++){
    		if (buttonInfoList.get(i).page == page && buttonInfoList.get(i).track == track){
    			Button button = (Button) findViewById(buttonInfoList.get(i).button);
    			if (button != null) {
    			button.setText(buttonInfoList.get(i).text);
    			buttons.add(button.getId());
    			}
    		}
    	}
    }
    
    public void getSpinVal(){
    	String spin1Value = spin1.getSelectedItem().toString();
    	String spin2Value = spin2.getSelectedItem().toString();
    	
    	for (int i=0; i<instList.length; i++){
    		if (instList[i].equals(spin1Value)){
    			iChar = String.valueOf(spin1Value.charAt(0));
    			inst = i;
    		}
    	}
    	
    	for (int i=0; i<soundList.length; i++){
    		if (soundList[i].equals(spin2Value)){
    			sChar = String.valueOf(spin2Value.charAt(spin2Value.length()-1));
    			sound = i;
    		}
    	}
    }
    
    public void minus(View v){
    	if (playing || playingLocal)
    		return;
    	bpm = Math.max(bpm-5, 5);
    	per = 60000/(2*bpm);
    	TextView tv = (TextView) findViewById(R.id.texV4);
    	tv.setText("bpm: \n" + Integer.toString(bpm));
    }
    
    public void plus(View v){
    	if (playing || playingLocal)
    		return;
    	bpm = Math.min(bpm+5, 300);
    	per = 60000/(2*bpm);
    	TextView tv = (TextView) findViewById(R.id.texV4);
    	tv.setText("bpm: \n" + Integer.toString(bpm));
    }
    
    public void addPage(View v){
//    	if (playing || playingLocal)
//    		return;
    	if (numPage < numPageMax){
    		numPage++;
    		fillButtons();
    	}
    }
    
    public void removePage(View v){
    	if (playing || playingLocal)
    		return;
    	if (numPage > 1){
    		numPage--;
    		fillButtons();
    	}
    	if (page >= numPage){
    		page = numPage-1;
    		fillButtons();
    	}
    }

    public void upTrack(View v){
    	if (track==1)
    		return;
    	track=1;
    	fillButtons();
    }
    
    public void downTrack(View v){
    	if (track==2)
    		return;
    	track=2;
    	fillButtons();
    }
    
    public void save(View v){
    	Gson gson = new Gson();
    	String songData;
    	
    	page=0;
    	fillButtons();
    	song.songArray = songArray;
    	song.bpm = bpm;
    	song.numPage = numPage;
    	song.buttonInfoList = buttonInfoList;
    	song.buttons = buttons;
    	
    	songData = gson.toJson(song);
    	
    	EditText songName = (EditText) findViewById(R.id.eT1);
    	
    	String filename = songName.getText().toString();
    	File dest = new File(Environment.getExternalStorageDirectory().getPath()+"/seq/");
    	dest.mkdirs();
    	File file = new File(dest, (filename.toLowerCase() + ".txt"));
    	FileOutputStream fos;
    	byte[] data = new String(songData).getBytes();
    	try {
    	    fos = new FileOutputStream(file);
    	    fos.write(data);
    	    fos.flush();
    	    fos.close();
    	    Toast.makeText(getApplicationContext(), filename + " saved.",
    	    		   Toast.LENGTH_LONG).show();
    	} catch (FileNotFoundException e) {
    		Toast.makeText(getApplicationContext(), "Save failed",
  	    		   Toast.LENGTH_LONG).show();
    	} catch (IOException e) {
    		Toast.makeText(getApplicationContext(), "Save failed",
  	    		   Toast.LENGTH_LONG).show();
    	}
    }
    
    public void load(View v){
    	
    	while (!buttons.isEmpty()){
    		Button button = (Button) findViewById(buttons.get(0));
    		button.setText("--");
    		buttons.remove(0);	
    	}
    	
    	Gson gson = new Gson();
    	
    	StringBuilder songData = new StringBuilder();
    	
    	EditText songName = (EditText) findViewById(R.id.eT1);
    	
    	File src = new File(Environment.getExternalStorageDirectory().getPath()+"/seq/");
    	src.mkdirs();
    	File file = new File(src, songName.getText().toString().toLowerCase() + ".txt");
    	try {
    	    BufferedReader br = new BufferedReader(new FileReader(file));
    	    String line;

    	    while ((line = br.readLine()) != null) {
    	        songData.append(line);
    	        songData.append('\n');
    	    }
    	    br.close();
    	}
    	catch (IOException e) {
    		Toast.makeText(getApplicationContext(), songName.getText().toString() + " does not exist.",
 	    		   Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
    	song = gson.fromJson(songData.toString(), Song.class);
    	songArray = song.songArray;
    	buttonInfoList = song.buttonInfoList;
    	buttons = song.buttons;
    	numPage = song.numPage;
    	bpm = song.bpm;
    	page = 0;
    	fillButtons();
    	Toast.makeText(getApplicationContext(), songName.getText().toString() + " loaded.",
	    		   Toast.LENGTH_LONG).show();
    }
    
    public void erase(View v){
    	if (!erase){
    		erase = true;
    		Button button = (Button) v;
    		button.setText("Erase: On");
    	}else{
    		erase = false;
    		Button button = (Button) v;
    		button.setText("Erase: Off");
    	}
    }
    
    public void loadSounds(){
    	soundIndex[0][0] = sp.load(getApplicationContext(), R.raw.drum_1,1);
    	soundIndex[0][1] = sp.load(getApplicationContext(), R.raw.drum_2,1);
    	soundIndex[0][2] = sp.load(getApplicationContext(), R.raw.drum_3,1);
    	soundIndex[0][3] = sp.load(getApplicationContext(), R.raw.drum_4,1);
    	soundIndex[0][4] = sp.load(getApplicationContext(), R.raw.drum_5,1);
    	soundIndex[0][5] = sp.load(getApplicationContext(), R.raw.drum_6,1);
    	soundIndex[0][6] = sp.load(getApplicationContext(), R.raw.drum_7,1);
    	soundIndex[0][7] = sp.load(getApplicationContext(), R.raw.drum_8,1);
    	soundIndex[0][8] = sp.load(getApplicationContext(), R.raw.drum_9,1);
    	
    	soundIndex[1][0] = sp.load(getApplicationContext(), R.raw.guitar_1,1);
    	soundIndex[1][1] = sp.load(getApplicationContext(), R.raw.guitar_2,1);
    	soundIndex[1][2] = sp.load(getApplicationContext(), R.raw.guitar_3,1);
    	soundIndex[1][3] = sp.load(getApplicationContext(), R.raw.guitar_4,1);
        soundIndex[1][4] = sp.load(getApplicationContext(), R.raw.guitar_5,1);	
        soundIndex[1][5] = sp.load(getApplicationContext(), R.raw.guitar_6,1);	
        soundIndex[1][6] = sp.load(getApplicationContext(), R.raw.guitar_7,1);	
        soundIndex[1][7] = sp.load(getApplicationContext(), R.raw.guitar_8,1);
        soundIndex[1][8] = sp.load(getApplicationContext(), R.raw.guitar_9,1);
      
    	soundIndex[2][0] = sp.load(getApplicationContext(), R.raw.bass_1,1);
    	soundIndex[2][1] = sp.load(getApplicationContext(), R.raw.bass_2,1);
    	soundIndex[2][2] = sp.load(getApplicationContext(), R.raw.bass_3,1);
    	soundIndex[2][3] = sp.load(getApplicationContext(), R.raw.bass_4,1);
    	soundIndex[2][4] = sp.load(getApplicationContext(), R.raw.bass_5,1);
    	soundIndex[2][5] = sp.load(getApplicationContext(), R.raw.bass_6,1);
    	soundIndex[2][6] = sp.load(getApplicationContext(), R.raw.bass_7,1);
    	soundIndex[2][7] = sp.load(getApplicationContext(), R.raw.bass_8,1);
    	soundIndex[2][8] = sp.load(getApplicationContext(), R.raw.bass_9,1);
    	
    	soundIndex[3][0] = sp.load(getApplicationContext(), R.raw.steel_1,1);
    	soundIndex[3][1] = sp.load(getApplicationContext(), R.raw.steel_2,1);
    	soundIndex[3][2] = sp.load(getApplicationContext(), R.raw.steel_3,1);
    	soundIndex[3][3] = sp.load(getApplicationContext(), R.raw.steel_4,1);
    	soundIndex[3][4] = sp.load(getApplicationContext(), R.raw.steel_5,1);
    	soundIndex[3][5] = sp.load(getApplicationContext(), R.raw.steel_6,1);
    	soundIndex[3][6] = sp.load(getApplicationContext(), R.raw.steel_7,1);
    	soundIndex[3][7] = sp.load(getApplicationContext(), R.raw.steel_8,1);
    	soundIndex[3][8] = sp.load(getApplicationContext(), R.raw.steel_9,1);
    	
    	soundIndex[4][0] = sp.load(getApplicationContext(), R.raw.piano_1,1);
    	soundIndex[4][1] = sp.load(getApplicationContext(), R.raw.piano_2,1);
    	soundIndex[4][2] = sp.load(getApplicationContext(), R.raw.piano_3,1);
    	soundIndex[4][3] = sp.load(getApplicationContext(), R.raw.piano_4,1);
    	soundIndex[4][4] = sp.load(getApplicationContext(), R.raw.piano_5,1);
    	soundIndex[4][5] = sp.load(getApplicationContext(), R.raw.piano_6,1);
    	soundIndex[4][6] = sp.load(getApplicationContext(), R.raw.piano_7,1);
    	soundIndex[4][7] = sp.load(getApplicationContext(), R.raw.piano_8,1);
    	soundIndex[4][8] = sp.load(getApplicationContext(), R.raw.piano_9,1);
    	
    	soundIndex[5][0] = sp.load(Environment.getExternalStorageDirectory().getPath()+"/seq/rec_1.3gpp", 1);
    	soundIndex[5][1] = sp.load(Environment.getExternalStorageDirectory().getPath()+"/seq/rec_2.3gpp", 1);
    	soundIndex[5][2] = sp.load(Environment.getExternalStorageDirectory().getPath()+"/seq/rec_3.3gpp", 1);
    	soundIndex[5][3] = sp.load(Environment.getExternalStorageDirectory().getPath()+"/seq/rec_4.3gpp", 1);
    	soundIndex[5][4] = sp.load(Environment.getExternalStorageDirectory().getPath()+"/seq/rec_5.3gpp", 1);
    	soundIndex[5][5] = sp.load(Environment.getExternalStorageDirectory().getPath()+"/seq/rec_6.3gpp", 1);
    	soundIndex[5][6] = sp.load(Environment.getExternalStorageDirectory().getPath()+"/seq/rec_7.3gpp", 1);
    	soundIndex[5][7] = sp.load(Environment.getExternalStorageDirectory().getPath()+"/seq/rec_8.3gpp", 1);
    	soundIndex[5][8] = sp.load(Environment.getExternalStorageDirectory().getPath()+"/seq/rec_9.3gpp", 1);
    	
    	for (int i=0; i<numSounds ;i++){
    		if (soundIndex[5][i]==0){
    			soundIndex[5][i] = sp.load(getApplicationContext(), R.raw.drum_9,1);
        	}
    	}
    }

}