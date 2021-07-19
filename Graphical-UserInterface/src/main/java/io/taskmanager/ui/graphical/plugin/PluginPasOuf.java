package io.taskmanager.ui.graphical.plugin;

import io.taskmanager.ui.graphical.App;

public class PluginPasOuf implements PluginInterface{
    @Override
    public void startPlugin(App nadal) {
        System.out.print("hey je suis un plugin");
    }
}
