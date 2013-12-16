/**
 *  Custom Directed Acyclic Word Graph for use as the data structure to hold our dictionary
 *  
**/

package dictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Arrays;


/**
 *
 * @author Sean Frazier
 * @author Amandeep Gill
 *
**/

public class Dawg {

    static boolean debug = false;
    // static boolean debug = true;
    private final class State{
        // The children, or transitions, from this state to others
        // stored as a hashmap because the dawg is deterministic
        private HashMap <Character, State> children;
        // The state's character value, basically, what input will send you to this state
        private Character charVal;
        // bool to determine if the state is accepting or not
        private boolean wIsAccepting;
        // the ID of the State, used to determine equivalance
        private int id;
        // whether the state has been printed already
        public boolean printed = false;
        // default constructor for the state
        public State(char value, int id){
            this.charVal = value;
            children = new HashMap<>();
            wIsAccepting = false;
            this.id = id;
        }
        // Store children in a hashmap (since the dawg is deterministic)
        public HashMap<Character, State> getChildren() { return children; }
        // returns the character value of the state
        public char getCharVal() { return charVal; }
        // sets the state to an accepting state
        public void setAccepting() { wIsAccepting = true; }
        // returns true if the state is accepting, false otherwise
        public boolean isAccepting() { return wIsAccepting; }
        // returns whether the two states are equal
        @Override
        public boolean equals(Object s) { return this.toString() == s.toString(); }
        // return the state's ID
        public int getID() { return id; }
        // set the state's ID
        public void setID(int id) { this.id = id; }
        // return the string representation of the state
        public String toString() { return ((Integer)id).toString(); }
    };

    // check that a state is accepting or not
    private boolean isAccepting(State s) {
        return s == null ? false : s.isAccepting();
    }

    private int currID = 0;
    private State rootState;
    private int states;
    private ArrayList<Character> alphabet;

    // Init an empty Dawg
    public Dawg(){
        alphabet = new ArrayList<>();
        for (int i = 0; i < 26; i++) 
            alphabet.add((char)((int)'a' + i));
        rootState = new State((char)0, currID);
        currID++;
        states = 2;
    }

    // print the contents of the sets
    private void printSet(Set<State> S) {
        for (State s : S) {
            System.out.println(s.toString());
        }
    }

    // Minimize the Trie into a directed graph (DFA) using the Hopcroft algorithm
    private void minimize() {
        List<Set<State>> P = new ArrayList<>();
        Queue<Set<State>> W = new LinkedList<>();
        Set<State> Q = new LinkedHashSet<>();

        if (debug) System.out.println("Setting initial partitions");
        // initialize P, W, Q where:
        //  Q -> the set of all states
        //  P -> the partition of Q where each partition represents a minimized state
        //       initialized as the partition of Q into accepting and non-accepting states
        //  W -> the set of working partitions
        //       initialized with only the set of accepting states
        Set<State> F = new LinkedHashSet<>();
        setPartitions(Q, F, rootState);
        Set<State> QlF = complement(Q, F);

        P.add(F);
        P.add(QlF);
        W.add(F);

        System.out.println("|Q|   = " + Q.size());
        System.out.println("|F|   = " + F.size());
        System.out.println("|Q\\F| = " + QlF.size());
        // when W is empty, the partitions P represent the final minimized state
        SKIPONW:
        while (!W.isEmpty()) {
            if (debug) System.out.println("Looping through working set");
            if (debug) System.out.println("|W|   = " + W.size());
            if (debug) System.out.println("|P|   = " + P.size());
            // select a set A from W
            Set<State> A = W.remove();
            for (Character c : alphabet) {
                if (debug) System.out.println("Setting X");
                // choose all states x where x in Q and d(x, c) -> some a in A
                Set<State> X = new LinkedHashSet<>();
                for (State s : Q) {
                    State n = null;
                    if (s != null) 
                        n = s.getChildren().get(c);
                    if (A.contains(n)) {
                        X.add(s);
                    }
                }
                // skip to next character if the set X is empty
                if (X.isEmpty()) {
                    if (debug) System.out.println("X is empty, moving on to next char");
                    continue;
                }
                // choose a set Y from P
                for (ListIterator<Set<State>> Yi = P.listIterator(); Yi.hasNext(); ) {
                    if (debug) System.out.println("Inside partitions loop");
                    Set<State> Y = Yi.next();
                    // find the intersection of X and Y
                    if (debug) System.out.println("Intersecting X and Y");
                    Set<State> XnY = intersect(X, Y);
                    if (debug) {
                        if (XnY.size() == 1)
                            System.out.println(XnY);
                    }
                    // find the complement of X in Y
                    if (debug) System.out.println("Complementing X in Y");
                    Set<State> YlX = complement(Y, X);
                    if (debug) {
                        System.out.println("|X|   = " + X.size());
                        System.out.println("|Y|   = " + Y.size());
                        System.out.println("|XnY| = " + XnY.size());
                        System.out.println("|Y\\X| = " + YlX.size());
                    }
                    // move to the next set Y if either partition is empty
                    if (!XnY.isEmpty() && !YlX.isEmpty()) {
                        // replace Y with the partitions of Y
                        Yi.remove();
                        Yi.add(XnY);
                        Yi.add(YlX);
                        // if Y is in W, replace Y with its partitions
                        if (W.remove(Y)) {
                            if (debug) System.out.println("Adding XnY to W");
                            W.add(XnY);
                            if (debug) System.out.println("Adding Y\\X to W");
                            W.add(YlX);
                        }
                        // if the intersection is not larger than the complement,
                        // add the intersection partition to the working set W
                        else if (XnY.size() <= YlX.size()) {
                            if (debug) System.out.println("Adding intersection to W");
                            W.add(XnY);
                        }
                        // else, add the complement partition to the working set W
                        else {
                            if (debug) System.out.println("Adding complement to W");
                            W.add(YlX);
                        }
                    }
                    if (debug) {
                        System.out.println("|W|   = " + W.size());
                        System.out.println("|P|   = " + P.size());
                    }
                }
            }
        }

        /*
        for (ListIterator<Set<State>> Pi = P.listIterator(); Pi.hasNext(); ) {
            Set<State> S = Pi.next();
            for (Iterator<State> Si = S.iterator(); Si.hasNext(); ) {
                State s = Si.next();
                if (s == null)
                    Si.remove();
            }

            if (S.size() == 0)
                Pi.remove();
        }
        */
        System.out.println("|W|   = " + W.size());
        System.out.println("|P|   = " + P.size());
        states = P.size();
        // use the partition of Q to create a new DFA graph
        State newStart = new State((char)0, 0);
        State[] newQ = new State[P.size()];
        recreateStates(newStart, P, newQ);
        rootState = newStart;
    }

