import java.util.*;
import java.util.stream.*;
import java.io.*;

class Solution {

  /* declarations */
static String myString = "test";
  
  static Map<Character, State> myMap = new HashMap<>();

  private static final Map<String, String> ABBREVIATIONS = Map.of(
    "sp", " ",
    "bS", "\\",
    "sQ", "'",
    "nl", "\n"
  );

  public static Map<String,String> translationMap = Stream.of(new Object[][] { 
     { "sp", " " }, { "bS", "\\" }, { "sQ", "'" }, { "nl", "\\n" }})
     .collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));

  static int myArray[] = { 1, 2, 3, 4, 5 };

  //TODO map.of etc
  // TODO init de map

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

  /*  operations on collection */

    // sort
    IntStream.of(14, 35, -7, 46, 98).min();
    Arrays.asList(14, 35, -7, 46, 98).stream().min(Integer::compare);
    Arrays.asList(14, 35, -7, 46, 98).stream().reduce(Integer::min);
    Collections.min(Arrays.asList(14, 35, -7, 46, 98));
    

    
    // filter
    // Filter list of numbers, return Map with Collectors.partitioningBy
    Map<Boolean, List<Integer>> passedFailedMap = Stream.of(49, 58, 76, 82, 88, 90)
        .collect(Collectors.partitioningBy(i -> i > 60));

  /* String, characters */

    // init from A to Z
    Stream.iterate('a', i -> ++i).limit(26).forEach(System.out::println);

String resultat = Stream.of( null, null, "valeur defaut").filter( Objects::nonNull ).findFirst().get();
    
    //repeat
    String stringABC="abc";
    int count=3;
    System.out.println("stringABC :"+stringABC.repeat(count));
    //TODO mettre un assert
    
    //Arrays to char puis list<Character>
    List<Character> charList = myString
      .chars() // Convert to an IntStream
      .mapToObj(i -> (char) i) // Convert int to char, which gets boxed to Character
      .collect(Collectors.toList()); // Collect in a List<Character>

          //List of Characters to String          
    String result = charList.stream()
      .map(String::valueOf) // convert in string
      .collect(Collectors.joining()); // using collect and joining() method

    // Verify if Exists in a String
    List<String> wordList = Arrays.asList("java", "jdk", "spring", "maven");
    String tweet = "This is an example tweet talking about java and maven.";
    wordList.stream().anyMatch(tweet::contains);

//Occurence d'une séquence dans une string
    //Il est aussi possible de faire un découpage avec split pour y aller mot à mot
    String str = "helloslkhellodjladfjhello";
    String findStr = "hello";
    int lastIndex = 0;
    int count2 = 0;
    
    while (lastIndex != -1) {
    
        lastIndex = str.indexOf(findStr, lastIndex);
    
        if (lastIndex != -1) {
            count2++;
            lastIndex += findStr.length();
        }
    }
    System.out.println(count2);
  }
}

class State {
  Character str;

  public State(Character k) {
    str = k;
  }
}

/*Java classe statique qu'on ne veut pas instancier
Private  constructor(){
throw new AssertionError("should never be instancied");
}*/
/*
.filter(Text.class::isInstance)
.map(Text.class::cast)
.forEach(childrenToRemove::add);
Instead of for-each-add, you can collect stream items with Collectors.toSet():
Set<Text> childrenToRemove = group.getChildren()
    // ...
    .collect(Collectors.toSet());

Instead of using
AN ANONYMOUS CLASS
you can use
A LAMBDA EXPRESSION
And if this just calls one method, you can use
A METHOD REFERENCE mais c'est juste du sucre syntaxique?
DSL comme sucre syntaxique?*/