package edu.cmu.a3.SystemA;

import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.ADevice;
import edu.cmu.a3.common.MessageCodes;

public class IntrusionAlarmControl extends ADevice {

	public static final String VALUE_TYPE = "IntrusionAlarmControl";

	public Integer [] relevant_ids = {MessageCodes.ARM,MessageCodes.SENSOR_ALARM};
	
	boolean armed = false;
	
	public IntrusionAlarmControl(String id) throws Exception {
		super(id, VALUE_TYPE);
		
	}

	public IntrusionAlarmControl(String id, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE, serverIp);
		
	}

	@Override
	protected void handleMessage(Message msg) {
		if(msg == null)
			return;
		
		//arm/clear the system
		if(msg.GetMessageId() == MessageCodes.ARM) {
			Map<String,String> values = getMapFromString(msg.GetMessage());
			if(values.containsKey(SecurityMonitor.KEY_ARM)) {
				if(values.get(SecurityMonitor.KEY_ARM).equals(String.valueOf(true))) {
					armed = true;
				} 
				else {
					armed = false;
				}
				
			}
		}
		else if(msg.GetMessageId() == MessageCodes.SENSOR_ALARM) {
			//add the stateful things to track sensor alarms
		}
	}

	public void sendSystemAlarmMessage() {
		//get all the stateful stuff and send it
	}
	
}
