package edu.cmu.a3.SystemA;

public class WindowSensor extends SecuritySensor {

	public static final String VALUE_TYPE = "WindowSensor";
	
	public static final String VALUE_DESCRIPTION = "Window Sensor Description";

	public WindowSensor(String id) throws Exception {
		super(id,VALUE_TYPE,VALUE_DESCRIPTION,new Integer [] {});
	}

	public WindowSensor(String id, String type, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION,new Integer [] {},serverIp);
	}
	
	public static void main(String [] args) {
		try {
			WindowSensor ws = new WindowSensor("1");
			boolean arm = true;
			while(true) {
				ws.sendArmMessage(arm);
//				System.out.println("MessageSent");
				arm = !arm;
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
