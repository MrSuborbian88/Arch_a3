package edu.cmu.a3.SystemA;

public class WindowSensor extends SecuritySensor {

	public static final String VALUE_TYPE = "WindowSensor";

	public WindowSensor(String id) throws Exception {
		super(id,VALUE_TYPE);
	}

	public WindowSensor(String id, String type, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,serverIp);
	}

}
