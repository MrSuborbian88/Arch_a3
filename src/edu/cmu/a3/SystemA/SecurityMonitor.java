package edu.cmu.a3.SystemA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.AMonitor;
import edu.cmu.a3.common.MessageCodes;

public class SecurityMonitor extends AMonitor {

	public static final String VALUE_TYPE = "WindowSensor";
	
	public static final String VALUE_DESCRIPTION = "Window Sensor Description";

	public SecurityMonitor(String id) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {MessageCodes.SYSTEM_ALARM});
	}
	public SecurityMonitor(String id,String serverIp) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {MessageCodes.SYSTEM_ALARM}, serverIp);
	}

	public static String KEY_ARM = "arm";
	
	
	@Override
	protected void handleMessage(Message msg) {
		Map<String,String> values = getMapFromString(msg.GetMessage());
		
//		System.out.println(Arrays.toString(values.keySet().toArray()));
		System.out.println(Arrays.toString(values.values().toArray()));

	}
	
	public void armSystem(boolean armed) {
		
		Map<String,String> values = new HashMap<String,String>();
		values.put(KEY_ARM, String.valueOf(armed));
		
		try {
			sendMessage(MessageCodes.ARM,values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		try {
			SecurityMonitor sm = new SecurityMonitor("sm01");
			sm.armSystem(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
