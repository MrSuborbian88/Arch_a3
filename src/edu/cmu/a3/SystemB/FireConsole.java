package edu.cmu.a3.SystemB;

import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.AMonitor;
import edu.cmu.a3.common.MessageCodes;

public class FireConsole extends AMonitor {

	public static final String VALUE_TYPE = "FireConsole";

	public static final String VALUE_DESCRIPTION = "FireConsole";

	public FireConsole(String id) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {});
	}
	public FireConsole(String id,String serverIp) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {}, serverIp);
	}

	public static String KEY_ARM = "arm";

	protected static final Integer [] msg_ids = {MessageCodes.FIRE_SYSTEM_ALARM,
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
			}
			break;
		case MessageCodes.SPRINKLER_STATUS:
			if(values.containsKey(SprinklerController.KEY_STATUS)) {
				System.out.println("Sprinklers are currently" + 
						values.get(SprinklerController.KEY_STATUS));
			}

			break;
		default:
			break;
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
				name = edu.cmu.a3.common.Util.createRandomId("DevicesMonitor_",2);
			}
			FireConsole fc = new FireConsole(name);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
