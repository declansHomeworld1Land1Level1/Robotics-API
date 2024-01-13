public abstract class AbstractDevice {
    protected String name;
    protected Api api;

    public AbstractDevice(String name) {
        this.name = name;
    }

    public void initialize(Properties properties, String deviceName) {
        this.name = deviceName;
        // Perform initialization tasks

      Initialize intitialize;
    }

    public abstract void connect();

    public abstract void disconnect();

    public abstract void sendCommand(String command);
}
