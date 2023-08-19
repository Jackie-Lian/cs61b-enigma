package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.HashMap;
import ucb.util.CommandArgs;
import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Jackie Lian
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */

    private void process() {
        String line;
        Machine m = readConfig();
        while (_input.hasNextLine()) {
            line = _input.nextLine();
            while (line.matches("\\s*") && _input.hasNextLine()) {
                _output.println(); line = _input.nextLine();
            }
            int index = line.indexOf("(");
            String info, cycles;
            if (index < 0) {
                info = line;
                cycles = "";
            } else {
                if (!line.substring(index - 1, index).equals(" ")) {
                    throw error("Setting cannot be next to plugboard");
                }
                info = line.substring(0, index);
                cycles = line.substring(index);
            }
            String[] names, setInfo;
            if (!info.startsWith("*")) {
                throw error("Invalid: Whitespace in the beginning");
            }
            String[] tempStore = info.substring(1).split("\\s+");
            if (tempStore[0].equals("")) {
                setInfo = new String[tempStore.length - 1];
                System.arraycopy(tempStore, 1, setInfo, 0, setInfo.length);
            } else {
                setInfo = tempStore;
            }
            names = new String[m.numRotors()];
            if (setInfo.length < names.length + 1
                    || setInfo.length > names.length + 2) {
                throw error("Wrong number of arguments");
            }
            for (int i = 0; i < names.length; i++) {
                names[i] = setInfo[i];
            }
            String setting, ringSetting;
            if (setInfo.length == names.length + 1) {
                setting = setInfo[setInfo.length - 1];
                ringSetting = "";
            } else {
                setting = setInfo[setInfo.length - 2];
                ringSetting = setInfo[setInfo.length - 1];
            }
            m.insertRotors(names); m.setRotors(setting);
            if (!ringSetting.equals("")) {
                m.setRingSetting(ringSetting);
            }
            Permutation plugboard = new Permutation(cycles, _alphabet);
            m.setPlugboard(plugboard);
            while (_input.hasNextLine() && !_input.hasNext("\\*.*")) {
                String msg = _input.nextLine();
                msg = msg.replaceAll("\\s+", "");
                printMessageLine(m.convert(msg));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int numRotors, numPawls;
            HashMap<String, Rotor> allRotors = new HashMap<>();
            String alphabet = _config.next();
            _alphabet = new Alphabet(alphabet);
            if (_config.hasNextInt()) {
                numRotors = _config.nextInt();
            } else {
                throw error("Invalid configuration file");
            }
            if (_config.hasNextInt()) {
                numPawls = _config.nextInt();
            } else {
                throw error("Invalid configuration file");
            }

            while (_config.hasNext()) {
                Rotor rotor = readRotor();
                if (allRotors.containsKey(rotor.name())) {
                    throw error("Duplicate rotors in configuration");
                } else {
                    allRotors.put(rotor.name(), rotor);
                }
            }
            return new
                    Machine(_alphabet, numRotors, numPawls, allRotors.values());
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Rotor rotor;
            String name = _config.next();
            if (name.contains("(")
                    || name.contains(")")
                    || name.contains("*")) {
                throw error("Invalid rotor name");
            }
            String part = _config.next();
            String type = part.substring(0, 1);
            String notches = part.substring(1);
            String cycles = "";
            while (_config.hasNext("\\(\\S+\\)")) {
                cycles = cycles + _config.next();
            }
            Permutation perm = new Permutation(cycles, _alphabet);
            switch (type) {
            case "R":
                if (notches.length() != 0) {
                    throw error("Reflector does not have notches");
                } else {
                    rotor = new Reflector(name, perm);
                }
                break;
            case "N":
                if (notches.length() != 0) {
                    throw error("Fixed rotor don't have notches.");
                } else {
                    rotor = new FixedRotor(name, perm);
                }
                break;
            case "M":
                if (notches.length() == 0) {
                    throw error("Moving rotor must have one notch.");
                } else {
                    rotor = new MovingRotor(name, perm, notches);
                }
                break;
            default:
                throw error("Invalid rotor type: not R, N, M");
            }
            return rotor;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            if ((i + 1) % 5 == 0) {
                _output.print(" ");
            }
        }
        _output.print("\r\n");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
