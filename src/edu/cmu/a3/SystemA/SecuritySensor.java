package edu.cmu.a3.SystemA;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.ADevice;
import edu.cmu.a3.common.MessageCodes;

public class SecuritySensor extends ADevice {
	
	public static final String KEY_STATUS = "status";
	public static final String VALUE_ARMED = "armed";
	public static final String VALUE_CLEARED = "clear";

	
	public SecuritySensor(String id, String type,String description, Integer [] msgids) throws Exception {
		super(id, type,description,msgids);
	}

	public SecuritySensor(String id, String type,String description,Integer [] msgids, String serverIp)
			throws Exception {
		super(id, type,description,msgids, serverIp);
	}

	@Override
	protected void handleMessage(Message msg) {
		//Does not need to accept any messages
	}
	@Override
	protected void handlePong(Message msg) {
		//No action needed
	}
	public void sendAlarmMessage(boolean armed) throws NullPointerException, Exception {
		Map<String,String> values = new HashMap<String,String>();
		values.put(KEY_STATUS, armed ? VALUE_ARMED : VALUE_CLEARED);
		sendMessage(MessageCodes.SENSOR_ALARM, values);
	}



}
