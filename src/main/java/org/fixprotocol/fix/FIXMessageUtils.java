package org.fixprotocol.fix;

import org.fixprotocol.ASCII;

import quickfix.Message;

public class FIXMessageUtils {

	public static FIXMessage convert(Message message) {
		return null;
	}

	public static Message convert(FIXMessage fixmessage) {
		return null;
	}

	public static String normalize(String fixMsg) {
		return fixMsg.trim().replaceAll("\r|\n", ASCII.PIPE)
				.replaceAll(ASCII.PIPE, ASCII.SOH);
	}
	
}
