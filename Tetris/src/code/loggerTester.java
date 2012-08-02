package code;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class loggerTester {

	
	
	// Set up Logger
	private static final Logger log = Logger.getLogger(loggerTester.class);
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DOMConfigurator.configure("log4j.xml");	
		
		log.info("Test");
		
		
	}

}
