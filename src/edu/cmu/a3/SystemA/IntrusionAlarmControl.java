package edu.cmu.a3.SystemA;

import java.util.Arrays;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.ADevice;
import edu.cmu.a3.common.MessageCodes;

public class IntrusionAlarmControl extends ADevice {

	public static final String VALUE_TYPE = "IntrusionAlarmControl";

	protected static final Integer [] msg_ids = {MessageCodes.ARM,MessageCodes.SENSOR_ALARM};
	
	boolean armed = false;
	
	public IntrusionAlarmControl(String id) throws Exception {
		super(id, VALUE_TYPE,msg_ids);
		
	}

	public IntrusionAlarmControl(String id, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE, msg_ids, serverIp);
		
	}

	@Override
	protected void handleMessage(Message msg) {
		if(msg == null)
			return;
		Map<String,String> values = getMapFromString(msg.GetMessage());
		//arm/clear the system
		if(msg.GetMessageId() == MessageCodes.ARM) {
			if(values.containsKey(SecurityMonitor.KEY_ARM)) {
				if(values.get(SecurityMonitor.KEY_ARM).equals(String.valueOf(true))) {
					armed = true;
				} 
				else {
					armed = false;
				}
				System.out.println(Arrays.toString(values.values().toArray()));
			}
		}
		else if(msg.GetMessageId() == MessageCodes.SENSOR_ALARM) {
			//add the stateful things to track sensor alarms
			System.out.println(Arrays.toString(values.values().toArray()));
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
