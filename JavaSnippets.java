import java.util.*;
import java.util.stream.IntStream;
import java.io.*;

class Solution {

  /* declarations */
  static Map<Character, State> myMap = new HashMap<>();
  
  static int myArray[] = { 1, 2, 3, 4, 5 };

  public static void main(String[] args) {

    System.out.println("tree_path");
    /* MAP and entry formatting */
    // split entry with space separator
    Scanner in = new Scanner(System.in);
    String input = in.nextLine();
    for (String state : in.nextLine().split("\\s+")) {
      // put in a MAP TODO explain more
      myMap.computeIfAbsent(state.charAt(0), k -> new State(k));
    }

    // display a map
    System.err.println(myMap.toString());
    myMap.forEach((k, v) -> System.err.println(" key : " + k + " value : " + v));

    // sort, operations on collections
    IntStream.of(14, 35, -7, 46, 98).min();
    Arrays.asList(14, 35, -7, 46, 98).stream().min(Integer::compare);
    Arrays.asList(14, 35, -7, 46, 98).stream().reduce(Integer::min);
    Collections.min(Arrays.asList(14, 35, -7, 46, 98));

  }
}

class State {
  Character str;

  public State(Character k) {
    str = k;
  }
}