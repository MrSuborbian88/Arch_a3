package edu.cmu.a3.SystemA;

public class MotionSensor extends SecuritySensor {

	public static final String VALUE_TYPE = "MotionSensor";

	public MotionSensor(String id) throws Exception {
		super(id,VALUE_TYPE);
	}

	public MotionSensor(String id, String type, String serverIp)
			throws Exception {
		super(id, VALUE_TYPE,serverIp);
	}

}
