package enigma;
import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Jackie Lian
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        if (chars.isEmpty()) {
            throw error("Alphabet cannot be empty");
        } else if (chars.contains("*")
                || chars.contains("(")
                || chars.contains(")")) {
            throw error("Invalid Alphabet: special characters detected");
        }
        String space = " ";
        for (int i = 0; i < chars.length(); i++) {
            for (int j = i + 1; j < chars.length(); j++) {
                if (chars.charAt(i) == space.charAt(0)) {
                    throw error("Whitespace in the alphabet provided");
                }
                if (chars.charAt(i) == chars.charAt(j)) {
                    throw error("Duplicates characters in the alphabet");
                }
            }
        }
        _chars = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < _chars.length(); i++) {
            if (_chars.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index >= _chars.length()) {
            throw error("Character out of bounds");
        }
        return _chars.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!contains(ch)) {
            throw error("Character provided is not in the alphabet");
        }
        for (int i = 0; i < _chars.length(); i++) {
            if (_chars.charAt(i) == ch) {
                return i;
            }
        }
        throw error("Character provided is not in the alphabet");
    }

    /** The characters stored in this alphabet. */
    private String _chars;
}
