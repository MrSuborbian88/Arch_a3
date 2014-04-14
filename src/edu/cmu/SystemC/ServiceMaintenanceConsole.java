package SystemC;

import edu.cmu.a3.MessagePackage.Message;
import edu.cmu.a3.common.AMonitor;

public class ServiceMaintenanceConsole extends AMonitor {
	

	public static final String VALUE_TYPE = "ServiceMaintenanceConsole";

	public static final String VALUE_DESCRIPTION = "Service Maintenance Console";
	
	public ServiceMaintenanceConsole(String id) throws Exception {
		super(id, VALUE_TYPE, VALUE_DESCRIPTION, new Integer[] {});
	}
	public ServiceMaintenanceConsole(String id,String serverIp) throws Exception {
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
				name = edu.cmu.a3.common.Util.createRandomId("ServiceMaintenanceConsole_",2);
			}
			ServiceMaintenanceConsole smc = new ServiceMaintenanceConsole(name);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}


