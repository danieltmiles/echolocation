package com.amateurbikenerd.echoLocation.math;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONObject;


public class TwoChannelMITData implements Serializable{
	private static final long serialVersionUID = 6665144965091920175L;
	private Hashtable<Integer, Hashtable<Integer, List<Short>>> leftImpulses;
	private Hashtable<Integer, Hashtable<Integer, List<Short>>> rightImpulses;
	public static void main(String[] args)throws Exception{
		TwoChannelMITData data = new TwoChannelMITData("/home/dmiles/mit_full");
		JSONObject obj = new JSONObject();
		for(int elevation : data.getElevations()){
			obj.put("elevation-" + elevation, "some value");
		}
		System.out.println(obj.toString());
	}
	public TwoChannelMITData(String filesystemDirectory) throws IllegalArgumentException, IllegalAccessException{
		leftImpulses = new Hashtable<Integer, Hashtable<Integer, List<Short>>>();
		rightImpulses = new Hashtable<Integer, Hashtable<Integer, List<Short>>>();
		File mitDataDirectory = new File(filesystemDirectory);
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
			File elevationFolder = new File(filesystemDirectory + "/" + elevationFolderName);
			String[] datFiles = elevationFolder.list();
			for(int j = 0; j < datFiles.length; j++){
				String datFileName = datFiles[j];
				if(datFileName.length() > 0 && (datFileName.charAt(0) == 'L' || datFileName.charAt(0) == 'R')){
					// Here, we're going to use a char for L or R (left or right)
					// and gather integers for elevation and azimuth
					int indexOfLetterE = datFileName.indexOf('e');
					int indexOfLetterA = datFileName.indexOf('a');
					int felevation = Integer.parseInt(datFileName.substring(1, indexOfLetterE));
					assert(felevation == elevation);
					int azimuth = Integer.parseInt(datFileName.substring(indexOfLetterE + 1, indexOfLetterA));
					List<Short> impulse = readImpulse(filesystemDirectory + "/" + elevationFolderName + "/" + datFileName);
					Integer elevationKey = new Integer(elevation);
					Hashtable<Integer, Hashtable<Integer, List<Short>>> impulses = leftImpulses;
					if(datFileName.toUpperCase().charAt(0) == 'R')
						impulses = rightImpulses;
					if(! impulses.containsKey(elevationKey))
						impulses.put(elevationKey, new Hashtable<Integer, List<Short>>());
					Integer azimuthKey = new Integer(azimuth);
					if(! impulses.get(elevationKey).containsKey(azimuthKey))
						impulses.get(elevationKey).put(azimuthKey, impulse);
				}
			}
		}
	}
	public void serialize()throws Exception{
		ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream("/home/dmiles/two_channel_mit_data_serialized_file"));
		out.writeObject(this);
	}
	public static TwoChannelMITData deserialize(InputStream location){
		try{
			ObjectInputStream in = new ObjectInputStream(location);
			return (TwoChannelMITData)in.readObject();
		}catch(IOException e){
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public List<Integer> getElevations(){
		// BOZO: this assumes left and right impulses all have the same elevations
		List<Integer> l = new ArrayList<Integer>(leftImpulses.size());
		for(Integer i : leftImpulses.keySet())
			l.add(i);
		return l;
	}
	public List<Integer> getAzimuths(){
		// BOZO: this assumes left and right impulses all have the same azimuths
		List<Integer> l = new ArrayList<Integer>();
		for(Integer elevation : leftImpulses.keySet()){
			for(Integer azimuth : leftImpulses.get(elevation).keySet()){
				l.add(azimuth);
			}
		}
		return l;
	}
	public List<Short> getImpulse(char channel, int elevation, int azimuth){
		Hashtable<Integer, Hashtable<Integer, List<Short>>> impulses = leftImpulses;
		if(channel == 'R')
			impulses = rightImpulses;
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
