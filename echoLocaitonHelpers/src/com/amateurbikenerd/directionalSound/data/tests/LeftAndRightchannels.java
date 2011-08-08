package com.amateurbikenerd.directionalSound.data.tests;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.amateurbikenerd.directionalSound.data.OneChannelMITData;

public class LeftAndRightchannels {
	@Test
	public void LeftAndRightChannelsAreTheSame(){
		OneChannelMITData leftImpulses = new OneChannelMITData("../mit_full", 'L');
		OneChannelMITData rightImpulses = new OneChannelMITData("../mit_full", 'R');
		List<Integer> leftElevations = leftImpulses.getElevations();
		List<Integer> rightElevations = rightImpulses.getElevations();
		Collections.sort(leftElevations);
		Collections.sort(rightElevations);
		assertEquals(leftElevations, rightElevations);
		List<Integer> leftAzimuths = leftImpulses.getAzimuths();
		List<Integer> rightAzimuths = rightImpulses.getAzimuths();
		Collections.sort(leftAzimuths);
		Collections.sort(rightAzimuths);
		assertEquals(leftAzimuths, rightAzimuths);
		
		for(Integer elevation : leftElevations){
			for(Integer azimuth : leftAzimuths){
				List<Short> leftImpulse = leftImpulses.getImpulse(elevation, azimuth);
				List<Short> rightImpulse = rightImpulses.getImpulse(elevation, azimuth);
				List<Short> calculatedRightImpulse = leftImpulses.getImpulse(elevation, 360 - azimuth);
				List<Short> calculatedLeftImpulse = rightImpulses.getImpulse(elevation, 360 - azimuth);
				assertEquals("left impulse not equal to calculated left impulse", leftImpulse, calculatedLeftImpulse);
				assertEquals("right impulse not equal to calculated right impulse", rightImpulse, calculatedRightImpulse);
			}
		}
	}
}
