package circus.robocalc.robochart.generator.llm;

import org.eclipse.ui.IStartup;

@SuppressWarnings("all")
public class Startup implements IStartup {
  public void earlyStartup() {
    System.out.println("Starting RoboTool RoboChart LLM Generator Plugin...");
  }
}
