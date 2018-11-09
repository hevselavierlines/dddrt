package com.baselet.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.baselet.control.Main;
import com.baselet.control.config.handler.ConfigHandler;
import com.baselet.control.constants.Constants;
import com.baselet.control.enums.Program;
import com.baselet.control.enums.RuntimeType;
import com.baselet.control.util.Path;
import com.baselet.gui.CurrentGui;
import com.baselet.plugin.gui.EclipseGUI;

/**
 * The activator class controls the plug-in life cycle
 */
public class MainPlugin extends AbstractUIPlugin {

	// The plug-in ID
	private static String pluginId;

	public static String getPluginId() {
		return pluginId;
	}

	// The shared instance
	private static MainPlugin plugin;

	/**
	 * The constructor
	 */
	public MainPlugin() {
		Program.getInstance().setRuntimeType(RuntimeType.ECLIPSE_PLUGIN);
		plugin = this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext) */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		initHomeProgramPath();
		Main.getInstance().initLogger();
		readBundleManifestInfo();
		ConfigHandler.loadConfig();
		Main.getInstance().init(new EclipseGUI(Main.getInstance()));
	}

	private void initHomeProgramPath() throws IOException {
		String path = null;
		URL homeURL = MainPlugin.getURL();
		path = FileLocator.toFileURL(homeURL).toString().substring("file:/".length());
		if (File.separator.equals("/")) {
			path = "/" + path;
		}
		Path.setHomeProgram(path);
	}

	// Issue 83: Use OSGI Bundle to read Manifest information
	private void readBundleManifestInfo() {
		Dictionary<String, String> headers = MainPlugin.getDefault().getBundle().getHeaders();
		pluginId = MainPlugin.getDefault().getBundle().getSymbolicName();
		Program.getInstance().init(headers.get(Constants.MANIFEST_BUNDLE_VERSION));

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext) */
	@Override
	public void stop(BundleContext context) throws Exception {
		CurrentGui.getInstance().getGui().closeWindow();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static MainPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(pluginId, path);
	}

	public static URL getURL() {
		return FileLocator.find(MainPlugin.getDefault().getBundle(), new org.eclipse.core.runtime.Path("/"), null);
	}
}
