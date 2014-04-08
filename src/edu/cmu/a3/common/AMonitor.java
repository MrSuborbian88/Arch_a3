package edu.cmu.a3.common;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;

public abstract class AMonitor extends ADevice {

	public static final Integer [] special_ids = {MessageCodes.PING,MessageCodes.PONG};

	private Thread listener;
	private Thread sender;

	private boolean running;

	public static final Long TIMEOUT = 60000L; //in ms


	private Map<String,Device> connectedDevices;

	private class Device {
		public String name;
		public String type;
		public String description;
		public Long timestamp;

		public Device(String name,String type, String description, Long timestamp)
		{
			this.name=name;
			this.type=type;
			this.description=description;
			this.timestamp = timestamp;
		}
	}

	public AMonitor(String id, String type, String description, Integer [] relevant_ids) throws Exception {
		super(id,type,description,relevant_ids);
		connectedDevices = new HashMap<String,Device>();
		startSending();
	}

	public AMonitor(String id, String type, String description, Integer [] relevant_ids, String serverIp) throws Exception {
		super(id,type,description,relevant_ids,serverIp);
		connectedDevices = new HashMap<String,Device>();

		startSending();
	}

	protected void startSending() {
		running = true;
		this.sender = new Thread() {
			@Override
			public void run() {
				while(running) {
					try {
						sendMessage(MessageCodes.PING);
						Thread.sleep(5000);
						checkDevices();
					} catch(Exception e) {
						System.err.println(e.getMessage());
					}
				}
			}
		};
		sender.start();
	}

	protected void checkDevices() {
		String [] keyset = connectedDevices.keySet().toArray(new String[connectedDevices.size()]);
		for( String key : keyset) {
			if(connectedDevices.containsKey(key))
			{
				Device d = connectedDevices.get(key);
				if(Math.abs(d.timestamp - System.currentTimeMillis()) > TIMEOUT ) {
					connectedDevices.remove(key);
				}
			}
		}
	}

	@Override
	protected void handlePong(Message msg) {
		if(msg == null)
			return;
		Map<String,String> values = getMapFromString(msg.GetMessage());

		if(values != null && values.containsKey(KEY_ID) &&
				values.containsKey(KEY_NAME) &&
				values.containsKey(KEY_DESCRIPTION)
				) {
			Device d = new Device(values.get(KEY_ID),
					values.get(KEY_NAME),
					values.get(KEY_DESCRIPTION),
					System.currentTimeMillis());

			connectedDevices.put(values.get(KEY_ID), d);
		}
//		System.out.println("Connected Devices " + connectedDevices.size());

	}

}