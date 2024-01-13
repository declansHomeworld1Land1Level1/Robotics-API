package com.bytetech.robotics.interfaces;

public class Interfaces {

public interface DeviceInterface {
    void connect();
    void disconnect();
    boolean isConnected();
    Object executeCommand(Object command);
}

class VexRobotInterface implements DeviceInterface {
    @Override
    public void connect() {}

    @Override
    public void disconnect() {}

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Object executeCommand(Object command) {
        return null;
    }
}

class ArduinoInterface implements DeviceInterface {
    @Override
    public void connect() {}

    @Override
    public void disconnect() {}

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Object executeCommand(Object command) {
        return null;
    }
}

class LinuxDriverManager {
    private Set<String> loadedDrivers;

    public LinuxDriverManager() {
        loadedDrivers = Collections.synchronizedSet(new HashSet<>());
    }

    public synchronized void loadDriver(String driverName) {
        loadedDrivers.add(driverName);
    }

    public synchronized void unloadDriver(String driverName) {
        loadedDrivers.remove(driverName);
    }

    public boolean isDriverLoaded(String driverName) {
        return loadedDrivers.contains(driverName);
    }

    public Set<String> getLoadedDrivers() {
        return Collections.unmodifiableSet(loadedDrivers);
    }
}

class MindstormsInterface implements DeviceInterface {
    @Override
    public void connect() {}

    @Override
    public void disconnect() {}

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Object executeCommand(Object command) {
        return null;
    }
}

class ScratchVMRunner {
    public void startVM() {}

    public void stopVM() {}

    public boolean isRunning() {
        return false;
    }
}

class RobotWrapper {
    private DeviceInterface deviceInterface;

    public RobotWrapper(DeviceInterface deviceInterface) {
        this.deviceInterface = deviceInterface;
    }

    public void connect() {
        deviceInterface.connect();
    }

    public void disconnect() {
        deviceInterface.disconnect();
    }

    public boolean isConnected() {
        return deviceInterface.isConnected();
    }

    public Object executeCommand(Object command) {
        return deviceInterface.executeCommand(command);
    }
}

class DeviceInterfaceTest {
    private DeviceInterface deviceInterface;

    @Before
    public void setup() {
        deviceInterface = new DummyDeviceInterface();
    }

    @Test
    public void testConnectDisconnect() {
        assertFalse(deviceInterface.isConnected());
        deviceInterface.connect();
        assertTrue(deviceInterface.isConnected());
        deviceInterface.disconnect();
        assertFalse(deviceInterface.isConnected());
    }

    private static class DummyDeviceInterface implements DeviceInterface {
        private boolean connected;

        @Override
        public void connect() {
            connected = true;
        }

        @Override
        public void disconnect() {
            connected = false;
        }

        @Override
        public boolean isConnected() {
            return connected;
        }

        @Override
        public Object executeCommand(Object command) {
            throw new UnsupportedOperationException("Execute command not implemented");
          }
      }
   }
}
