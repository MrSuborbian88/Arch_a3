package edu.cmu.a3.SystemB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.ADevice;
import edu.cmu.a3.common.MessageCodes;

public class FireAlarmController extends ADevice {

	public static final String VALUE_TYPE = "FireAlarmController";
	public static final String VALUE_DESCRIPTION = "Fire Alarm Controller Description";
	public static final String VALUE_ARMED = "armed";
	public static final String VALUE_CLEARED = "clear";

	public static final String KEY_STATUS = "status";
	public static final String KEY_SENSORIDS = "sensorids";

	protected static final Integer [] msg_ids = {MessageCodes.FIRE_SENSOR_ALARM};

	private Map<String,Device> sensors;

	public FireAlarmController(String id) throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION,msg_ids);
		sensors = new HashMap<String,Device>();

	}

	public FireAlarmController(String id, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION, msg_ids, serverIp);
		sensors = new HashMap<String,Device>();
	}

	@Override
	protected void handleMessage(Message msg) {
		if(msg == null)
			return;
		Map<String,String> values = getMapFromString(msg.GetMessage());

		//Received fire alarm message
		if(msg.GetMessageId() == MessageCodes.FIRE_SENSOR_ALARM) {

			if(values.containsKey(ADevice.KEY_ID) && values.containsKey(ADevice.KEY_TYPE)
					&& values.containsKey(FireSensor
							.KEY_STATUS))
			{
				Device d = new Device(
						values.get(ADevice.KEY_ID),
						values.get(ADevice.KEY_TYPE),
						values.get(FireSensor.KEY_STATUS).equals(String.valueOf(FireSensor.VALUE_ARMED)) ?
								true : false
						);
				sensors.put(values.get(ADevice.KEY_ID), d);
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
		values.put(KEY_TYPE, type);
		if(armed_ids.size() > 0)
		{
			values.put(KEY_STATUS, VALUE_ARMED);
			values.put(KEY_SENSORIDS,
					Arrays.toString(armed_ids.toArray(new String[armed_ids.size()])));
			sendMessage(MessageCodes.FIRE_SYSTEM_ALARM,values);
			System.out.println("Active "+type+" :");
			for(String id : armed_ids) {
				System.out.println(id);
			}

		} else if (clear_ids.size() > 0) {
			values.put(KEY_STATUS, VALUE_CLEARED);
			values.put(KEY_SENSORIDS,
					Arrays.toString(clear_ids.toArray(new String[armed_ids.size()])));
			sendMessage(MessageCodes.FIRE_SYSTEM_ALARM,values);
			System.out.println("All "+type+" are inactive.");
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
	public static void main(String [] args) {
		try {
			String name = "";
			if(args.length > 0)
				name = args[0];
			if(name.equals("")) {
				name = edu.cmu.a3.common.Util.createRandomId("FireAlarmController_",2);
			}

			FireAlarmController ws = new FireAlarmController(name);

			System.out.println(name);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
