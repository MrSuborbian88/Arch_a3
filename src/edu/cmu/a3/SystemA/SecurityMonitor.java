package edu.cmu.a3.SystemA;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.SystemB.FireAlarmController;
import edu.cmu.a3.SystemB.SprinklerController;
import edu.cmu.a3.TermioPackage.Termio;
import edu.cmu.a3.common.AMonitor;
import edu.cmu.a3.common.MessageCodes;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SecurityMonitor extends AMonitor {
	

	public static String KEY_ARM = "arm";
	public static final String VALUE_TYPE = "SecurityMonitor";
	public static final String VALUE_DESCRIPTION = "Security System Monitor";
	
	
	private boolean armed;
	
	private String countdownId;
	private String countdownString;
	private boolean countdownResponseSent;
	private Lock countdownLock = new ReentrantLock();
	
	private boolean sprinkerSetOptionActive = false;
	private boolean sprinklersOn = false;
	
	private Map<String, String> intrusionAlarmState = new HashMap<String, String>();
	private String fireAlarmState = null;
	
	public SecurityMonitor(String id) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {MessageCodes.INTRUSION_SYSTEM_ALARM, 
				MessageCodes.FIRE_SYSTEM_ALARM, MessageCodes.SPRINKLER_STATUS, MessageCodes.COUNTDOWN});
		armed = false;
	}
	public SecurityMonitor(String id,String serverIp) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {MessageCodes.INTRUSION_SYSTEM_ALARM, 
				MessageCodes.FIRE_SYSTEM_ALARM, MessageCodes.SPRINKLER_STATUS, MessageCodes.COUNTDOWN}, serverIp);
		armed = false;
	}
		
	@Override
	protected void handleMessage(Message msg) {
		Map<String,String> values = getMapFromString(msg.GetMessage());
		
		switch(msg.GetMessageId()) {
		
		case MessageCodes.INTRUSION_SYSTEM_ALARM:
			handleSystemIntrusionAlarm(values);
			break;
			
		case MessageCodes.FIRE_SYSTEM_ALARM:
			handleSystemFireAlarm(values);
			break;
			
		case MessageCodes.COUNTDOWN:
			handleCountdown(values);
			break;
			
		case MessageCodes.SPRINKLER_STATUS:
			handleSprinklerStatus(values);
			break;
			
		default:
			break;
		}
	}
	
	private void handleSystemIntrusionAlarm(Map<String,String> values) {
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
			
			if(values.get(IntrusionAlarmControl.KEY_STATUS).equals(IntrusionAlarmControl.VALUE_ALARM))
			{
				String alarmString = "System Alarm -- Sensor Type: " + values.get(IntrusionAlarmControl.KEY_TYPE) 
						+ ", Sensor ID: " + sensors;
				
				intrusionAlarmState.put(values.get(IntrusionAlarmControl.KEY_TYPE), alarmString);
			
			} else if(values.get(IntrusionAlarmControl.KEY_STATUS).equals(IntrusionAlarmControl.VALUE_CLEARED)) {
				intrusionAlarmState.remove(values.get(IntrusionAlarmControl.KEY_TYPE));
			}
			
			updateMonitor();
		}
	}
	
	private void handleSystemFireAlarm(Map<String, String> values) {
		if(values.containsKey(FireAlarmController.KEY_STATUS)) {
			if(values.get(FireAlarmController.KEY_STATUS).equals(FireAlarmController.VALUE_ARMED)) {
				fireAlarmState = "Fire detected by " + values.get(KEY_ID);
			}
			else {
				fireAlarmState = null;
			}
			
			updateMonitor();
		}
	}
	
	private void handleCountdown(Map<String, String> values) {
		try {
			countdownLock.lock();
			if(countdownId != values.get(SprinklerController.KEY_COUNTDOWN_ID)) {
				countdownResponseSent = false;
			}			
			countdownId = values.get(SprinklerController.KEY_COUNTDOWN_ID);
			
			if(!countdownResponseSent && values.containsKey(SprinklerController.KEY_COUNTDOWN_TIME)) {
				
				if(values.get(SprinklerController.KEY_COUNTDOWN_TIME).equals("0")) {
					countdownString = null;
				} else {
					countdownString = "Sprinklers will go off in " + 
							values.get(SprinklerController.KEY_COUNTDOWN_TIME) + " seconds.";	
				}
				
				updateMonitor();			
			}			
		} finally {
			countdownLock.unlock();
		}
		
	}
	
	private void handleSprinklerStatus(Map<String, String> values) {
		if(values.containsKey(SprinklerController.KEY_STATUS)) {
			
			if(values.get(SprinklerController.KEY_STATUS).equals(SprinklerController.VALUE_ON)) {
				sprinklersOn = true;
			} else {
				sprinklersOn = false;
			}
			
			updateMonitor();
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

	public void setSprinklers(boolean on) {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(SprinklerController.KEY_STATUS, on ? SprinklerController.VALUE_ON : SprinklerController.VALUE_COUNTDOWN_CANCEL);

		try {
			sendMessage(MessageCodes.SET_SPRINKLER,values);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void sendCountdownResponse(boolean confirm) {
		try {
			countdownLock.lock();
			HashMap<String,String> values = new HashMap<String,String>();
			
			values.put(SprinklerController.KEY_COUNTDOWN_ID, countdownId);
			
			if(confirm) {
				values.put(SprinklerController.KEY_COUNTDOWN_RESPONSE,SprinklerController.VALUE_COUNTDOWN_CONFIRM);
			} else {
				values.put(SprinklerController.KEY_COUNTDOWN_RESPONSE,SprinklerController.VALUE_COUNTDOWN_CANCEL);
			}
			
			try {
				sendMessage(MessageCodes.COUNTDOWN_RESPONSE,values);
				countdownString = null;
				countdownResponseSent = true;
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}finally {
			countdownLock.unlock();
		
		}
		
	}

	
	@Override
	protected void deviceChange() {
		// No action needed		
	}
	
	
	private void printOptionMenu() {
		System.out.println("===========================================");
		System.out.println( "System State: \n" );
		
		if(armed) {
			System.out.println( "Intrusion System Armed\n" );
		} else {
			System.out.println( "Intrusion System Disarmed\n" );
		}
		
		if(intrusionAlarmState.size() > 0) {
			System.out.println( "Intrusion Alarms:" );
			for(Entry<String, String> alarm : intrusionAlarmState.entrySet()) {
				System.out.println( "\t" + alarm.getValue() );				
			}			
			System.out.println();
		} else {
			System.out.println( "No Intrusion Alarms" );
		}
		
		if(fireAlarmState != null) {
			System.out.println(fireAlarmState);
			System.out.println();
		} else {
			System.out.println("No Fire Alarms");
		}
				
		if(sprinklersOn) {
			System.out.println( "Sprinklers On\n" );
		} else {
			System.out.println( "Sprinklers Off\n" );
		}
		
		if(countdownString != null) {
			System.out.println(countdownString);
			System.out.println();
		}
		
		System.out.println("===========================================\n");
		System.out.println( "Select an Option: \n" );
		
		if(!armed) {
			System.out.println( "1: Arm Instrusion Alarm System" );
		} else {
			System.out.println( "1: Disarm Intrusion Alarm System" );
		}
		
		if(sprinklersOn) {
			System.out.println("2: Shut off sprinklers");
		}
		
		if(countdownString != null) {
			System.out.println("3: Confirm sprinklers");
			System.out.println("4: Cancel sprinklers");
		}
		
		System.out.println("X: Quit");

		System.out.print( "\n>>>> " );
	}
	
	public void updateMonitor() {
		printOptionMenu();	
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
			Termio UserInput = new Termio();
			String option = null;
			
			boolean arm = false;
			
			
			sm.armSystem(arm);
			
			
			while(true) {
				sm.updateMonitor();
				
				option = UserInput.KeyboardReadString();
				
				if(option.equals("1")) {
					arm = !arm;
					sm.armSystem(arm);
				} else if (option.equals("2")) {
					sm.setSprinklers(false);
				} else if (option.equals("3")) {
					sm.sendCountdownResponse(true);
				} else if (option.equals("4")) {
					sm.sendCountdownResponse(false);
				} else if (option.equals("X")) {
					sm.halt();
					break;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}


