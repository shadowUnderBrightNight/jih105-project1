import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ac_test {

    //dictionary DLB
    private static DLB<String> DIC = new DLB<String>();
    //user DLB
    private static DLB_user<String> USER = new DLB_user<String>();

    private static String[] predictions;
    private static String[] userPredictions;
    private static String[] dicPredictions;
    private static TreeMap<String,Integer> userSuggestions;
    private static double time = 0;                 //total run time for searching
    private static int index = 0;                   //times of searching
    private static double estimatedTime;            //estimatedTime for single search
    private static char input;                      //input character
    private static String typed = "";               //the String has been input

    /**
     *
     * @throws FileNotFoundException if the dictionary file is not found
     */
    public static void main(String args[])throws FileNotFoundException,IOException{
        //read dictionary
        DIC.readDictionary();
        //read user history
        USER.readHistory();
        boolean run = true;
        Scanner scan = new Scanner(System.in);                      //Scanner object used to scan use's input
        while(run) {
            System.out.print("Enter your first character: ");
            //read user's input
            input = scan.next().charAt(0);
            while(true){
                //the end of one single word
                if(input == '$'){
                    if(typed.equals("")){break;}
                    System.out.println("WORD COMPLETED: "+ typed);
                    USER.put(typed,1);
                    typed = "";
                    DIC.resetSuggestion();
                    USER.resetSuggestion();
                    break;
                }

                //the end of one single word
                else if(Character.isDigit(input)){
                    if(typed.equals("")){break;}
                    int num = Character.getNumericValue(input);
                    System.out.println("WORD COMPLETED: "+ typed + predictions[num-1]);
                    USER.put(typed+predictions[num-1],1);
                    typed = "";
                    DIC.resetSuggestion();
                    USER.resetSuggestion();
                    break;
                }

                //the end of the run
                else if(input == '!'){
                    run = false;
                    typed = "";
                    DIC.resetSuggestion();
                    USER.resetSuggestion();
                    break;
                }
                //if the input has value
                else {
                    //reset all arrays of predictions
                    userPredictions = null;
                    dicPredictions = new String[5];
                    predictions = new String[5];
                    search(); }
                typed += input;
                printPrompt();
                System.out.print("Enter the next character: ");
                input = scan.next().charAt(0);                                      //read user's input
            }
        }
        USER.exportHistory();
        avg_time(time,index);
    }

    /**
     * print the prompt after every prediction is made
     */
    private static void printPrompt(){
        if(predictions[0]!=null) {
            System.out.println("Predictions:");
            for (int i = 0; i < 5; i++) {
                if(predictions[i]!=null){
                    System.out.print("(" + (i+1) + ")" + " " + typed + predictions[i] + " ");
                }
            }
            System.out.println(" ");
        }else{
            System.out.println("There does not exist any predictions. ");
        }
        System.out.println("whole"+String.format("(%f s)", (double) estimatedTime));
    }

    /**
     * search from a node to find suggestions
     */
    private static void search(){
        //search time ++
        index++;
        //user prediction
        long t0 = System.nanoTime();
        dicPredictions = DIC.getSuggestion(input);
        //sort the user predictions according to the suggestions
        userSuggestions = USER.getSuggestion(input);
        //dictionary prediction
        calUserPredictions();
        //put user's predictions and dictionary's predictions into predictions
        calPredictions();
        //elapsed time in milliseconds
        long t1 = System.nanoTime();
        estimatedTime = ((t1 - t0) * Math.pow(10, -9));
        time += estimatedTime;
    }

    /**
     * calcuate and print average time
     * @param time total time accumulated
     * @param index how many times searching in the DLB
     */
    private static void avg_time(double time, int index){
        double averageTime;
        if(index>0){ averageTime = time / index; } else{averageTime = 0;}
        System.out.println("Average time: "+String.format("(%f s)", (double) averageTime));
        System.out.println("Bye");
    }

    /**
     * calculate userPredictions using treeMap derived from user DLB
     */
    private static void calUserPredictions() {
        Set keys = userSuggestions.keySet();
        //if there is 5 words with the prefix in the DLB_user
        int size = 5;
        if (userSuggestions.size() < 5) {
            size = userSuggestions.size();
        }
        ;
        userPredictions = new String[size];
        //put the keys in order of value from big to small
        for (int j = 0; j < size; j++) {
            int largest = 0;
            for (Iterator i = keys.iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                Integer value = (Integer) userSuggestions.get(key);
                if (value > largest) {
                    userPredictions[j] = key;
                    largest = value;
                }
            }
            userSuggestions.remove(userPredictions[j]);
        }
    }

    /**
     *calculate the predictions based on the userPredictions and dicPredictions
     */
    private static void calPredictions(){
        int userIndex = 0;
        int predIndex = 0;
        int dicIndex = 0;
        for (int i = 0; i < userPredictions.length;i++){
            predictions[i] = userPredictions[i];
            userIndex++;
            predIndex++;
        }

        while(dicIndex<5 & predIndex<5 ) {
            Boolean add = true;
            if(dicPredictions[dicIndex]==null){break;}
            for (int i = 0; i < userPredictions.length; i++) {
                if (dicPredictions[dicIndex].equals(userPredictions[i])) {
                    add = false;
                    break;
                }
                i++;
            }
            if(add){
                predictions[predIndex] = dicPredictions[dicIndex];
                predIndex++;
            }
            dicIndex++;
        }

    }

}