    // find the intersection of S1 and S2 and return it
    private Set<State> intersect(Set<State> S1, Set<State> S2) {
        Set<State> I = new LinkedHashSet<>(S1);
        I.retainAll(S2);
        return I;
    }

    // find the complement of S2 in S1
    //  ie: find all elements in S1 that are not also in S2
    private Set<State> complement(Set<State> S1, Set<State> S2) {
        Set<State> C = new LinkedHashSet<>(S1);
        C.removeAll(S2);
        return C;
    }

    // set the initial partitions for the hopcroft algorithm
    private void setPartitions(Set<State> Q, Set<State> F, State current) { 
        Q.add(current);
        if (current != null) {
            if (isAccepting(current)) 
                F.add(current);
            for (Character c : alphabet) {
                setPartitions(Q, F, current.getChildren().get(c));
            }
        }
    }

    // create the new DFA graph from the minimized partition of Q
    private void recreateStates(State start, List<Set<State>> P, State[] Q) {
        for (int i = 0; i < Q.length; i++) {
            if (Q[i] == null) {
                if (P.get(i).contains(rootState)) 
                    Q[i] = start;
                else
                    Q[i] = new State((char)i, i+1);
            }
            else if (P.get(i).contains(rootState))
                start = Q[i];
            State s = P.get(i).iterator().next();
            if (s == null) 
                continue;
            if (s.isAccepting())
                Q[i].setAccepting();
            for (Character c : alphabet) {
                State n = s.getChildren().get(c);
                if (n == null)
                    Q[i].getChildren().put(c, null);
                else {
                    for (int j = 0; j < P.size(); j++) {
                        if (P.get(j).contains(n)) {
                            if (Q[j] == null) {
                                Q[j] = new State((char)j, j+1);
                                if (n.isAccepting())
                                    Q[j].setAccepting();
                            }
                            Q[i].getChildren().put(c, Q[j]);
                            break;
                        }
                    }
                }
            }
        }
    }

    // recursively add the word to the graph
    private void addWord(String word, int index, State current) {
        // if (debug) System.out.println(current.getID());
        // return if we have used the whole string
        if (word.length() == index) {
            current.setAccepting();
            return;
        }
        // check if the transition on c exists
        char c = word.charAt(index);
        if (current.getChildren().get(c) == null) {
            current.getChildren().put(c, new State(c, currID));
            currID++;
        }
        // follow the transition to the next state
        addWord(word, index+1, current.getChildren().get(c));
    }

