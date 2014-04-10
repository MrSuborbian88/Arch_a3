package SystemC;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.AMonitor;

public class DevicesMonitor extends AMonitor {
	

	public static final String VALUE_TYPE = "DevicesMonitor";

	public static final String VALUE_DESCRIPTION = "Device Monitor Description";
	
	public DevicesMonitor(String id) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {});
	}
	public DevicesMonitor(String id,String serverIp) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {}, serverIp);
	}

	public static String KEY_ARM = "arm";
	
	@Override
	protected void handleMessage(Message msg) {
		//No action needed
	}
	
	@Override
	protected void deviceChange() {
		System.out.println("================================================");
		System.out.println("Name\t\t\tType\t\t\tDescription");
		for(Device d : this.connectedDevices.values()) {
			System.out.println(
					d.name + "\t\t" +
					d.type + "\t\t" +
					d.description + "\t\t"
		);
		}
		
	}

	public static void main(String[] args) {
		try {
			String name = "";
			if(args.length > 0)
				name = args[0];
			if(name.equals("")) {
				name = edu.cmu.a3.common.Util.createRandomId("DoorSensor_",2);
			}
			DevicesMonitor ds = new DevicesMonitor(name);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}


