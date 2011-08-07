package com.amateurbikenerd.directionalSound.math;

public class Convolutions {
	/*
	 * I'm giving this to you, as the implementation is rather messy.
	 * This is the version with full "tails" implemented: a value is assigned
	 * whenever samples and the kernel have any overlap at all.
	 * As of June 19, it is only partially tested.
	 */
	public static long[] convolve(short[] samples, short[] kernel) {
		int len = kernel.length;
		long[] result = new long[samples.length + len-1];
		for (int n = 0; n < result.length; n++) {
			double sum = 0;
			for (int i = 0; i<len; i++) {
				if (i<samples.length + (len-1) - n && i >= len-n-1) {
					sum += samples[i+n-len+1]*kernel[len-i-1];
				}
			}
			assert(sum <= Long.MAX_VALUE);
			result[n] = (long)sum;
		}
		return result;
	}
}