    // Traverse the dawg by using the string 'word' as input does not garentee that the word
    // is accepted, see "isAccepted(String word)"
    private State getState(String word){
        State currentState = rootState;
        // walk through all child states until we find the transition to the state we're looking for.
        // then increment i and look for the next transition
        for (Character c : word.toCharArray()) {
            State next = currentState.getChildren().get(c);
            if (next != null)
                currentState = next;
            else
                break;
        }
        // 
        return currentState;
    }

    // Return true if the word is accepted by the dawg, false otherwise
    public boolean isAccepted (String word){
        State currentState = getState(word);
        if (isAccepting(currentState)){
            // if (debug) System.out.println(word + " accepted!");
            return true; 
        }
        // if (debug) System.out.println(word + " not accepted!");
        return false;
    }

    public void printDawg(String output) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(output));
        out.write((new Integer(states-1)).toString());
        out.newLine();
        recursivePrintHelper(this.rootState, out);
    }

    private void recursivePrintHelper(State currentState, BufferedWriter out) throws IOException {
        currentState.printed = true;
        out.write(currentState.getID() + " ");
        for (Character c : alphabet) {
            State s = currentState.getChildren().get(c);
            if (s != null) 
                out.write(s.getID() + " ");
            else
                out.write("-1 ");
        }
        if (isAccepting(currentState))
            out.write("A");
        out.newLine();
        for (Character c : alphabet) {
            State s = currentState.getChildren().get(c);
            if (s != null && !s.printed)
                recursivePrintHelper(s, out);
        }
    }

    // Accepts an input text file full of words and populates the Dawg with 
    // them. 
    public void loadDictionary(String input) throws IOException {
        BufferedReader in;
        try {
            in  = new BufferedReader(new FileReader(input));
        } catch (FileNotFoundException fnfE){
            System.out.println("Unable to find file '" + input + "'!");
            in = null;
        }


        ArrayList<String> words = getWords(in);
        // Add each word in the dictionary file to the dawg
        for (String word : words) {
            if (word != null)
                addWord(word, 0, rootState);
        }

        minimize();
    }

    public void importDawg(String input) throws IOException {
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(input));
        } catch (FileNotFoundException fnfe) {
            System.out.println("Unable to find file '" + input + "'!");
            in = null;
        }

        if (in.ready()) {
            String numStateStr = in.readLine();
            int numStates = Integer.parseInt(numStateStr);
            State[] Q = new State[numStates];
            readInStates(numStates, in, Q);
        }
    }

    private void readInStates(int numStates, BufferedReader in, State[] Q) throws IOException {
        Q[0] = rootState;
        for (int i = 0; i < numStates; i++) {
            String[] line = in.readLine().split(" ");
            if (debug) {
                for (String s : line) {
                    System.out.print(s + ",,");
                }
                System.out.println("");
            }
            int id = Integer.parseInt(line[0]);
            if (id == -1)
                continue;
            if (Q[id] == null)
                Q[id] = new State((char)id, id);
            for (int j = 1; j <= 26; j++) {
                int nId = Integer.parseInt(line[j]);
                if (nId >= 0) {
                    if (Q[nId] == null)
                        Q[nId] = new State((char)nId, nId);
                    Q[id].getChildren().put((char)((int)'a'+j-1), Q[nId]);
                }
            }
            if (line.length > 27 || line[26].equals("A")) {
                Q[id].setAccepting();
                if (debug) {
                    System.out.println(line[27]);
                }
            }
        }
    }

    public static ArrayList<String> getWords(BufferedReader in) throws IOException {
        ArrayList<String> words = new ArrayList<>();
        while (in.ready()) 
            words.add(in.readLine());
        return words;
    }

    public static final void main(String[] args) throws IOException {
        Dawg dawg = new Dawg();
        dawg.importDawg("dawg.txt");
        ArrayList<String> failedTests = new ArrayList<>();

        int goodFail = 0;
        int badFail = 0;

        ArrayList<String> goodWords = Dawg.getWords(new BufferedReader(new FileReader("good.txt")));
        for (String word : goodWords) {
            if (!dawg.isAccepted(word)) {
                failedTests.add("good word '" + word + "' was rejected");
                goodFail++;
            }
        }
        ArrayList<String> badWords = Dawg.getWords(new BufferedReader(new FileReader("bad.txt")));
        for (String word : badWords) {
            if (dawg.isAccepted(word)) {
                failedTests.add("bad word '" + word + "' was accepted");
                badFail++;
            }
        }

        System.out.println("Testing orriginal dawg");
        System.out.println("tested " + (goodWords.size() + badWords.size()) + " words");
        System.out.println("tests failed: " + (goodFail + badFail));

        if ((goodFail + badFail) > 0) {
            System.out.println("word tests that failed:");
            for (String fail : failedTests) {
                System.out.println(fail);
            }
        }
    }
}

