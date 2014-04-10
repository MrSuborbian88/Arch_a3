package edu.cmu.a3.SystemB;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.ADevice;
import edu.cmu.a3.common.MessageCodes;

public class SprinklerController extends ADevice {

	public static final String VALUE_TYPE = "SprinklerController";
	public static final String VALUE_DESCRIPTION = "Sprinkler Controller Description";
	public static final String VALUE_ARMED = "armed";
	public static final String VALUE_CLEARED = "clear";

	public static final String VALUE_ON = "off";
	public static final String VALUE_OFF = "on";


	public static final String KEY_STATUS = "status";
	public static final String KEY_SENSORIDS = "sensorids";
	
	public static final String KEY_COUNTDOWN_RESPONSE = "response";
	public static final String KEY_COUNTDOWN_ID = "response";
	public static final String KEY_COUNTDOWN_TIME = "secondsleft";

	public static final String VALUE_COUNTDOWN_YES = "start";
	public static final String VALUE_COUNTDOWN_NO = "cancel";

	protected static final Integer [] msg_ids = {MessageCodes.FIRE_SYSTEM_ALARM,
		MessageCodes.COUNTDOWN_RESPONSE,
		MessageCodes.SET_SPRINKLER};

	boolean activated = false;
	boolean sprinkling = false;


	private Map<String,CountdownThread> countdownThreads;

	public SprinklerController(String id) throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION,msg_ids);
		countdownThreads = new HashMap<String,CountdownThread>();

	}

	public SprinklerController(String id, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION, msg_ids, serverIp);
		countdownThreads = new HashMap<String,CountdownThread>();
	}

	@Override
	protected void handleMessage(Message msg) {
		if(msg == null)
			return;
		Map<String,String> values = getMapFromString(msg.GetMessage());

		//arm/clear the system
		if(msg.GetMessageId() == MessageCodes.FIRE_SYSTEM_ALARM) {
			/*TODO - if fire system alarm determined to be on, turn sprinklers on*/
			if(values.containsKey(FireAlarmController.KEY_STATUS)) {
				if(values.get(FireAlarmController.KEY_STATUS).equals(FireAlarmController.VALUE_ARMED)) {
					startCountdown();
				}
				else {
					stopSprinklers();
				}
			}
		}

		else if(msg.GetMessageId() == MessageCodes.COUNTDOWN_RESPONSE) {
			/*TODO - if there is no input from user within 10 seconds, turn sprinklers on*/
			if(values.containsKey(KEY_COUNTDOWN_RESPONSE) && values.containsKey(KEY_COUNTDOWN_ID)) {
				if(values.get(KEY_COUNTDOWN_RESPONSE).equals(VALUE_COUNTDOWN_YES)) {
					this.stopCountdown(values.get(KEY_COUNTDOWN_ID));
				}
				else if(values.get(KEY_COUNTDOWN_RESPONSE).equals(VALUE_COUNTDOWN_NO)) {
					//Stop countdown and start sprinklers
					this.stopCountdown(values.get(KEY_COUNTDOWN_ID));
					this.startSprinklers();
				}
			}
			
			
		}
		else if(msg.GetMessageId() == MessageCodes.SET_SPRINKLER) {
			if(values.containsKey(KEY_STATUS)) {
			/*TODO - set sprinkler to TRUE when sprinklers already on*/
				if(values.get(KEY_STATUS).equals(VALUE_ON) && !sprinkling)
					this.startSprinklers();
				else if(values.get(KEY_STATUS).equals(VALUE_OFF) && sprinkling)
					this.stopSprinklers();
			
			}

		}
	}
	private void stopSprinklers() {
		sprinkling = false;
		this.sendStopSprinklers();
	}

	private void startSprinklers() {
		activated = true;
		startCountdown();
	}

	private void startCountdown() {
		CountdownThread countdownThread = new CountdownThread();
		countdownThread.start();
		countdownThreads.put(countdownThread.id, countdownThread);
		
	}
	private void stopCountdown(String id) {
		if(countdownThreads.containsKey(id)) {
			countdownThreads.get(id).finish();
			countdownThreads.remove(id);
		}
		activated = false;
		
	}
	private class CountdownThread extends Thread{
		public String id;
		public CountdownThread() {
			this.id = edu.cmu.a3.common.Util.createRandomId("", 5);
		}
		private boolean active;
		private int time = 10;
		public void run() {
			active = true;
			while(active && time > 0) {
				sendCountdownMessage(id,time);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				time--;
			}
			if(time > 0)
				sendStopSprinklers();
			else
				sendStartSprinklers();
		}
		public void finish() {
			active = false;
		}
	}
	private void sendStopSprinklers() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(KEY_STATUS, VALUE_CLEARED);
		try {
			sendMessage(MessageCodes.SET_SPRINKLER,values);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void sendCountdownMessage(String id, int time) {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(KEY_COUNTDOWN_ID, id);
		values.put(KEY_COUNTDOWN_TIME, String.valueOf(time));
	}
	private void sendStartSprinklers() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(KEY_STATUS, VALUE_ARMED);
		try {
			sendMessage(MessageCodes.SET_SPRINKLER,values);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handlePong(Message msg) {
		//No action needed
	}


	public static void main(String [] args) {
		try {
			String name = "";
			if(args.length > 0)
				name = args[0];
			if(name.equals("")) {
				name = edu.cmu.a3.common.Util.createRandomId("MotionSensor_",2);
			}

			SprinklerController ws = new SprinklerController(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
