package edu.cmu.a3.common;

public interface MessageCodes {

	public static final int ARM = 100;
	public static final int INTRUSION_SYSTEM_ALARM = 101;
	public static final int INTRUSION_SENSOR_ALARM = 102;
	
	public static final int FIRE_SYSTEM_ALARM = 201;
	public static final int FIRE_SENSOR_ALARM = 202;
	public static final int COUNTDOWN = 203;
	public static final int COUNTDOWN_RESPONSE = 204;
	public static final int SPRINKLER_STATUS = 205;
	public static final int SET_SPRINKLER = 206;
	
	
	public static final int PING = 301;
	public static final int PONG = 302;
	
}
