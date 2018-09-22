package malte0811.weblogger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

public class WebLoggerHooks
{
	private static final Logger logger = LogManager.getLogger("weblogger");
	public static void log(URL url) {
		logger.info("Web access to "+url);
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		//Index 0: getStackTrace, Index 1: logAndReturn
		for (int i = 2;i<stack.length;i++) {
			logger.info("at "+stack[i]);
		}
	}
}
