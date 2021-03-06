package edu.cmu.a3.SystemB;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.ADevice;
import edu.cmu.a3.common.MessageCodes;

public class FireSensor extends ADevice {
	
	public static final String KEY_STATUS = "status";
	public static final String VALUE_ARMED = "armed";
	public static final String VALUE_CLEARED = "clear";
	
	public static final String VALUE_TYPE = "FireSensor";
	public static final String VALUE_DESCRIPTION = "Fire Sensor Description";


	
	public FireSensor(String id) throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION,new Integer [] {});
	}

	public FireSensor(String id, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION,new Integer [] {}, serverIp);
	}

	@Override
	protected void handleMessage(Message msg) {
		//Does not need to accept any messages
	}
	@Override
	protected void handlePong(Message msg) {
		//No action needed
	}
	public void sendArmMessage(boolean armed) throws NullPointerException, Exception {
		Map<String,String> values = new HashMap<String,String>();
		values.put(KEY_STATUS, armed ? VALUE_ARMED : VALUE_CLEARED);
		sendMessage(MessageCodes.FIRE_SENSOR_ALARM, values);
	}
	
	public static void main(String [] args) {

		try {
			String name = "";
			if(args.length > 0)
				name = args[0];
			if(name.equals("")) {
				name = edu.cmu.a3.common.Util.createRandomId("FireSensor_",2);
			}
			FireSensor fs = new FireSensor(name);

			boolean arm = false;
			System.out.println(fs.getId());
			while(true) {
				if(arm)
					System.out.println("Press Enter to Toggle Sensor Off:");
				else
					System.out.println("Press Enter to Toggle Sensor On:");
				edu.cmu.a3.common.Util.getNextEnter();
				arm = !arm;
				fs.sendArmMessage(arm);
				if(arm)
					System.out.println("Sensor is on!");
				else
					System.out.println("Sensor is off!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}




}
