package edu.cmu.a3.SystemA;

public class DoorSensor extends SecuritySensor {

	public static final String VALUE_TYPE = "DoorSensor";

	public DoorSensor(String id) throws Exception {
		super(id,VALUE_TYPE,new Integer [] {});
	}

	public DoorSensor(String id, String type, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,new Integer [] {},serverIp);	}

}
