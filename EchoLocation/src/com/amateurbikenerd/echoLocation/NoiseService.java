package com.amateurbikenerd.echoLocation;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.IBinder;

import com.amateurbikenerd.echoLocation.math.TwoChannelMITData;

public class NoiseService extends Service {

	private Timer timer;
	//private Queue<short[]> q;
	private short[][] dataBuffers;
	// useless comment
	private int nativeSampleRate;
	private int bufSize;
	private AudioTrack track;
	private Random random;
	private int INTERVAL_MILLISECONDS;
	private static int numBuffers = 20;
	private static int elevation = 0;
	private TwoChannelMITData mitData;
	@Override public void onCreate(){
		AssetManager assets = getAssets();
		try {
			mitData = new TwoChannelMITData(this.getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timer = new Timer();
		nativeSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
		bufSize = AudioTrack.getMinBufferSize(nativeSampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		//bufSize = nativeSampleRate / 6;
		float[] preFFTData = new float[bufSize];
		for(int i = 0; i < preFFTData.length; i++)
			preFFTData[i] = 0;
		int fund_idx = preFFTData.length / 150;
		preFFTData[fund_idx] = 5;
		
		//FloatFFT_1D stereofft = new FloatFFT_1D(bufSize);
		//stereofft.realInverse(preFFTData, false);
		
		random = new Random();
		dataBuffers = new short[numBuffers][];
		for(int i = 0; i < numBuffers; i++){
			short[] data = new short[bufSize];

			for(int j = 0; j < bufSize; j++){

				data[j] = (short)random.nextInt(Short.MAX_VALUE);
			}
			dataBuffers[i] = data;
		}
		track = new AudioTrack(
				AudioManager.STREAM_MUSIC,
				nativeSampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				AudioFormat.ENCODING_PCM_16BIT,
				2 * bufSize,
				AudioTrack.MODE_STREAM
		);
		super.onCreate();
		startService();
	}
	private void startService(){
	
		// must divide by 2 because we're in stereo mode and every other data is taken
		// as right or left channel and played simultaneously
		INTERVAL_MILLISECONDS = (int)((double)(bufSize) / (double)(nativeSampleRate * 2) * 1000);


		timer.scheduleAtFixedRate( new TimerTask() {

			public void run() {
				synchronized(this){
					//System.out.println("playing. Sample rate is + " + nativeSampleRate + ", buffer is " + data.length + " shorts long, interval is " + INTERVAL_MILLISECONDS);
					//Ok, so this is to go around every four seconds
					int azimuth = (int)((double)((System.currentTimeMillis() % 4000) * 360) / 4000d);
//					List<Short> leftImpulseList = mitData.getImpulse('L', elevation, azimuth);
//					short[] leftImpulse = new short[leftImpulseList.size()];
//					for(int i = 0; i < leftImpulseList.size(); i++)
//						leftImpulse[i] = leftImpulseList.get(i).shortValue();
//					List<Short> rightImpulseList = mitData.getImpulse('L', elevation, azimuth);
//					short[] rightImpulse = new short[rightImpulseList.size()];
//					for(int i = 0; i < rightImpulseList.size(); i++)
//						rightImpulse[i] = rightImpulseList.get(i).shortValue();
//					short[] originalBuffer = dataBuffers[random.nextInt(numBuffers)];
//					short[] buffer = new short[originalBuffer.length];
//					for(int i = 0; i < originalBuffer.length; i++)
//						buffer[i] = originalBuffer[i];
					
					//System.out.println(azimuth);
					track.write(dataBuffers[random.nextInt(numBuffers)], 0, bufSize);
					track.play();
				}
			}

		}, 0, INTERVAL_MILLISECONDS);
	}
	@Override
	public void onDestroy(){
		timer.cancel();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}