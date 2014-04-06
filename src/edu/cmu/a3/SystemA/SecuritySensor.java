package edu.cmu.a3.SystemA;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.ADevice;
import edu.cmu.a3.common.MessageCodes;

public class SecuritySensor extends ADevice {
	
	public static final String KEY_STATUS = "status";
	
	public SecuritySensor(String id, String type) throws Exception {
		super(id, type);
	}

	public SecuritySensor(String id, String type, String serverIp)
			throws Exception {
		super(id, type, serverIp);
	}

	@Override
	protected void handleMessage(Message msg) {
		//Does not need to accept any messages
	}
	
	public void sendArmMessage(boolean armed) throws NullPointerException, Exception {
		Map<String,String> values = new HashMap<String,String>();
		values.put(KEY_STATUS, String.valueOf(armed));
		sendMessage(MessageCodes.SENSOR_ALARM, values);
	}

}
