import java.io.*;
import java.util.*;

public class DLB_user<T> {
    public DLB_user(){ }
    //initial node in the linked list
    private static Node root;
    private static Node searchingNode = root;
    private static TreeMap<String,Integer> searchingArray = new TreeMap<String, Integer>();
    private static int searchingIndex = 0;
    private static PrintWriter file;

    /**
     * Node initializing used in the linked list
     */
    public static class Node{
        public char r;
        public Node ChildChar;
        public Node SiblingChar;
        public int freq;
        //node with char input
        public Node(char r){
            this.r = r;
            this.ChildChar = null;
            this.SiblingChar = null;
            this.freq = 0;
        }
    }



    /**
     * search if the sibling node has the charater
     * @return the Node if found
     * @return the Node at the last of the linked list if not found
     */
    public Node searchSibling(Node node, char r){
        //this is the node that needs
        if (node.r == r){ return node; }
        //this node is the last node
        if (node.SiblingChar == null){ return node; }
        //go to next sibling node
        else{ return searchSibling(node.SiblingChar, r); }
    }

    /**
     * extend the silbing linked list if the value is not found
     * @return the node just built
     */
    private Node buildSibling(Node node, char r){
        Node newNode = new Node(r);
        node.SiblingChar = newNode;
        return newNode;
    }

    /**
     * extend the child linked list if the value is not found
     * @return the node just built
     */
    private Node buildChild(Node node, char r){
        Node newNode = new Node(r);
        node.ChildChar = newNode;
        return newNode;
    }

    /**
     * read history of user's input
     * @throws FileNotFoundException if history is not found
     */
    public void readHistory()throws FileNotFoundException,IOException{
        String word;
        int freq;
        File file = new File("user_history.txt");
        if(file.exists()) {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                word = sc.next();
                freq = sc.nextInt();
//                word+='^';
                put(word, freq);
            }
            sc.close();
        }
        else{
            file.createNewFile();
        }
    }

    /**
     *  put data into the DLB structure
     *  if the word already exists, frequency + 1
     */
    public void put(String word, int freq){
        //if root is null, put the first character in the root
        if (root == null){ root = new Node(word.charAt(0)); searchingNode = root;}
        Node node = root;
        word+='^';
        int index = 0;
        while(index < word.length()){
            char r = word.charAt(index);
            node = searchSibling(node, r);
            index++;
            //go to child
            if(node.r == r){
                //if the ChildChar has no value and the word in not end
                if (node.ChildChar == null & (index < word.length())){ node = buildChild(node, word.charAt(index)); }
                else if(index < word.length()){ node = node.ChildChar; }
            }
            //build a sibling in the linked list and go to child
            else{
                node = buildSibling(node,r);
                //if the ChildChar has no value and the word in not end
                if (node.ChildChar == null & (index < word.length())){ node =  buildChild(node, word.charAt(index)); }
                else if(index < word.length()){ node = node.ChildChar; }
            };
        }
        //add the frequency of the word
        node.freq += freq;
    }

    /**
     * search if the string is in the DLB
     * @return {@code false} if the string is not found {@code true} if the string is found
     */
    public int search(String word){
        Node node = root;
        int freq = 0;
        int index = 0;
        word += '^';
        //if there is nothing in the DLB, return false
        if (root == null){ return 0; }
        while(index < word.length()) {
            char r = word.charAt(index);
            index++;
            node = searchSibling(node, r);
            if (node.r == r & index<word.length()) { node = node.ChildChar; }
            else if(node.r == r & index == word.length()){return node.freq;}
            //string not found
            else { return 0; }
        }
        //if get out of the loop, the string exists
        return 0;
    }

    /**
     *function called to export user DLB data to user history
     */
    public void exportHistory()throws IOException{
        if(!isEmpty()) {
            Node node = root;
            String temp = "";
            file = new PrintWriter("user_history.txt");
            file.print("");
            file.close();
            exportNodes(temp, node);
        }
    }

    /**
     *  only called by getSuggestion, used to search for the correct suggestions
     * @param temp the recursive string
     * @param searchingNode the recursive node
     */
    private void exportNodes(String temp, Node searchingNode)throws IOException{
        //if the char of this node is digit
        if(searchingNode.r == '^'){
            //put string to the history
            FileWriter fileWriter = new FileWriter("user_history.txt", true);
            file = new PrintWriter(fileWriter);
            file.println("");
            file.print(temp+" ");
            file.print(searchingNode.freq);
            file.close();
        }
        else{
            exportNodes(temp+searchingNode.r, searchingNode.ChildChar);
        }
        if(searchingNode.SiblingChar != null){
            exportNodes(temp, searchingNode.SiblingChar);
        }
    }

    /**
     *
     * @param r the character needed to search that
     * @return return the array of suggestions
     */
    public TreeMap<String, Integer> getSuggestion(char r){
        if(isEmpty()){return searchingArray;}
        searchingArray = new TreeMap<String, Integer>();
        searchingIndex = 0;
        searchingNode = searchSibling(searchingNode,r);
        if(searchingNode.r == r){
            searchingNode = searchingNode.ChildChar;
            String temp = "";
            searchingSuggestion(temp,searchingNode);
        }
        return searchingArray;
    }

    /**
     *  only called by getSuggestion, used to search for the correct suggestions
     * @param temp the recursive string
     * @param searchingNode the recursive node
     */
    private void searchingSuggestion(String temp, Node searchingNode){
        //if the char of this node is digit
        if(searchingNode.r == '^'){
            searchingArray.put(temp,searchingNode.freq);
            searchingIndex += 1;
        }
        //if the char of this node is a character
        else {
            searchingSuggestion(temp + searchingNode.r, searchingNode.ChildChar);
        }
        if(searchingNode.SiblingChar != null){
            searchingSuggestion(temp, searchingNode.SiblingChar);
        }


    }

    /**
     * reset the searching node to root
     */
    public void resetSuggestion(){ searchingNode = root; }

    /**
     * Is this linked list empty?
     * @return {@code true} if this linked list is empty and {@code false}otherwise
     */
    public boolean isEmpty(){ return root == null; }
}
