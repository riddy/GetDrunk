package com.hackathon.getdrunk;

import java.util.concurrent.LinkedBlockingQueue;

import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDistanceIR.DistanceListener;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class DistanceTrigger implements DistanceListener {
    private static final String HOST = "localhost";
    private static final int PORT = 4223;

    private static final String UID = "w3K";
	private BrickletDistanceIR dir;
	private IPConnection ipcon;
	
	private GlassTriggerListener glassListener;
	
	private LinkedBlockingQueue<Integer> measurements = new LinkedBlockingQueue<Integer>();
	
	public DistanceTrigger() {
		
	}
    
    public void initDistanceTrigger(GlassTriggerListener listener){
    	
    	glassListener = listener;
    	
		try{
			ipcon = new IPConnection();
			dir = new BrickletDistanceIR(UID, ipcon);
			ipcon.connect(HOST, PORT); // Connect to brickd
			
			//Add distance listener (parameter has unit mm)
			dir.addDistanceListener(this);
			
			// Set period for distance callback to 0.2s (200ms)
			// Note: The distance callback is only called every 0.2 seconds
			//       if the distance has changed since the last call!
			try {
				dir.setDistanceCallbackPeriod(200);
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Connected to IR sensor");
			
		} catch(Exception e){
			System.out.println("Exceptoion "+e);
		}
		
		
    }
    
    public void closeConnection(){
        System.out.println("Press key to exit");
        try {
			ipcon.disconnect();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void addMeasurement(int measurement){
    	if(measurements.size() > 3) measurements.poll();
    	
    	measurements.add(measurement);
    }

	@Override
	public void distance(int distance) {
		addMeasurement(distance);
		
		int meanValue = 0;
		
		for(Integer m : measurements){
			meanValue += m;
		}
		
		meanValue /= measurements.size();
		
		//System.out.println(meanValue);
		
		if(meanValue != 0){
			if(distance < 200){
				glassListener.glassState(true);
			} else {
				glassListener.glassState(false);
			}
		}
	}

}
