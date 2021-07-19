package io.taskmanager.ui.graphical.plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;

public class PluginLoader {
    public Optional<PluginInterface> loadPlugin(File jarFile) {
        PluginInterface plugin = null;
        try {
            URLClassLoader child = new URLClassLoader(
                    new URL[]{jarFile.toURI().toURL()},
                    this.getClass().getClassLoader()
            );
            var object = Class.forName("io.taskmanager.ui.graphical.plugin.Plugin", true, child)
                    .getConstructor()
                    .newInstance();
            plugin = (PluginInterface) object;
        } catch (MalformedURLException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (plugin != null){
            return Optional.of(plugin);
        }
        return Optional.empty();
    }
}
