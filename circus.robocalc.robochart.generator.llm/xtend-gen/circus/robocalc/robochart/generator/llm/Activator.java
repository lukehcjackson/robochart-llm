package circus.robocalc.robochart.generator.llm;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

@SuppressWarnings("all")
public class Activator extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "circus.robocalc.robochart.generator.llm";
  
  private static Activator plugin;
  
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    Activator.plugin = this;
  }
  
  public void stop(final BundleContext context) throws Exception {
    Activator.plugin = null;
    super.stop(context);
  }
  
  public static Activator getDefault() {
    return Activator.plugin;
  }
}
