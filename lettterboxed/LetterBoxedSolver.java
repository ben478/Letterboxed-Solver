 /* Written by Ben leon
* July 2025
*
* A java program to solve the New York Times Letterboxed game with the
* fewest words possible using a breadth first search.
*/
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.DataInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;


public class LetterBoxedSolver {

    public static void main(String[] args) throws IOException {
        // Define the letters on each edge of the box.
        ArrayList<String> stringList = new ArrayList<>();

        // Create a Scanner object to read input from the console
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the four sides of the letterbox:");

        String input;
        // Loop to continuously read input until "done" is entered
        while (true) {
            System.out.print("Enter a edge: ");
            String Input = scanner.nextLine(); // Read the entire line of input

            String truncatedInput;
            if(Input.length() < 3){
                System.out.print("Enter a edge longer than 3");
            }else if (Input.length() > 3) {
                truncatedInput = Input.substring(0, 3); // Get the first 3 characters
            } else {
                truncatedInput = Input;
                stringList.add(Input); // Add the entered string to the ArrayList
            }

            
            if(stringList.size() >= 4) {
                break; // Exit the loop if 4 edges are entered
            } 
        }

        

        // Close the scanner to release system resources
        scanner.close();

        
        String[] edges = stringList.toArray(new String[0]);
        //String[] edges = { "lmi", "ecp", "utb", "kfa" };

        List<Character> letterbox = new ArrayList<Character>();
        for(String i : edges){
            i.toLowerCase();
            for(Character c : i.toCharArray()){
                letterbox.add(c);
            }
        }

        Path filePath = Paths.get("words_hard.txt");
        List<String>lines=Files.readAllLines(filePath);
        String[]array=lines.toArray(new String[lines.size()]);
        for(int i = 0; i < array.length ; i++) {
            array[i] = array[i].toLowerCase();
        }
        List<String> possibleWords = CheckWord(array, edges);
        SortBySize(possibleWords);

     
        
        sortByUniqueCharacters(possibleWords);  
        for (String str : possibleWords) {
            String formattedStrR = String.format("%-" + 14 + "s", str);
            String formattedStrL = String.format("-%" + 14 + "s", str);
        } 
        SearchTree ST = new SearchTree(possibleWords, letterbox);
        BreadthFirstSearch(ST, possibleWords).PrintToRoot();

    }
    
    private static List<String>CheckWord(String[] array, String[] edges){
        List<String> possibleWords = new ArrayList<String>();
        for(String word : array){
            int edgeindex = 0;
            for(String edge : edges){
                if(edge.indexOf(word.charAt(0)) != -1){
                    List<Character> wordAsList = new ArrayList<Character>();
                    for (int i = 0; i < word.length(); i++) {
                        wordAsList.add(word.charAt(i));
                    }
                    if(CheckRestOfWord(wordAsList, (edgeindex + 1)%4 , edges))
                        possibleWords.add(word);
                }
                edgeindex++;
            }
        }  
        return possibleWords; 
    }
    public static boolean CheckRestOfWord(List<Character> word, int edgeindex, String[] edges){
        word.remove(0);

        if (word.size() == 0){
            return true;
        }else {
            for (int i = 0; i < 3; i++) {
                if ((edges[(i+edgeindex)%4]).contains((word.get(0)).toString())) {
                    return CheckRestOfWord(word, (i + edgeindex + 1)%4, edges);
                }
            }
            return false;
        }
    }

