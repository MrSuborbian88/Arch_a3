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
	
	public static void main(String [] args) {try {
		String name = "";
		if(args.length > 0)
			name = args[0];
		if(name.equals("")) {
			name = edu.cmu.a3.common.Util.createRandomId("MotionSensor_",2);
		}
		MotionSensor ds = new MotionSensor(name);

		boolean arm = false;
		System.out.println(ds.getId());
		while(true) {
			if(arm)
				System.out.println("Press Enter to Toggle Sensor Off:");
			else
				System.out.println("Press Enter to Toggle Sensor On:");
			edu.cmu.a3.common.Util.getNextEnter();
			arm = !arm;
			ds.sendAlarmMessage(arm);
			if(arm)
				System.out.println("Sensor is on!");
			else
				System.out.println("Sensor is off!");
		}
	} catch (Exception e) {
		e.printStackTrace();
	}

	}

}
