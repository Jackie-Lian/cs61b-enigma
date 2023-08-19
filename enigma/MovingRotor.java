package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Jackie Lian
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        if (checkValidNotches(notches)) {
            _notches = notches;
            _position = 0;
        } else {
            throw error("Invalid notches: character not in the alphbet");
        }
    }

    /** Returns true if NOTCHES are valid. */
    boolean checkValidNotches(String notches) {
        for (int i = 0; i < notches.length(); i++) {
            if (!alphabet().contains(notches.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        _position = _position + 1;
    }

    @Override
    String notches() {
        return _notches;
    }

    @Override
    public String toString() {
        return "MovingRotor " + name();
    }

    /** Notches for moving rotor. */
    private String _notches;

}
