package cn.edu.cug.cs.gtl.se.app.cmd;

import org.slf4j.Logger;

public interface AppCommand {
    void run(String [] args);
    default Logger getLogger(){return MainApp.LOGGER;}
}
