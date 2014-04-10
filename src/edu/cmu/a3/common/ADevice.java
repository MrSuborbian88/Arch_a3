package edu.cmu.a3.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.MessagePackage.MessageManagerInterface;
import edu.cmu.a3.MessagePackage.MessageQueue;

public abstract class ADevice {
	public static String KEY_TYPE = "type";
	public static String KEY_ID = "id";
	
	public static String KEY_NAME = "name";
	public static String KEY_DESCRIPTION = "description";
	
	protected String description ;
	
	public static final Integer [] special_ids = {MessageCodes.PING,MessageCodes.PONG};
	
	//Messages with these message ids will be sent to handleMessage()
	protected Integer [] relevant_ids;// = {};
	
	private String id;
	private String type;
	private MessageManagerInterface msgManager;
	private Thread listener;

	private boolean running;

	public ADevice(String id, String type,String description, Integer [] relevant_ids) throws Exception {
		this.msgManager = new MessageManagerInterface();
		setId(id);		
		setType(type);
		setDescription(description);
		startListening();
		this.relevant_ids = relevant_ids;
	}

	public ADevice(String id, String type, String description, Integer [] relevant_ids, String serverIp) throws Exception {
		this.msgManager = new MessageManagerInterface(serverIp);
		setId(id);
		setType(type);
		startListening();
		setDescription(description);
		this.relevant_ids = relevant_ids;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected abstract void handleMessage(Message msg);
	protected abstract void handlePong(Message msg);

	protected void handlePing() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put(KEY_ID, getId());
		values.put(KEY_NAME, getType());
		values.put(KEY_DESCRIPTION, getDescription());
		try {
			sendMessage(MessageCodes.PONG,values);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	protected void sendMessage(int msgId) throws NullPointerException,Exception {
		sendMessage(msgId, new HashMap<String,String>());
	}

	protected void sendMessage(int msgId,Map<String,String> values) throws NullPointerException,Exception {
		if(values == null)
			throw new NullPointerException("Values are null");

		if(!values.containsKey(KEY_TYPE))
			values.put(KEY_TYPE,getType());
		if(!values.containsKey(KEY_ID))
			values.put(KEY_ID,getId());
		
		Message msg = new Message(msgId,getStringFromMap(values));
		
		sendMessage(msg);
	}

	protected void sendMessage(Message msg) throws Exception {
		if(msg == null)
			throw new NullPointerException("Message is null");
		this.msgManager.SendMessage(msg);

	}

	protected void startListening() {
		running = true;
		this.listener = new Thread() {
			@Override
			public void run() {

				while(running) {
					try {
						MessageQueue mq = msgManager.GetMessageQueue();
						
						while(mq.GetSize() > 0) {
							Message msg = mq.GetMessage();
							if(msg.GetMessageId() == MessageCodes.PING)
								handlePing();
							else if(msg.GetMessageId() == MessageCodes.PONG)
								handlePong(msg);
							else{
								if(relevant_ids != null)
								for(int msgid : relevant_ids) {
									if(msgid == msg.GetMessageId())
									{
										handleMessage(msg);
										break;
									}
								}
							}
						}
						Thread.sleep(1000);
					} catch(Exception e) {
						System.err.println(e.getMessage());
					}
				}
			}
		};
		listener.start();
	}
	
	

	protected static  Map<String,String> getMapFromString(String serializedObject) {
		Map<String,String> deserializedObject = new HashMap<String,String>();

		// deserialize the object
		try {
			byte b[] = serializedObject.getBytes(); 
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			deserializedObject = (Map<String,String>) si.readObject();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		return deserializedObject;
	}

	protected static String getStringFromMap(Map<String,String> map ) {
		String serializedObject = "";
		// serialize the object
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(map);
			so.flush();
			serializedObject = bo.toString();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		return serializedObject;
	}
}