    public static void SortBySize(List<String> unsortedList){
        Collections.sort(unsortedList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s1.length(), s2.length());
            }
        });
    }

    public static void sortByUniqueCharacters(List<String> strings) {
        Collections.sort(strings, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                int uniqueChars1 = countUniqueChars(s1);
                int uniqueChars2 = countUniqueChars(s2);

                // Sort by descending order of unique characters
                return Integer.compare(uniqueChars2, uniqueChars1);
            }

            private int countUniqueChars(String str) {
                Set<Character> uniqueChars = new HashSet<>();
                for (char c : str.toCharArray()) {
                    uniqueChars.add(c);
                }
                return uniqueChars.size();
            }
        });
    }

    public static void sortByUniqueCharacters(ArrayList<Node> nodes) {
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                int uniqueChars1 = countUniqueChars(n1);
                int uniqueChars2 = countUniqueChars(n2);

                // Sort by descending order of unique characters
                return Integer.compare(uniqueChars2, uniqueChars1);
            }

            private int countUniqueChars(Node node) {
                Set<Character> uniqueChars = new HashSet<>();
                String word = (String) node.data;
                for (char c : word.toCharArray()) {
                    uniqueChars.add(c);
                }
                return uniqueChars.size();
            }
        });
    }
    
    public static Node BreadthFirstSearch(SearchTree ST, List<String> possibleWords){
        Node cur = ST.root;
        sortByUniqueCharacters((ArrayList<Node>) cur.next);
        Queue<Node> queue = new LinkedList<>();
        for(Node child : (ArrayList<Node>) cur.next){
            queue.add(child);
        }
        while(!queue.isEmpty()){
            //pop queue
            cur = queue.remove();
            //add children to searchtree
            String childword = (String) cur.data;
            char lastLetter = childword.charAt(childword.length() - 1);
            for (String word : possibleWords) {// check what possible words can come after current word
                if (lastLetter == word.charAt(0)) {
                    Set<Character> newletterbox = new HashSet<>();
                    for (char c : (List<Character>) cur.letterbox) {
                        newletterbox.add(c);
                    }
                    char[] wordaschar = word.toCharArray();
                    for (char c : wordaschar) {
                        newletterbox.add(c);
                    }
                    List<Character> finalletterbox = new ArrayList<>(newletterbox);
                    Node nextchild = new Node(cur, word, finalletterbox);
                    cur.next.add(nextchild); // -add to searchtree
                    queue.add(nextchild);// add to fifo queue//add children to queue

                    if (nextchild.letterbox.size() == 12){ // move to bottom//check if solved
                        return nextchild; // final solution
                    }
                }
            }
        }
        return null;
    }
    
}

    class Node<String> {
        public String data;
        public ArrayList<Node> next;
        public Node parent;
        public List<Character> letterbox;

        public Node(Node parent, String data, List<Character> letterbox) {
            this.data = data;
            this.letterbox = letterbox;
            this.next = new ArrayList<Node>();
            this.parent = parent;
        }

        public void PrintToRoot(){
            Node printparent = this;
            ArrayList<Node> RootToSolution = new ArrayList<Node>();
            while(printparent != null){
                RootToSolution.add(0, printparent);
                printparent = printparent.parent;
            }
            for(Node node : RootToSolution){
                System.out.print(node.data + " ");
            }
        }
    }

    class SearchTree {
        Node root = new Node(null, "", new ArrayList<Character>());

        public SearchTree(List<String> possibleWords, List<Character> letterbox) {
            for (String str : possibleWords) {
                List<Character> letterboxUsed = new ArrayList<Character>();
                for (Character c : str.toCharArray()) {
                    if (!letterboxUsed.contains(c)) {
                        letterboxUsed.add(c);
                    }
                }
                root.next.add(new Node(root, str, letterboxUsed));
            }
        }


        public void Print(){
            System.out.println(root.next);
            for (Node node : (ArrayList<Node>) root.next){
                System.out.println(node.data + "|" + node.letterbox.size());
            }
            for (Node node : (ArrayList<Node>) root.next) {
                if (node.next != null) {
                    Print(node, 1);
                }
            }
        }
        
        private void Print(Node Pnode,int  level){
            for(Node node : (ArrayList<Node>) Pnode.next){
                for(int i=0; i<level; i++){
                    System.out.print(" ");
                }
                System.out.printf("%d " + node.data + "|" + node.letterbox.size(), level);
            }
            for (Node node : (ArrayList<Node>) Pnode.next) {
                if (node.next != null) {
                    Print(node, level+1);
                }
            }
        }
    }
   
    

