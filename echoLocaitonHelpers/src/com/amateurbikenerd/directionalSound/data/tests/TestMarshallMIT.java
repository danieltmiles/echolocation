package com.amateurbikenerd.directionalSound.data.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import com.amateurbikenerd.directionalSound.data.MITData;

public class TestMarshallMIT {
	@Test
	public void testMarshallMIT() throws Exception{
		MITData data = new MITData("../mit_full");
		List<Short> actuals = data.getImpulse(10, 115);
		//actuals.add(new Short((short)5));
		Process p = Runtime.getRuntime().exec("python pyTests/MITData.py ../mit_full 10 115");
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
			if(expecteds.get(i).shortValue() != actuals.get(i).shortValue())
				badCount++;
		}
	}
}
