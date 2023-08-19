package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Jackie Lian
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _position = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _position;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        posn = posn % alphabet().size();
        _position = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _position = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int intoP = _permutation.wrap(p + _position - _ringSetting);
        int outP = _permutation.permute(intoP);
        int result = _permutation.wrap(outP - _position + _ringSetting);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int intoP = _permutation.wrap(e + _position - _ringSetting);
        int outP = _permutation.invert(intoP);
        int result = _permutation.wrap(outP - _position + _ringSetting);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        for (int i = 0; i < notches().length(); i++) {
            if (_permutation.wrap(_position)
                    == alphabet().toInt(notches().charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** Set the ringSetting for this rotor to P. */
    void setRingSetting(int p) {
        _ringSetting = _permutation.wrap(p);
    }

    /** Set the ringSetting for this rotor to C. */
    void setRingSetting(char c) {
        _ringSetting = _permutation.alphabet().toInt(c);
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** The position of the rotor. */
    protected int _position;

    /** The ringSetting of the rotor. */
    protected int _ringSetting;

}
