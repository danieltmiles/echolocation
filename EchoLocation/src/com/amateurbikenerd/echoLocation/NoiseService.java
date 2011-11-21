package com.amateurbikenerd.echoLocation;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.IBinder;

import com.amateurbikenerd.echoLocation.math.Convolutions;
import com.amateurbikenerd.echoLocation.math.MITData;

public class NoiseService extends Service {

	private Timer timer;
	//private Queue<short[]> q;
	private short[][] dataBuffers;
	// useless comment
	private int nativeSampleRate;
	private int bufSize;
	private int channelSize;
	private AudioTrack track;
	private Random random;
	private int INTERVAL_MILLISECONDS;
	private static int numBuffers = 40;
	private static int elevation = 0;
	private MITData mitData;
	@Override public void onCreate(){
		AssetManager assets = getAssets();
		try {
			JSONObject obj = new JSONObject("{\"foo\":\"bar\"}");
			System.out.println(obj.get("foo"));
			AssetManager assetManager = getAssets();
			for(String thing : assetManager.list("/"))
				System.out.println(thing);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timer = new Timer();
		nativeSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
		bufSize = AudioTrack.getMinBufferSize(nativeSampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		if(bufSize % 2 != 0)
			bufSize++;
		channelSize = bufSize / 2;
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
			short[] data = new short[channelSize];
			for(int j = 0; j < data.length; j++)
				data[j] = (short)random.nextInt(Short.MAX_VALUE);
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
					short[] leftBuffer = dataBuffers[random.nextInt(numBuffers)];
					short[] rightBuffer = dataBuffers[random.nextInt(numBuffers)];
					System.out.println("right " + rightBuffer.length + ", left " + leftBuffer.length);
					long start = System.currentTimeMillis();
					short[][] kernels = MITData.get(azimuth, 0);
					long end = System.currentTimeMillis();
					System.out.println("impulse selection took " + (end - start) + " milliseconds.");
					System.out.println("right impulse size = " + kernels[0].length);
					System.out.println("left impulse size = " + kernels[1].length);
					if(kernels == null)
						System.out.println("kernels was null at (az, ele) = (" + azimuth + ", " + elevation + ")");
					rightBuffer = Convolutions.convolveAndScale(rightBuffer, kernels[0]);
					leftBuffer = Convolutions.convolveAndScale(rightBuffer, kernels[1]);
					track.write(Convolutions.zipper(leftBuffer, rightBuffer), 0, bufSize);
					System.out.println("playing");
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
