package circus.robocalc.robotest.rttestergen;

import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext


class Activator extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "circus.robocalc.robotest.rttestergen";
	
	static Activator plugin

    override start(BundleContext context) throws Exception {
    	
        super.start(context); 
        plugin = this;

    }
    
    override stop(BundleContext context) throws Exception {
    	
        plugin = null;
        super.stop(context);
    }
    
    def static Activator getDefault() {
        return plugin;
    }
}