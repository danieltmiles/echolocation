package com.amateurbikenerd.directionalSound.data;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class OneChannelMITData {
	private Hashtable<Integer, Hashtable<Integer, List<Short>>> impulses;
	public OneChannelMITData(String mitDataDirectoryName, char channel){
		if(! (channel == 'R' || channel == 'L'))
			throw new IllegalArgumentException("Channel must be 'L' or 'R'");
		impulses = new Hashtable<Integer, Hashtable<Integer, List<Short>>>();
		File mitDataDirectory = new File(mitDataDirectoryName);
		String[] elevationFolders = mitDataDirectory.list();
		for(int i = 0; i < elevationFolders.length; i++){
			String elevationFolderName = elevationFolders[i];
			if(! (elevationFolderName.length() > 4 && elevationFolderName.substring(0, 4).toLowerCase().equals("elev")))
				continue;
			// count backwards from the end of the file name until the character is
			// not a digit. Use that index to get the elevation number.
			int elevIdx = elevationFolderName.length() - 1;
			while(Character.isDigit(elevationFolderName.charAt(elevIdx)))
				elevIdx--;
			elevIdx++;
			int elevation = Integer.parseInt(elevationFolderName.subSequence(elevIdx, elevationFolderName.length()).toString());
			File elevationFolder = new File(mitDataDirectoryName + "/" + elevationFolderName);
			String[] datFiles = elevationFolder.list();
			for(int j = 0; j < datFiles.length; j++){
				String datFileName = datFiles[j];
				if(datFileName.length() > 0 && datFileName.charAt(0) == channel){
					// Here, we're going to use a char for L or R (left or right)
					// and gather integers for elevation and azimuth
					int indexOfLetterE = datFileName.indexOf('e');
					int indexOfLetterA = datFileName.indexOf('a');
					int felevation = Integer.parseInt(datFileName.substring(1, indexOfLetterE));
					assert(felevation == elevation);
					int azimuth = Integer.parseInt(datFileName.substring(indexOfLetterE + 1, indexOfLetterA));
					List<Short> impulse = readImpulse(mitDataDirectoryName + "/" + elevationFolderName + "/" + datFileName);
					Integer elevationKey = new Integer(elevation);
					if(! impulses.containsKey(elevationKey))
						impulses.put(elevationKey, new Hashtable<Integer, List<Short>>());
					Integer azimuthKey = new Integer(azimuth);
					if(! impulses.get(elevationKey).containsKey(azimuthKey))
						impulses.get(elevationKey).put(azimuthKey, impulse);
				}
			}
		}
	}
	public List<Integer> getElevations(){
		List<Integer> l = new ArrayList<Integer>(impulses.size());
		for(Integer i : impulses.keySet())
			l.add(i);
		return l;
	}
	public List<Integer> getAzimuths(){
		List<Integer> l = new ArrayList<Integer>();
		for(Integer elevation : impulses.keySet()){
			for(Integer azimuth : impulses.get(elevation).keySet()){
				l.add(azimuth);
			}
		}
		return l;
	}
	public List<Short> getImpulse(int elevation, int azimuth){
		if(azimuth == 360)
			azimuth = 0;
		Integer elevationKey = new Integer(elevation);
		Integer azimuthKey = new Integer(azimuth);
		if(! impulses.containsKey(elevationKey))
			return null;
		if(! impulses.get(elevationKey).containsKey(azimuthKey))
			return null;
		return impulses.get(elevationKey).get(azimuthKey);
	}
	private static List<Short> readImpulse(String filename){
		File file = new File(filename);
		int fileLength = (int) file.length();
		if (fileLength % 2 != 0) {
			System.err
			.println("Error, file does not contain an even number of bytes. Invalid format");
			System.exit(1);
		}
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] bytes = new byte[fileLength];
		int read = 0;
		try {
			read = is.read(bytes, 0, fileLength);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (read != fileLength) {
			System.err.println("Error, did not read whole file");
			System.exit(1);
		}
		ByteBuffer byteBuf = ByteBuffer.wrap(bytes);
		List<Short> values = new ArrayList<Short>();
		for(int bytesIdx = 0; bytesIdx < fileLength; bytesIdx += 2){
			values.add(byteBuf.getShort(bytesIdx));
		}
		return values;

	}
}
