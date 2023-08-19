package enigma;
import static enigma.EnigmaException.*;

import java.util.Collection;
import java.util.Iterator;

/** Class that represents a complete enigma machine.
 *  @author Jackie Lian
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        if (numRotors < 1) {
            throw error("Invalid number of rotor slots");
        } else if (pawls >= numRotors || pawls < 1) {
            throw error("Invalid number of pawls");
        }
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[numRotors];
        _plugboard = new Permutation("", _alphabet);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        if (k >= _numRotors || k < 0) {
            throw error("Invalid rotor index");
        } else {
            return _rotors[k];
        }
    }

    /** Returns the current alphabet used by this machine. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length != _numRotors) {
            throw error("Number of rotors do not match");
        } else if (!checkExists(rotors)) {
            throw error("Rotors not found in available rotors");
        } else {
            for (int i = 0; i < _numRotors; i++) {
                _rotors[i] = findRotor(rotors[i]);
            }
            if (!checkOrder(_rotors)) {
                throw error("Order of rotors is invalid");
            } else if (!checkPawlMatch()) {
                throw error("Number of pawls don't "
                        + "match number of moving rotors");
            }
        }
    }

    /** Returns true if the number of pawls matches the number of
     * moving rotors in the machine. */
    boolean checkPawlMatch() {
        int moving = 0;
        for (Rotor rotor: _rotors) {
            if (rotor instanceof MovingRotor) {
                moving = moving + 1;
            }
        }
        return moving == _numPawls;
    }

    /** Returns true if the rotors in ROTORS are arranged in the correct
     * order. */
    boolean checkOrder(Rotor[] rotors) {
        if (!(rotors[0] instanceof Reflector)) {
            return false;
        } else if (!(rotors[rotors.length - 1] instanceof MovingRotor)) {
            return false;
        } else {
            boolean movingPresent = false;
            boolean hasReflector = false;
            for (Rotor rotor: rotors) {
                if ((rotor instanceof Reflector) && hasReflector) {
                    return false;
                } else {
                    hasReflector = true;
                }
                if ((rotor instanceof FixedRotor) && movingPresent) {
                    return false;
                } else if (rotor instanceof MovingRotor) {
                    movingPresent = true;
                }
            }
            return true;
        }
    }

    /** Returns the rotor with name NAME if it's present
     * among the available rotors. */
    Rotor findRotor(String name) {
        Iterator<Rotor> curr = _allRotors.iterator();
        while (curr.hasNext()) {
            Rotor current = curr.next();
            if (current.name().equals(name)) {
                return current;
            }
        }
        throw error("Rotor's name not found in available rotors");
    }

    /** Helper function that checks the names ROTORS of the rotors
     * exists in available rotors. Returns false if any of the
     * rotors do not exist in _allRotors. Returns true if all
     * rotors can be found in _allRotors.
     */
    boolean checkExists(String[] rotors) {
        int count = 0;
        for (String rotorName: rotors) {
            Iterator<Rotor> curr = _allRotors.iterator();
            boolean found = false;
            while (curr.hasNext() && !found) {
                Rotor current = curr.next();
                if (current.name().equals(rotorName)) {
                    count = count + 1;
                    found = true;
                }
            }
        }
        return count == rotors.length;
    }


    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw error("Invalid setting");
        }
        for (int i = 1; i < _numRotors; i++) {
            _rotors[i].set(_alphabet.toInt(setting.charAt(i - 1)));
        }
    }

    /** Set my rotors according to RINGSETTING, which must be a string
     * of numRotors() - 1 characters in my alphabet. */
    void setRingSetting(String ringSetting) {
        if (ringSetting.length() != _numRotors - 1) {
            throw error("Invalid ringSetting");
        }
        for (int i = 1; i < _numRotors; i++) {
            _rotors[i].setRingSetting(ringSetting.charAt(i - 1));
        }
    }



    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean[] canAdv = new boolean[_numRotors];
        canAdv[canAdv.length - 1] = true;
        for (int i = 1; i < numRotors() - 1; i++) {
            if (_rotors[i].rotates() && _rotors[i + 1].atNotch()) {
                canAdv[i] = true;
            } else if (_rotors[i - 1].rotates() && _rotors[i].atNotch()) {
                canAdv[i] = true;
            }
        }
        for (int i = 0; i < _numRotors; i++) {
            if (canAdv[i]) {
                _rotors[i].advance();
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        for (int i = _rotors.length - 1; i >= 0; i--) {
            c = _rotors[i].convertForward(c);
        }
        for (int i = 1; i < _rotors.length; i++) {
            c = _rotors[i].convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            int p = alphabet().toInt(msg.charAt(i));
            p = convert(p);
            result = result + (alphabet().toChar(p));
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors in this machine. */
    private int _numRotors;

    /** Number of pawls in this machine. */
    private int _numPawls;

    /** All of the rotors available to this machine. */
    private Collection<Rotor> _allRotors;

    /** The rotors used in the machine. */
    private Rotor[] _rotors;

    /** The plugboard used in the machine. */
    private Permutation _plugboard;
}
