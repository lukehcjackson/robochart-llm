package circus.robocalc.robochart.generator.llm;

import org.eclipse.ui.IStartup

class Startup implements IStartup {
	
	override earlyStartup() {
		System.out.println("Starting RoboTool RoboChart LLM Generator Plugin...");
	}
	
}
