package com.amateurbikenerd.directionalSound.data.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import com.amateurbikenerd.directionalSound.data.OneChannelMITData;

public class TestMarshallMIT {
	@Test
	public void testMarshallMIT() throws Exception{
		int elevation = 10;
		int azimuth = 115;
		OneChannelMITData data = new OneChannelMITData("../mit_full", 'L');
		List<Short> actuals = data.getImpulse(elevation, azimuth);
		//actuals.add(new Short((short)5));
		Process p = Runtime.getRuntime().exec("python pyTests/MITData.py ../mit_full " + elevation + " " + azimuth);
		p.waitFor();
		BufferedReader rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String ln = "";
		List<Short> expecteds = new ArrayList<Short>();
		while((ln = rdr.readLine()) != null){
			try{
				expecteds.add(new Short(Short.parseShort(ln)));
			}
			catch(NumberFormatException e){
				
			}
		}
		assertEquals(expecteds.size(), actuals.size());
		int badCount = 0;
		for(int i = 0; i < expecteds.size(); i++){
			assertTrue(expecteds.get(i).shortValue() == actuals.get(i).shortValue());
			if(expecteds.get(i).shortValue() != actuals.get(i).shortValue())
				badCount++;
		}
		
	}
}
