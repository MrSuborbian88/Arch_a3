package edu.cmu.a3.SystemA;

public class DoorSensor extends SecuritySensor {

	public static final String VALUE_TYPE = "DoorSensor";

	public static final String VALUE_DESCRIPTION = "Door Sensor Description";

	public DoorSensor(String id) throws Exception {
		super(id,VALUE_TYPE,VALUE_DESCRIPTION,new Integer [] {});
	}

	public DoorSensor(String id, String type, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,VALUE_DESCRIPTION,new Integer [] {},serverIp);	}

	public static void main(String [] args) {
		try {
			DoorSensor ds1 = new DoorSensor("ds1");
			DoorSensor ds2 = new DoorSensor("ds2");
			DoorSensor ds3 = new DoorSensor("ds3");

			boolean arm = true;
			while(true) {
				ds1.sendArmMessage(arm);
				ds2.sendArmMessage(arm);
				ds3.sendArmMessage(!arm);
//				System.out.println("MessageSent");
				arm = !arm;
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
