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

	boolean activated = false; //if any sensors are active
	boolean sprinkling = false; //state of the sprinklers


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
					startCountdown(values.get(KEY_ID));
					activated = true;
					System.out.println("Fire detected by " + values.get(KEY_ID));
				}
				else {
					//Stop the countdown if the sensor has cleared
					String sensorid = values.get(KEY_ID);
					if(countdownThreads.containsKey(sensorid))
					{
						this.stopCountdown(countdownThreads.get(sensorid).id);
					}
					if(countdownThreads.size() == 0 )
						activated = false;
					System.out.println("Fire cleared by " + values.get(KEY_ID));
				}
			}
		}

		else if(msg.GetMessageId() == MessageCodes.COUNTDOWN_RESPONSE) {
			/*TODO - if there is no input from user within 10 seconds, turn sprinklers on*/
			if(values.containsKey(KEY_COUNTDOWN_RESPONSE) && values.containsKey(KEY_COUNTDOWN_ID)) {
				if(values.get(KEY_COUNTDOWN_RESPONSE).equals(VALUE_COUNTDOWN_YES)) {
					System.out.println("Received a response to the countdown to turn sprinklers on" 
							+ values.get(KEY_COUNTDOWN_ID));
					this.stopCountdown(values.get(KEY_COUNTDOWN_ID));
					this.startSprinklers();
				}
				else if(values.get(KEY_COUNTDOWN_RESPONSE).equals(VALUE_COUNTDOWN_NO)) {
					//Stop countdown and start sprinklers
					System.out.println("Received a response to the countdown to not turn sprinklers on" 
							+ values.get(KEY_COUNTDOWN_ID));
					this.stopCountdown(values.get(KEY_COUNTDOWN_ID));
				}
			}
		}
		else if(msg.GetMessageId() == MessageCodes.SET_SPRINKLER) {
			if(values.containsKey(KEY_STATUS)) {
				/*TODO - set sprinkler to TRUE when sprinklers already on*/
				if(values.get(KEY_STATUS).equals(VALUE_ON))
				{
					if(!sprinkling) {
						System.out.println("Turning on sprinklers!");
						this.startSprinklers();
					}
					else
					{
						System.out.println("Sprinklers are already on!");
					}
				}
				if(values.get(KEY_STATUS).equals(VALUE_OFF)) 
				{
					if(sprinkling) {
						System.out.println("Turning off sprinklers!");
						this.stopSprinklers();
					}
					else
					{
						System.out.println("Sprinklers are already off!");
					}
				}


			}

		}
	}
	private void stopSprinklers() {
		sprinkling = false;
	}

	private void startSprinklers() {
		sprinkling = true;
	}

	private void startCountdown(String sensorId) {
		CountdownThread countdownThread = new CountdownThread();
		countdownThreads.put(sensorId, countdownThread);
		countdownThread.start();
		

	}
	private void stopCountdown(String countdownId) {
		for(String key : countdownThreads.keySet()) {
			if(countdownThreads.get(key).id.equals(countdownId)) {
				countdownThreads.get(key).finish();
				countdownThreads.remove(key);
				break;
			}
		}
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
	public void sendCountdownMessage(String id, int time) {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(KEY_COUNTDOWN_ID, id);
		values.put(KEY_COUNTDOWN_TIME, String.valueOf(time));
		try {
			sendMessage(MessageCodes.COUNTDOWN,values);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Sending countdown " + id + " with " + time + " seconds");
	}

	private void sendStartSprinklers() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(KEY_STATUS, VALUE_ON);
		try {
			sendMessage(MessageCodes.SET_SPRINKLER,values);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sendSprinklerStatus();
	}

	private void sendStopSprinklers() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(KEY_STATUS, VALUE_OFF);
		try {
			sendMessage(MessageCodes.SET_SPRINKLER,values);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sendSprinklerStatus();
	}
	private void sendSprinklerStatus() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(KEY_STATUS, this.sprinkling ? VALUE_ON : VALUE_OFF);
		try {
			sendMessage(MessageCodes.SPRINKLER_STATUS,values);
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
				name = edu.cmu.a3.common.Util.createRandomId("SprinklerController_",2);
			}
			SprinklerController ws = new SprinklerController(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
