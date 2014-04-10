package edu.cmu.a3.SystemA;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.AMonitor;
import edu.cmu.a3.common.MessageCodes;

public class SecurityMonitor extends AMonitor {
	

	public static final String VALUE_TYPE = "SecurityMonitor";

	public static final String VALUE_DESCRIPTION = "Security Monitor Description";
	private boolean armed;
	
	public SecurityMonitor(String id) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {MessageCodes.SYSTEM_ALARM});
		armed = false;
	}
	public SecurityMonitor(String id,String serverIp) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {MessageCodes.SYSTEM_ALARM}, serverIp);
		armed = false;
	}

	public static String KEY_ARM = "arm";


	
	
	@Override
	protected void handleMessage(Message msg) {
		Map<String,String> values = getMapFromString(msg.GetMessage());

		//		System.out.println(Arrays.toString(values.keySet().toArray()));
//		System.out.println(Arrays.toString(values.values().toArray()));
		
		if(msg.GetMessageId() == MessageCodes.SYSTEM_ALARM) {
			/*"{
		    Status: ""ALARM"",
		    Type: ""WINDOW"",
		    SensorIDs: [""WINDOW_1"", ""WINDOW_2"", ""WINDOW_4""]
		}"" +
		    */
			if(values.containsKey(IntrusionAlarmControl.KEY_STATUS) && 
					values.containsKey(IntrusionAlarmControl.KEY_SENSORIDS) &&
					values.containsKey(IntrusionAlarmControl.KEY_TYPE)) {
				String sensors = values.get(IntrusionAlarmControl.KEY_SENSORIDS);
				if(sensors.startsWith("[")) {
					sensors = sensors.substring(1);
				}
				if(sensors.endsWith("]")) {
					sensors = sensors.substring(0,sensors.length()-1);
				}
				if(values.get(IntrusionAlarmControl.KEY_STATUS).
						equals(IntrusionAlarmControl.VALUE_ARMED))
				{
				System.out.println("Alarming " + values.get(IntrusionAlarmControl.KEY_TYPE) 
						+ ": " + sensors);
				}
				else if(values.get(IntrusionAlarmControl.KEY_STATUS).
						equals(IntrusionAlarmControl.VALUE_CLEARED)) {
					System.out.println("Cleared " + values.get(IntrusionAlarmControl.KEY_TYPE) 
							+ ": " + sensors);	
				}
			}
		}
		

	}

	public void armSystem(boolean armed) {

		this.armed =armed;
		Map<String,String> values = new HashMap<String,String>();
		values.put(KEY_ARM, String.valueOf(armed));
		try {
			sendMessage(MessageCodes.ARM,values);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void deviceChange() {
		// No action needed		
	}
	
	public static void main(String[] args) {
		try {
			String name = "";
			if(args.length > 0)
				name = args[0];
			if(name.equals("")) {
				name = edu.cmu.a3.common.Util.createRandomId("DoorSensor_",2);
			}
			SecurityMonitor sm = new SecurityMonitor(name);
			boolean arm = false;
			sm.armSystem(arm);
			while(true) {
				if(arm)
					System.out.println("Press Enter to Disarm System:");
				else
					System.out.println("Press Enter to Arm System:");
				edu.cmu.a3.common.Util.getNextEnter();
				arm = !arm;
				sm.armSystem(arm);
				if(arm)
					System.out.println("System is armed!");
				else
					System.out.println("System is disarmed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}


