import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        Properties properties = loadProperties();

        Integration integration = new BluetoothIntegration();

        Api api = new Api(integration);

        // Load devices based on property configuration
        Set<AbstractDevice> devices = new TreeSet<>();
        for (String devicePropertyKey : properties.stringPropertyNames()) {
            if (devicePropertyKey.contains(".device")) {
                String deviceClassName = properties.getProperty(devicePropertyKey);
                String deviceName = devicePropertyKey.replace(".device", "");

                try {
                    Class<?> deviceClass = Class.forName(deviceClassName);
                    AbstractDevice device = (AbstractDevice) deviceClass.getDeclaredConstructor().newInstance();
                    device.initialize(properties, deviceName);
                    devices.add(device);
                } catch (ReflectiveOperationException e) {
                    System.err.printf("Error loading device %s: %s%n", deviceName, e.getCause().getMessage());
                }
            }
        }

        // Connect devices
        for (AbstractDevice device : devices) {
            device.connect();
        }

        // Perform operations
        api.speedMotor(50);
        api.paramRegister("custom, notCustom");

        // Disconnect devices
        for (AbstractDevice device : devices) {
            device.disconnect();
        }
    }

    private static Properties loadProperties() {
        try {
            File propertiesFile = new File("config.properties");
            Properties properties = new Properties();
            properties.load(propertiesFile.newInputStream());
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties: " + e.getMessage(), e);
        }
    }
}
