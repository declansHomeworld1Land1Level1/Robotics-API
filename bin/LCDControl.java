import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Serial;
import com.pi4j.wiringpi.SoftwareSerial;

public class LCDControl {

    private static Logger logger = Logger.getLogger(LCDControl.class.getName());

    public enum DisplayType {
        UNKNOWN,
        TEXT_DISPLAY,
        GRAPHICAL_DISPLAY
    }

    private static class DisplayDetails {
        String manufacturer;
        String modelName;
        String currentText;
        DisplayType displayType;
        SerialPort serialPort;
        SpiDevice spiDevice;
        SoftwareSerial softwareSerial;
        int usbProductId;
        int usbVendorId;

        DisplayDetails(String manufacturer, String modelName, String currentText, DisplayType displayType,
                SerialPort serialPort, SpiDevice spiDevice, SoftwareSerial softwareSerial, int usbProductId,
                int usbVendorId) {
            this.manufacturer = manufacturer;
            this.modelName = modelName;
            this.currentText = currentText;
            this.displayType = displayType;
            this.serialPort = serialPort;
            this.spiDevice = spiDevice;
            this.softwareSerial = softwareSerial;
            this.usbProductId = usbProductId;
            this.usbVendorId = usbVendorId;
        }
    }

    /**
     * Finds displays on available serial ports, USB devices, and SPI buses.
     */
    public static List<DisplayDetails> findDisplays() {
        List<DisplayDetails> result = new ArrayList<>();

        // Search for serial port displays
        CommPortIdentifier[] ids = CommPortIdentifier.getPortIdentifiers();
        while (ids != null && ids.length > 0) {
            CommPortIdentifier id = ids[0];
            ids = Arrays.copyOfRange(ids, 1, ids.length);

            if (id.getPortType() != CommPortIdentifier.SERIAL_PORT) {
                continue;
            }

            try {
                SerialPort serialPort = (SerialPort) id.open("LCDControl", 5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                String decodedText = URLDecoder.decode(sb.toString(), "ASCII");
                String displayModel = extractPattern(decodedText, "(?<=\\[)(.*?)(?=\\]:)");
                if (displayModel == null) {
                    logger.warning("Unable to parse display model from " + decodedText);
                    continue;
                }

                DisplayDetails display = new DisplayDetails("Unknown Manufacturer", displayModel, decodedText,
                        DisplayType.TEXT_DISPLAY, serialPort, null, null, 0, 0);
                result.add(display);

            } catch (NoSuchPortException ex) {
                logger.warning("No such port exception: " + ex.getMessage());
            } catch (PortInUseException ex) {
                logger.warning("Port already in use: " + ex.getMessage());
            } catch (UnsupportedCommOperationException ex) {
                logger.warning("Unsupported comm operation: " + ex.getMessage());
            } catch (IOException ex) {
                logger.warning("IO error: " + ex.getMessage());
            }
        }

        // Search for USB devices
        Path path = Paths.get("/sys/bus/usb/devices");
        FileUtils.listFilesWithExtension(path, ".udev").forEach(usbFilePath -> {
            try {
                List<String> lines = Files.readAllLines(usbFilePath);
                if (lines.isEmpty()) {
                    return;
                }

                int productId = Integer.parseInt(extractPattern(lines.get(2), "ID_VENDOR_ID=(.*?)"), 16);
                int vendorId = Integer.parseInt(extractPattern(lines.get(3), "ID_MODEL_ID=(.*?)"), 16);

                DisplayDetails display = new DisplayDetails("Unknown Manufacturer", "", "[USB]",
                        DisplayType.UNKNOWN, null, null, null, productId, vendorId);
                result.add(display);

            } catch (IOException ex) {
                logger.warning("IO error: " + ex.getMessage());
            }
        });

        // Search for SPI displays
        List<SpiDevice> spiDevices = SpiDevice.getSpiBuses();
        spiDevices.forEach(spiDevice -> {
            try {
                byte[] buffer = new byte[spiDevice.getMaxTransferSize()];
                spiDevice.transfer(buffer);

                String decodedBuffer = new String(buffer, "ASCII");
                String displayModel = extractPattern(decodedBuffer, "(?<=\\[)(.*?)(?=\\]:)");
                if (displayModel == null) {
                    logger.warning("Unable to parse display model from " + decodedBuffer);
                    return;
                }

                DisplayDetails display = new DisplayDetails("Unknown Manufacturer", displayModel, decodedBuffer,
                        DisplayType.GRAPHICAL_DISPLAY, null, spiDevice, null, 0, 0);
                result.add(display);

            } catch (IOException ex) {
                logger.warning("IO error: " + ex.getMessage());
            }
        });

        return result;
    }

    private static String extractPattern(String subject, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(subject);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static void main(String[] args) {
        List<DisplayDetails> displays = LCDControl.findDisplays();
        System.out.println("Detected displays: ");
        displays.forEach(System.out::println);
    }
}
