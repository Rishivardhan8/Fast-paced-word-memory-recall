import java.util.*;
import java.io.*;

public class VerbalMemoryGame2 {

    static ArrayList<String> words = new ArrayList<>(Arrays.asList(
            "Accept","Except","Affect","Effect","Allusion","Illusion",
            "Breath","Breathe","Capital","Capitol","Complement","Compliment",
            "Desert","Dessert","Principal","Principle","Stationary","Stationery",
            "Ensure","Insure","Implicit","Explicit","Loose","Lose",
            "Patience","Patients","Peace","Piece","Resign","Re-sign",
            "Weather","Whether","Advice","Advise","Lead","Led"
    ));

    static HashSet<String> seenWords = new HashSet<>();

    // Queue
    static Queue<String> wordHistory = new LinkedList<>();

    static Random random = new Random();

    static int score = 0;
    static int lives = 3;

    static final String SCORE_FILE = "scores.txt";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("===== VERBAL MEMORY GAME =====");
        System.out.println("1 → Lives Mode");
        System.out.println("2 → Timer Mode");
        System.out.println("3 → View Top Scores");

        int mode = sc.nextInt();

        if(mode == 1){
            livesMode(sc);
            saveScore(score);
        }
        else if(mode == 2){
            timerMode(sc);
            saveScore(score);
        }
        else if(mode == 3){
            showTopScores();
        }
        else{
            System.out.println("Invalid choice");
        }

        sc.close();
    }

    // -------- CLEAR SCREEN --------
    public static void clearScreen(){
        try{
            if(System.getProperty("os.name").contains("Windows")){
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch(Exception e){
            System.out.println("Screen clear not supported.");
        }
    }

    // -------- PAUSE METHOD --------
    public static void pause(int ms){
        try{
            Thread.sleep(1200 );
        }catch(Exception e){}
    }

    // ---------------- LIVES MODE ----------------
    public static void livesMode(Scanner sc){

        System.out.println("\nLives Mode Started (3 Lives)");
        pause(1500);

        while(lives > 0){

            clearScreen();

            System.out.println("Lives: " + lives + "   Score: " + score);

            String word = words.get(random.nextInt(words.size()));

            // Queue stores history
            wordHistory.offer(word);

            System.out.println("\nWORD: " + word);
            System.out.print("1 → Seen | 2 → New : ");

            int choice = sc.nextInt();

            boolean wasSeen = linearSearch(word);

            if((choice == 1 && wasSeen) || (choice == 2 && !wasSeen)){
                score++;
                seenWords.add(word);
                System.out.println("Correct!");
            }
            else{
                lives--;
                System.out.println("Wrong! Lives left: " + lives);
            }

            System.out.println("Score: " + score);

            pause(1500);
        }

        System.out.println("\nGame Over!");
        System.out.println("Final Score: " + score);
    }

    // ---------------- TIMER MODE ----------------
    public static void timerMode(Scanner sc){

    System.out.println("\nTimer Mode Started (30 seconds)");
    pause(1500);

    long startTime = System.currentTimeMillis();
    long timeLimit = 30000;

    while(System.currentTimeMillis() - startTime < timeLimit){

        clearScreen();

        long timeLeft = (timeLimit - (System.currentTimeMillis() - startTime))/1000;

        System.out.println("Score: " + score + "   Time Left: " + timeLeft + "s");

        String word = words.get(random.nextInt(words.size()));

        // Queue stores history
        wordHistory.offer(word);

        System.out.println("\nWORD: " + word);
        System.out.print("1 → Seen | 2 → New : ");

        int choice = sc.nextInt();

        boolean wasSeen = linearSearch(word);

        if((choice == 1 && wasSeen) || (choice == 2 && !wasSeen)){
            score++;
            seenWords.add(word);
            System.out.println("Correct!");
        }
        else{
            System.out.println("Wrong!");
        }

        System.out.println("Score: " + score);
    }

    System.out.println("\nTime Up!");
    System.out.println("Final Score: " + score);
}

    // ------------- LINEAR SEARCH ----------------
    public static boolean linearSearch(String word){

        for(String w : seenWords){
            if(w.equals(word)){
                return true;
            }
        }

        return false;
    }

    // ------------- SAVE SCORE ----------------
    public static void saveScore(int newScore){

        try{
            FileWriter fw = new FileWriter(SCORE_FILE, true);
            fw.write(newScore + "\n");
            fw.close();
        }
        catch(Exception e){
            System.out.println("Error saving score.");
        }
    }

    // ------------- SHOW TOP 10 SCORES ----------------
    public static void showTopScores(){

        ArrayList<Integer> scores = new ArrayList<>();

        try{
            File file = new File(SCORE_FILE);

            if(!file.exists()){
                System.out.println("No scores yet.");
                return;
            }

            Scanner fileScanner = new Scanner(file);

            while(fileScanner.hasNextInt()){
                scores.add(fileScanner.nextInt());
            }

            fileScanner.close();
        }
        catch(Exception e){
            System.out.println("Error reading scores.");
        }

        Collections.sort(scores, Collections.reverseOrder());

        System.out.println("\n===== TOP 10 SCORES =====");

        for(int i=0;i<Math.min(10, scores.size());i++){
            System.out.println((i+1) + ". " + scores.get(i));
        }
    }
}
