package enigma;

import java.util.HashMap;
import java.util.Map;
import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Jackie Lian
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        if (cycles.isEmpty()) {
            _cycles = new HashMap<>();
        } else if (!cycles.matches("(\\(\\S+\\)\\s*)+")) {
            throw error("Invalid cycles provided");
        } else if (!checkDuplicates(cycles)) {
            throw error("Invalid cycles: characters have duplicates");
        } else {
            _cycles = new HashMap<>();
            cycles = cycles.replace(")", "");
            cycles = cycles.replace(" ", "");
            if (cycles.contains("*")) {
                throw error("* detected in alphabet");
            }
            String[] partitioned = cycles.split("\\(");
            for (int i = 1; i < partitioned.length; i++) {
                this.addCycle(partitioned[i]);
            }
        }
    }

    /** Returns true if there is no duplicate characters in the CYCLE. */
    public boolean checkDuplicates(String cycle) {
        cycle = cycle.replace("(", "");
        cycle = cycle.replace(")", "");
        cycle = cycle.replace(" ", "");
        for (int i = 0; i < cycle.length() - 1; i++) {
            for (int j = i + 1; j < cycle.length(); j++) {
                if (cycle.charAt(i) == cycle.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i++) {
            if (!alphabet().contains(cycle.charAt(i))) {
                throw error("Character not in the alphabet");
            }
        }
        Character first = cycle.charAt(0);
        for (int i = 0; i < cycle.length() - 1; i++) {
            _cycles.put(cycle.charAt(i), cycle.charAt(i + 1));
        }
        _cycles.put(cycle.charAt(cycle.length() - 1), first);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);
        Character before, after;
        int afterIndex;
        before = _alphabet.toChar(p);
        if (_cycles.containsKey(before)) {
            after = _cycles.get(before);
        } else {
            after = before;
        }
        afterIndex = _alphabet.toInt(after);
        return afterIndex;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);
        Character before = _alphabet.toChar(c);
        Character after = null;
        int afterInd;
        for (Map.Entry elem: _cycles.entrySet()) {
            if (elem.getValue() == before) {
                after = (Character) elem.getKey();
            }
        }
        if (after == null) {
            after = before;
        }
        afterInd = _alphabet.toInt(after);
        return afterInd;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int resultInd;
        if (!alphabet().contains(p)) {
            throw error("Character doesn't exist in the alphabet");
        } else {
            resultInd = permute(_alphabet.toInt(p));
        }
        return _alphabet.toChar(resultInd);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int resultInd;
        if (!_alphabet.contains(c)) {
            throw error("Character doesn't exist in the alphabet");
        } else {
            resultInd = invert(_alphabet.toInt(c));
        }
        return _alphabet.toChar(resultInd);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            if (i == permute(i)) {
                return false;
            }
        }
        return true;
    }

    /** Returns the cycles of this permutation. */
    public HashMap<Character, Character> getCycles() {
        return _cycles;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private HashMap<Character, Character> _cycles;
}
