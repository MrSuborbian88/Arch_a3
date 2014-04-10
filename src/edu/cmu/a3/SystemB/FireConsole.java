package edu.cmu.a3.SystemB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.AMonitor;
import edu.cmu.a3.common.MessageCodes;

public class FireConsole extends AMonitor {

	public static final String VALUE_TYPE = "FireConsole";

	public static final String VALUE_DESCRIPTION = "FireConsole";

	public FireConsole(String id) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, msg_ids);
	}
	public FireConsole(String id,String serverIp) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, msg_ids, serverIp);
	}

	private RespondThread respondThread;

	protected static final Integer [] msg_ids = {
		MessageCodes.FIRE_SYSTEM_ALARM,
		MessageCodes.COUNTDOWN,
		MessageCodes.SPRINKLER_STATUS};


	@Override
	protected void handleMessage(Message msg) {
		Map<String,String> values = getMapFromString(msg.GetMessage());
		switch(msg.GetMessageId()) {
		case MessageCodes.FIRE_SYSTEM_ALARM:
			if(values.containsKey(FireAlarmController.KEY_STATUS)) {
				if(values.get(FireAlarmController.KEY_STATUS).equals(FireAlarmController.VALUE_ARMED)) {
					System.out.println("Fire detected by " + values.get(KEY_ID));
				}
				else {
					System.out.println("Fire cleared by " + values.get(KEY_ID));
				}
			}
			System.out.println("Fire System Alarm");
			break;
		case MessageCodes.COUNTDOWN:
			if(values.containsKey(SprinklerController.KEY_COUNTDOWN_TIME)) {
				System.out.println("Sprinklers will go off in " + 
						values.get(SprinklerController.KEY_COUNTDOWN_TIME) + " seconds.");
				respondThread = new RespondThread();
				respondThread.start();
			}
			break;
		case MessageCodes.SPRINKLER_STATUS:
			if(values.containsKey(SprinklerController.KEY_STATUS)) {
				System.out.println("Sprinklers are currently " + 
						values.get(SprinklerController.KEY_STATUS));
			}

			break;
		default:
			break;
		}

	}
	private void sendStopCountdown() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(SprinklerController.KEY_COUNTDOWN_RESPONSE,SprinklerController.VALUE_OFF);
		try {
			sendMessage(MessageCodes.COUNTDOWN_RESPONSE,values);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public class RespondThread extends Thread {
		public void run() {
			System.out.println("Press enter to stop the sprinklers:");
			try {
				//This doesn't work yet (won't grab user input)
				int x = 10; // wait 2 seconds at most
				
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				long startTime = System.currentTimeMillis();
				while ((System.currentTimeMillis() - startTime) < x * 1000
						&& !in.ready()) {
				}
				if (in.ready()) {
					System.out.println("Stopping countdown");
					sendStopCountdown();

				} else {

				}
				edu.cmu.a3.common.Util.getNextEnter();
			} catch (IOException e) {
			}

		}
	}
	public void setSprinklers(boolean on) {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(SprinklerController.KEY_STATUS, on ? SprinklerController.VALUE_ON : SprinklerController.VALUE_OFF);

		try {
			sendMessage(MessageCodes.SET_SPRINKLER,values);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	protected void deviceChange() {
		//No action needed

	}

	public static void main(String[] args) {
		try {
			String name = "";
			if(args.length > 0)
				name = args[0];
			if(name.equals("")) {
				name = edu.cmu.a3.common.Util.createRandomId("FireConsole_",2);
			}

			try {
				FireConsole fc = new FireConsole(name);

				boolean on = false;
				System.out.println(fc.getId());
				while(true) {
					if(on)
						System.out.println("Press Enter to Toggle Sprinklers Off:");
					else
						System.out.println("Press Enter to Toggle Sprinkler On:");
					edu.cmu.a3.common.Util.getNextEnter();
					on = !on;
					fc.setSprinklers(on);
					if(on)
						System.out.println("Sprinkler is on!");
					else
						System.out.println("Sprinkler is off!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
