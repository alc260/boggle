/*
Boggle.java
"ai" to win Boggle
java Boggle <board filename>
*/

import java.io.*;
import java.util.*;

public class Boggle {
  public final static int N = 0, NE = 1, E = 2, SE = 3, S = 4, SW = 5, W = 6, NW = 7;
  public final static int[] directions = { N, NE, E, SE, S, SW, W, NW };
  public final static int[] rowMods = { -1, -1, 0, 1, 1, 1, 0, -1 };
  public final static int[] colMods = { 0, 1, 1, 1, 0, -1, -1, -1 };
  public static int MAX_LENGTH;
  public static TreeSet<String> dictionary = new TreeSet<String>();

  public static void main(String[] args) throws Exception {
    //long start = System.currentTimeMillis();
    
    if (args.length != 1) {
      System.out.println("Usage: java Boggle <board filename>");
      System.exit(0);
    }

    int[] dimension = new int[1];
    String[][] board = loadMatrix(args[0], dimension);

    //read dictionary into a TreeSet
    BufferedReader dictionaryReader = new BufferedReader(new FileReader("dictionary.txt"));
    while (dictionaryReader.ready()) dictionary.add(dictionaryReader.readLine());

    //the maximum length of a word in the board. used to subSet the dictionary to check prefixes
    MAX_LENGTH = dimension[0] * dimension[0] + 1;

    //loop through each place on the board as the starting point
    for (int i = 0; i < dimension[0]; i++)
      for (int j = 0; j < dimension[0]; j++)
        findWords(i, j, board, "");

    //System.out.format("Time: %d\n", System.currentTimeMillis() - start);
  }

  //loads file into a 2D String array and uses an int array to return the dimension back to main
  private static String[][] loadMatrix(String infileName, int[] dimension) throws Exception {
    Scanner infile = new Scanner( new File(infileName) );
    int rows = infile.nextInt();
    int cols = rows;
    dimension[0] = rows;
    String[][] matrix = new String[rows][cols];

    for(int r = 0; r < rows ; r++)
      for(int c = 0; c < cols; c++)
        matrix[r][c] = infile.next();

    infile.close();
    return matrix;
  }

  //recursive algorithm to find the words
  private static void findWords(int row, int col, String[][] board, String word) {
    boolean found = false;
    //add the next letter to the running word
    word = appendWord(word, board, row, col);

    //only count words with length > 3
    if (word.length() >= 3) {
      //check if word is in dictionary. if it is, remove it (to avoid duplicates) and print
      if (dictionary.contains(word)) {
        found = true;
        dictionary.remove(word);
        System.out.println(word);
      }
    }

    if (!found) {
      //create a word with the "max value" that a possible string could have in the dictionary
      //  this way we can create a subset of possible words with the word as a prefix
      String newWord = word;
      for (int i = word.length(); newWord.length() < MAX_LENGTH; i++) newWord += "z";

      //if the subset of words is empty, the word is not a prefix to any others. exit.
      if (dictionary.subSet(word, newWord).size() == 0) return;
    }

    //loop through possible directions
    for (int dir : directions)
      //check if a step in any direction is safe
      if (isSafeStep(row + rowMods[dir], col + colMods[dir], board)) {
        board[row][col] = board[row][col].toUpperCase(); //mark
        findWords(row + rowMods[dir], col + colMods[dir], board, word); //recurse!
        board[row][col] = board[row][col].toLowerCase(); //unmark
      }

    //if there is nowhere to go, stop
  }

  private static String appendWord(String word, String[][] board, int row, int col) {
    return word + board[row][col];
  }

  private static boolean isSafeStep(int row, int col, String[][] board) {
    boolean flag;
    try {
      //check if the character at the proposed step is lowercase (unmarked)
      flag = Character.isLowerCase(board[row][col].charAt(0));
    } catch (Exception e) {
      //if the last block threw an ArrayIndexOutOfBounds Exception, that step does not exist
      return false;
    }
    //return true if the proposed letter was lowercase, false if uppercase
    return flag;
  }
}
