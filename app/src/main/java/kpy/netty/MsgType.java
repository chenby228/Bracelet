package kpy.netty;

/**
 * 消息类型
 * 
 * @author xingchencheng
 * 
 */

public class MsgType {
	public static final short ERROR = 0;
	public static final short APP = 1;
	public static final short PING = 3;
	public static final short LOGIN = 2;
	public static final short REBIND = 9;
	public static final short REBIND2 = 8;
	public static final short ASK_HOW_INIT_MESSAGE = 10;

	public enum Code
	{
		SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
		THURSDAY, FRIDAY, SATURDAY,
		NOVALUE;

		public static Code toCode(String str)
		{
			try {
				return valueOf(str);
			}
			catch (Exception ex) {
				return NOVALUE;
			}
		}
	}
}
