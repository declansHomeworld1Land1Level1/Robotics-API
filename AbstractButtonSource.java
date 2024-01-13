public interface ButtonListener {
    void onButtonPressed(String buttonName);
}

public interface GPIOPinAdapter {
    boolean readPinState(int pinNumber);
    void writePinState(int pinNumber, boolean state);
    String getPinName(int pinNumber);
}

public abstract class AbstractButtonSource {
    private List<ButtonListener> listeners = new ArrayList<>();

    public void addButtonListener(ButtonListener listener) {
        listeners.add(listener);
    }

    public void removeButtonListener(ButtonListener listener) {
        listeners.remove(listener);
    }

    protected void fireButtonPressedEvent(String buttonName) {
        for (ButtonListener listener : listeners) {
            listener.onButtonPressed(buttonName);
        }
    }

    public abstract void initialize(GPIOPinAdapter gpioAdapter);
}

public class ControllerButtonSource extends AbstractButtonSource {
    // Implement methods for reading button states from a game controller
    
    private Map<Integer, Boolean> lastButtonStates = new HashMap<>();

    @Override
    public void initialize(GPIOPinAdapter gpioAdapter) {}

    public boolean isButtonPressed(int buttonId) {
        // Read button state from controller hardware
    }

    public String getButtonName(int buttonId) {
        // Return a human-readable name for the specified button
    }

    @Override
    protected void fireButtonPressedEvent(String buttonName) {
        super.fireButtonPressedEvent(buttonName);
    }
}

public class InputCableButtonSource extends AbstractButtonSource {
    // Implement methods for reading button states from input cables
    
    private Map<Integer, Boolean> lastButtonStates = new HashMap<>();

    @Override
    public void initialize(GPIOPinAdapter gpioAdapter) {}

    public boolean isButtonPressed(int cableNumber, int buttonIndex) {
        // Read button state from input cable hardware
    }

    public String getButtonName(int cableNumber, int buttonIndex) {
        // Return a human-readable name for the specified button on the given input cable
    }

    @Override
    protected void fireButtonPressedEvent(String buttonName) {
        super.fireButtonPressedEvent(buttonName);
    }
}

public class CircuitSwitchButtonSource extends AbstractButtonSource {
    // Implement methods for reading button states from circuit switches
    
    private Map<Integer, Boolean> lastSwitchStates = new HashMap<>();

    @Override
    public void initialize(GPIOPinAdapter gpioAdapter) {
        // Configure GPIO pins to read switch states
    }

    public boolean isSwitchClosed(int switchNumber) {
        return gpioAdapter.readPinState(switchNumber);
    }

    public String getSwitchName(int switchNumber) {
        return gpioAdapter.getPinName(switchNumber);
    }

    @Override
    protected void fireButtonPressedEvent(String buttonName) {
        super.fireButtonPressedEvent(buttonName);
    }
}

public class GPIOButtonSource extends AbstractButtonSource {
    // Implement methods for reading button states from GPIO pins
    
    private Map<Integer, Boolean> lastPinStates = new HashMap<>();

    @Override
    public void initialize(GPIOPinAdapter gpioAdapter) {
        // Configure GPIO pins to read button states
    }

    public boolean isButtonPressed(int pinNumber) {
        return gpioAdapter.readPinState(pinNumber);
    }

    public String getButtonName(int pinNumber) {
        return gpioAdapter.getPinName(pinNumber);
    }

    @Override
    protected void fireButtonPressedEvent(String buttonName) {
        super.fireButtonPressedEvent(buttonName);
    }
}

// Example usage:
public class Main {
    public static void main(String[] args) throws Exception {
        RaspberryPiGPIOAdapter gpioAdapter = new RaspberryPiGPIOAdapter();
        gpioAdapter.initialize();

        ControllerButtonSource controllerButtons = new ControllerButtonSource();
        controllerButtons.addButtonListener(new ButtonListener() {
            @Override
            public void onButtonPressed(String buttonName) {
                System.out.println("Controller button " + buttonName + " was pressed.");
            }
        });
        controllerButtons.initialize(gpioAdapter);

        InputCableButtonSource inputCableButtons = new InputCableButtonSource();
        inputCableButtons.addButtonListener(new ButtonListener() {
            @Override
            public void onButtonPressed(String buttonName) {
                System.out.println("Input cable button " + buttonName + " was pressed.");
            }
        });
        inputCableButtons.initialize(gpioAdapter);

        CircuitSwitchButtonSource circuitSwitches = new CircuitSwitchButtonSource();
        circuitSwitches.addButtonListener(new ButtonListener() {
            @Override
            public void onButtonPressed(String buttonName) {
                System.out.println("Circuit switch " + buttonName + " was toggled.");
            }
        });
        circuitSwitches.initialize(gpioAdapter);

        GPIOButtonSource gpioButtons = new GPIOButtonSource();
        gpioButtons.addButtonListener(new ButtonListener() {
            @Override
            public void onButtonPressed(String buttonName) {
                System.out.println("GPIO button " + buttonName + " was pressed.");
            }
        });
        gpioButtons.initialize(gpioAdapter);
    }
}
