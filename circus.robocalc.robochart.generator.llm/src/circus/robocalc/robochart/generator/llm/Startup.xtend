package circus.robocalc.robotest.rttestergen;

import org.eclipse.ui.IStartup

class Startup implements IStartup {
	
	override earlyStartup() {
		System.out.println("Starting RoboTool RT-Tester Test Generator Plugin...");
	}
	
}
