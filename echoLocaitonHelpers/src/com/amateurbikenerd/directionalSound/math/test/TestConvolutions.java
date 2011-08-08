package com.amateurbikenerd.directionalSound.math.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import com.amateurbikenerd.directionalSound.data.OneChannelMITData;
import com.amateurbikenerd.directionalSound.math.Convolutions;

public class TestConvolutions {
	@Test
	public void TestConvolution() throws Exception{
		int elevation = 0;
		int azimuth = 115;
		int sampleLength = 512;
		OneChannelMITData kernels = new OneChannelMITData("../mit_full", 'L');
		List<Short> listKernrel = kernels.getImpulse(elevation, azimuth);
		short[] kernel = new short[listKernrel.size()];
		for(int i = 0; i < listKernrel.size(); i++)
			kernel[i] = listKernrel.get(i).shortValue();
		short[] samples = readInSamples();
		long[] actuals = Convolutions.convolve(samples, kernel);
		long[] expecteds = new long[actuals.length];
		Process p = Runtime.getRuntime().exec("python pyTests/numpyConvolution.py ../mit_full " + elevation + " " + azimuth);
		p.waitFor();
		BufferedReader rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String ln = "";
		int expectedIdx = 0;
		while((ln = rdr.readLine()) != null){
			System.out.println(ln);
			assertTrue(expectedIdx < expecteds.length);
			try{
				expecteds[expectedIdx] = Long.parseLong(ln);
				expectedIdx++;
			}catch(NumberFormatException e){}
		}
		assertTrue(actuals.length == expecteds.length);
		for(int i = 0; i < actuals.length; i++)
			assertEquals(expecteds[i], actuals[i]);
	}

	private short[] readInSamples() throws FileNotFoundException, IOException {
		BufferedReader rdr = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(
								new File("pyTests/512_random_samples.txt")
								)
						)
				);
		String ln = "";
		short[] samples = new short[512];
		int samplesIdx = 0;
		while((ln = rdr.readLine()) != null && samplesIdx < samples.length){
			samples[samplesIdx] = Short.parseShort(ln);
			samplesIdx++;
		}
		return samples;
	}
}
