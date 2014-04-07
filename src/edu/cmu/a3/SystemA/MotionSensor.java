package edu.cmu.a3.SystemA;

public class MotionSensor extends SecuritySensor {

	public static final String VALUE_TYPE = "MotionSensor";

	public static final String VALUE_DESCRIPTION = "Motion Sensor Description";

	public MotionSensor(String id) throws Exception {
		super(id,VALUE_TYPE,VALUE_DESCRIPTION,new Integer [] {});
	}

	public MotionSensor(String id, String type, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION,new Integer [] {},serverIp);	}
	
	public static void main(String [] args) {
		try {
			MotionSensor ms = new MotionSensor("ms1");
			boolean arm = true;
			while(true) {
				ms.sendArmMessage(arm);
//				System.out.println("MessageSent");
				arm = !arm;
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
