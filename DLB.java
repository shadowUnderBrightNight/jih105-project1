import java.io.*;
import java.util.*;

public class DLB<T> {

    //initial node in the linked list
    private static Node root;
    private static Node searchingNode = root;
    private static String[] searchingArray = new String[5];
    private static int searchingIndex = 0;

    /**
     * Node initializing used in the linked list
     */
    public static class Node{
        public char r;
        public Node ChildChar;
        public Node SiblingChar;
        //node with char input
        public Node(char r){
            this.r = r;
            this.ChildChar = null;
            this.SiblingChar = null;
        }
    }

    /**
     * Initializes an empty linked list
     */
    public DLB(){

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
     * read data from dictionary in the same project
     * @throws FileNotFoundException if this dictionary is not found
     */
    public void readDictionary() throws FileNotFoundException {
        String word;
        File file = new File("dictionary.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            word = sc.nextLine();
            put(word);
        }
        sc.close();

    }

    /**
     *  put new data into the DLB structure
     */
    public void put(String word){
        //if root is null, put the first character in the root
        if (root == null){ root = new Node(word.charAt(0)); searchingNode = root;}
        Node node = root;
        word += '^';
        int index = 0;
        while(index < word.length()){
            char r = word.charAt(index);
            node = searchSibling(node, r);
            //go to child
            if(node.r == r){
                //if the ChildChar has no value and the word in not end
                if (node.ChildChar == null & (index < word.length()-1)){ buildChild(node, word.charAt(index+1)); }
                node = node.ChildChar;
            }
            //build a sibling in the linked list and go to child
            else{
                node = buildSibling(node,r);
                //if the ChildChar has no value and the word in not end
                if (node.ChildChar == null & (index < word.length()-1)){ buildChild(node, word.charAt(index+1)); }
                node = node.ChildChar;
            };
            index++;
        }
    }

    /**
     * search if the string is in the DLB
     * @return {@code false} if the string is not found {@code true} if the string is found
     */
    public boolean search(String word){
        Node node = root;
        int index = 0;
        word += '^';
        //if there is nothing in the DLB, return false
        if (root == null){ return false; }
        while(index < word.length()) {
            char r = word.charAt(index);
            node = searchSibling(node, r);
            //go to child
            if (node.r == r) { node = node.ChildChar; }
            //string not found
            else { return false; }
            index++;
        }
        //if get out of the loop, the string exists
        return true;
    }

    /**
     *
     * @param r the character needed to search that
     * @return return the array of suggestions
     */
    public String[] getSuggestion(char r){
        searchingArray = new String[5];
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
     * reset the searching node to root
     */
    public void resetSuggestion(){ searchingNode = root; }

    private void searchingSuggestion(String temp, Node searchingNode){

        //if the char of this node is '^'
        if(searchingNode.r == '^'){
            searchingArray[searchingIndex] = temp;
            searchingIndex += 1;
        }
        //if the char of this node is a character
        else {
            searchingSuggestion(temp + searchingNode.r, searchingNode.ChildChar);
        }
        /**
         * the recursive I metioned in the additional comment in the info sheet
         */
        if(searchingIndex < 5 & searchingNode.SiblingChar != null){
            searchingSuggestion(temp, searchingNode.SiblingChar);
        }
    }

    /**
     * Is this linked list empty?
     * @return {@code true} if this linked list is empty and {@code false}otherwise
     */
    public boolean isEmpty(){ return root == null; }
}
