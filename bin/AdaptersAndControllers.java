import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.wiringpi.Gpio;
import com.fazecast.jSerialComm.SerialPort;

public class AdaptersAndControllers {

    public static void main(String[] args) {
        initComponents();
        registerListeners();

        // Demonstrate USB Serial Communications
        SerialPort usbPort = SerialPort.getCommPort("COM1");
        usbPort.openPort();
        usbPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);

        // Send data to USB
        usbPort.writeBytes("\nHello USB!\n".getBytes());
        usbPort.closePort();
    }

    private static void initComponents() {
        // Initialize the GPIO library
        GpioFactory.setDefaultProvider(new RaspiGpioProvider());

        // Provision pins
        Pin ledPin = RaspiPin.GPIO_01;
        Pin btnPin = RaspiPin.GPIO_00;

        // Setup the LED pin as OUTPUT
        GpioFactory.getInstance().provisionDigitalOutputPin(ledPin, "LED", PinState.LOW);
    }

    private static void registerListeners() {
        // Register a listener to react to the button pin state change
        GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN).addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                System.out.println("Button state changed to " + event.getState());
                if (event.getState() == PinState.HIGH) {
                    GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_01, "LED").toggle();
                }
            }
        });
    }
}

private static class Init {

  import java.io.IOException;
import java.io.PrintWriter;
import java.io.SerialWriter;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        // Initialise USB Serial Connection
        Serial serial = SerialFactory.createInstance();
        serial.open("/dev/ttyAMA0", 115200);
        PrintWriter writer = new PrintWriter(serial.getOutputStream(), true);

        // Initialise SPI Connection
        SpiDevice spi = SpiFactory.getInstance().createDevice(0, 500000);
        spi.setChipSelectActiveHigh(true);

        // Output Debug Information
        writer.println("=== DEBUG INFO BEGIN ===");
        writer.println("Date: " + new java.util.Date());
        writer.flush();

        // Read SPI Data
        byte[] rx = new byte[1];
        spi.write(new byte[]{0x01}, rx);
        writer.print("SPI Data: ");
        for (byte b : rx) {
            writer.print(Integer.toHexString(b & 0xff));
        }
        writer.println();

        // Cleanup
        writer.println("=== DEBUG INFO END ===");
        serial.close();
        spi.close();
    }
  }
}
