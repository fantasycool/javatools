package com.frio;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.BasicPermission;
import java.security.Permission;

/**
 * Created by frio on 17/6/20.
 */
public class CreateFile {

    public void createFile() {
        String path = "/tmp/test.txt";
        SecurityManager security = System.getSecurityManager();
        if(security != null){
            security.checkWrite(path);
        }

    }

    public static void main(String[] args) {
//        new CreateFile().createFile();
        new WatchTV().watchTV();
    }

    public static class TVPermission extends BasicPermission {

        public TVPermission(String name) {
            super(name);
        }

        public TVPermission(String name, String actions) {
            super(name, actions);
        }

        public boolean implies(Permission p) {
            boolean isPermitted = false;
            if (p instanceof TVPermission) {
                isPermitted = p.getName().equals(getName()) && p.getActions().equals(getActions());
            }
            return isPermitted;
        }
    }

    public static class WatchTV {
        public void watchTV() {
            TVPermission tvPermission = new TVPermission("channel-5", "watch");
            AccessController.checkPermission(tvPermission);
        }
    }
}
