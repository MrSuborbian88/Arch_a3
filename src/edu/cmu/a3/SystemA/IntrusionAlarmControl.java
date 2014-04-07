package edu.cmu.a3.SystemA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.ADevice;
import edu.cmu.a3.common.MessageCodes;

public class IntrusionAlarmControl extends ADevice {

	public static final String VALUE_TYPE = "IntrusionAlarmControl";
	public static final String VALUE_DESCRIPTION = "Intrusion Alarm Control Description";
	public static final String VALUE_ARMED = "armed";
	public static final String VALUE_CLEARED = "clear";

	public static final String KEY_STATUS = "status";
	public static final String KEY_SENSORIDS = "sensorids";

	protected static final Integer [] msg_ids = {MessageCodes.ARM,MessageCodes.SENSOR_ALARM};

	boolean armed = false;


	private Map<String,Device> sensors;

	public IntrusionAlarmControl(String id) throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION,msg_ids);
		sensors = new HashMap<String,Device>();

	}

	public IntrusionAlarmControl(String id, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION, msg_ids, serverIp);
		sensors = new HashMap<String,Device>();
	}

	@Override
	protected void handleMessage(Message msg) {
		if(msg == null)
			return;
		Map<String,String> values = getMapFromString(msg.GetMessage());
		System.out.println(Arrays.toString(values.values().toArray()));

		//arm/clear the system
		if(msg.GetMessageId() == MessageCodes.ARM) {
			if(values.containsKey(SecurityMonitor.KEY_ARM)) {
				if(values.get(SecurityMonitor.KEY_ARM).equals(String.valueOf(true))) {
					armed = true;
				} 
				else {
					armed = false;
				}
				if(this.armed)
				{
					for(String type : this.getDeviceTypes())
					{
						try {
							messageForAlarms(type);
						} catch (NullPointerException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}						
					}
				}
			}
		}
		else if(msg.GetMessageId() == MessageCodes.SENSOR_ALARM) {
			//add the stateful things to track sensor alarms
			//			System.out.println(Arrays.toString(values.values().toArray()));
			if(values.containsKey(ADevice.KEY_ID) && values.containsKey(ADevice.KEY_TYPE)
					&& values.containsKey(SecuritySensor.KEY_STATUS))
			{

				Device d = new Device(
						values.get(ADevice.KEY_ID),
						values.get(ADevice.KEY_TYPE),
						values.get(SecuritySensor.KEY_STATUS).equals(String.valueOf(SecuritySensor.VALUE_ARMED)) ?
								true : false
						);
				sensors.put(values.get(ADevice.KEY_ID), d);
				if(this.armed)
					try {
						messageForAlarms(d.type);
					} catch (NullPointerException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}

		}	
	}
	private Set<String> getDeviceTypes() {
		Set<String> types = new HashSet<String>();
		for(Device d : sensors.values()) {
			types.add(d.type);
		}
		return types;
	}
	private void messageForAlarms(String type) throws NullPointerException, Exception {
		if(type == null)
			throw new NullPointerException("String type is null");
		ArrayList<String> armed_ids = new ArrayList<String>();
		ArrayList<String> clear_ids = new ArrayList<String>();

		for(Device d : sensors.values()) {
			if(d.type.equals(type)) {
				if(d.state) 
					armed_ids.add(d.name);
				else
					clear_ids.add(d.name); 
			}
		}

		HashMap<String,String> values = new HashMap<String,String>();
		if(armed_ids.size() > 0)
		{
			values.put(KEY_STATUS, VALUE_ARMED);
			values.put(KEY_SENSORIDS,
					Arrays.toString(armed_ids.toArray(new String[armed_ids.size()])));
			sendMessage(MessageCodes.SYSTEM_ALARM,values);
//			System.out.println("Alarms");

		} else if (clear_ids.size() > 0) {
			values.put(KEY_STATUS, VALUE_CLEARED);
			values.put(KEY_SENSORIDS,
					Arrays.toString(clear_ids.toArray(new String[armed_ids.size()])));
			sendMessage(MessageCodes.SYSTEM_ALARM,values);
//			System.out.println("No Alarms");
		}

	}
	@Override
	protected void handlePong(Message msg) {
		//No action needed
	}


	private class Device {
		public String name;
		public String type;
		public boolean state; 
		public Device(String name,String type, boolean state)
		{
			this.name=name;
			this.type=type;
			this.state=state;
		}
	}
	public void sendSystemAlarmMessage() {
		//get all the stateful stuff and send it
	}
	public static void main(String [] args) {
		try {
			IntrusionAlarmControl ws = new IntrusionAlarmControl("2");
			boolean arm = true;
			//			while(true) {
			//				ws.sendArmMessage(arm);
			//				System.out.println("MessageSent");
			//				arm = !arm;
			//				Thread.sleep(10000);
			//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
