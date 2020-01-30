package edu.pdx.cs410J.rag4;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.ParserException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

public class Project2 {

  //MAIN FUNCTION
  public static void main(String[] args) throws IOException, ParserException {

    int printFlag = 0; //flag to tell if the -print option exists
    int textFileFlag = 0;
    int fileExistsFlag = 0;
    int numberOfOptions = 0;
    int invalidOption = 0;
    String [] options = {"-README", "-print", "-textFile"};

    int airlineFlightsCommand = 8;
    String fileName = "";
    ArrayList<String> newCommandArgs = new ArrayList<String>();

    if(args.length <= 0) { //error if there are NO command line arguments
      System.err.println("There are NO command line arguments.\n");
      commandLineInterface();
      System.exit(1);
    }
    for (int i = 0; i < args.length; i++){ //checks for -README option, if it exists, execute README then exit

      if (args[i].equals("-README")){
        printReadMe();
        System.exit(1);
      }

      if (args[i].charAt(0) == '-'){
        for (int j = 0; j < options.length; j++){
          if (args[i].equals(options[j])){
            invalidOption = 0;
            break;
          }else{
            invalidOption = 1;
          }
        }
        if (invalidOption == 1){
          System.err.println("THE OPTION: " + args[i] + " DOES NOT EXIST. \n");
          commandLineInterface();
          System.exit(1);
        }
      }

      if (args[i].equals("-print")){ // if -print option exists, turn on printFlag then go to next for loop iteration
        printFlag = 1;
        numberOfOptions += 1;
        continue;
      }
      if (args[i].equals("-textFile")){
        fileName = args[i+1] + ".txt";
        textFileFlag = 1;
        numberOfOptions += 2;
        continue;
      }
    }

    if (args.length < airlineFlightsCommand + numberOfOptions){ //if command line arguments has too few contents, print error
      System.err.println("You have too FEW command line arguments. \n");
      commandLineInterface();
      System.exit(1);
    }
    if (args.length > airlineFlightsCommand + numberOfOptions){ //if command line arguments has too many contents, print error
      System.err.println("You have far too MANY command line arguments. \n");
      commandLineInterface();
      System.exit(1);
    }

    for (int i = 0; i < args.length; i++){ //for loop to put respective airlineName, flightNumber, src, depart, dest, and arrive from command line arguments into an arraylist
      if (args[i].equals("-print")){ //if -print option exists, go to next for loop iteration
        continue;
      }
      if (args[i].equals("-textFile")){
        i++;
        continue;
      }

      if (args[i].indexOf('/') == 2 || args[i].indexOf('/') == 1) { // concatenate the date and time command line arguments into one string
        if(args[i].charAt(1) == ('/')){ // insert a 0 if date format: m/dd/yyyy
          args[i] = "0" + args[i];
        }
        if(args[i].charAt(4) == ('/')){ // insert a 0 if date format: mm/d/yyyy OR m/d/yyyy --> mm/d/yyyy
          args[i] =  args[i].substring(0,3) + "0" + (args[i].substring(3,args.length));
        }
        if(args[i+1].equals("-textFile")){
          if(args[i+3].indexOf(':') == 1){
            args[i+3] = "0" + args[i+3];
          }
          newCommandArgs.add(args[i] + " " + args[i+3]);
          if (i + 4 == args.length){
            break;
          }
          i += 3;
          continue;
        }
        if(args[i+1].equals("-print")){ // this considers if -print option exists between date and time
          if(args[i+2].indexOf(':') == 1){ // insert a 0 if date format: h:mm
            args[i+2] = "0" + args[i+2];
          }
          newCommandArgs.add(args[i] + " " + args[i + 2]);
          if (i + 3 == args.length) { // if it's already on arrival, end the for loop
            break;
          }
          i += 2;
          continue;
        }
        else {
          if(args[i+1].indexOf(':') == 1){ // insert a 0 if date format: h:mm
            args[i+1] = "0" + args[i+1];
          }
          newCommandArgs.add(args[i] + " " + args[i + 1]); //concatenate date and time into one string
          if (i + 2 == args.length) { // if it's already on arrival, end the for loop
            break;
          }
          i += 1;
          continue;
        }
      }
      newCommandArgs.add(args[i]); //appends command line argument into arraylist
    }

    String flightNumberCheck = newCommandArgs.get(1); //easy string variable to get the flight number
    if (!flightNumberCheck.matches("[0-9]+")){ // if flight number contains anything other than numbers, throw an illegal argument exception
      throw new IllegalArgumentException("Flight number contains illegal character. Flight number must be INTEGER. \n");
    }

    File exists = new File(fileName);
    if (textFileFlag == 1 && exists.isFile()) {
      //System.out.println("THE FILE EXISTS");
      TextParser toParse = new TextParser(fileName);
      Airline airline = (Airline) toParse.parse();
      toParse.checkIfEqual(newCommandArgs.get(0), airline.getName());
      Flight flight = new Flight(Integer.parseInt(newCommandArgs.get(1)), newCommandArgs.get(2), newCommandArgs.get(3), newCommandArgs.get(4), newCommandArgs.get(5));
      airline.addFlight(flight); // add this newly created flight with initialized values into airline
      if (printFlag == 1) { // if printFlag is on, print the airline and it's flight descriptions
        System.out.println("AIRLINE: " + airline.getName());
        System.out.println(flight.toString()); //since -print option exists, print the flight description
      }
      TextDumper toDump = new TextDumper(fileName);
      toDump.dump(airline);
      System.exit(1);
    }
    ArrayList<AbstractFlight> flightArray = new ArrayList<AbstractFlight>(); // new Abstract FLight Array List
    Airline airline = new Airline(newCommandArgs.get(0), flightArray); // new Airline object
    Flight flight = new Flight(Integer.parseInt(newCommandArgs.get(1)), newCommandArgs.get(2), newCommandArgs.get(3), newCommandArgs.get(4), newCommandArgs.get(5));
    airline.addFlight(flight); // add this newly created flight with initialized values into airline
    if (printFlag == 1) { // if printFlag is on, print the airline and it's flight descriptions// if (printFlag == 1) { // if printFlag is on, print the airline and it's flight descriptions
      System.out.println("AIRLINE: " + airline.getName());
      System.out.println(flight.toString()); //since -print option exists, print the flight description
    }
    if (textFileFlag == 1) {
      TextDumper toDump = new TextDumper(fileName);
      toDump.dump(airline);
    }

    System.exit(1);
  }

  /**Command Line Interface
   * prints out the command line interface, should be used when improper usage of the command line arguments in the main function occurs
   */
  public static void commandLineInterface(){
    System.out.println("usage: java edu.pdx.cs410J.rag4.Project1 [options] <args>\n" +
            "\targs are (in this order):\n" +
            "\t\tairline The name of the airline\n" +
            "\t\tflightNumber The flight number\n" +
            "\t\tsrc Three-letter code of departure airport\n" +
            "\t\tdepart Departure date and time (24-hour time)\n" +
            "\t\tdest Three-letter code of arrival airport\n" +
            "\t\tarrive Arrival date and time (24-hour time)\n" +
            "\toptions are (options may appear in any order):\n" +
            "\t\t-textFile file Where to read/write the airline info\n" +
            "\t\t-print Prints a description of the new flight\n" +
            "\t\t-README Prints a README for this project and exits\n" +
            "\tDate and time should be in the format: mm/dd/yyyy hh:mm\n");
  }

  /**README
   * prints out the README, should be used when -README option exists as the first command line argument in the main function
   */
  public static void printReadMe(){
        /*System.out.println("PROJECT 1: DESIGNING AN AIRLINE APPLICATION\n" +
                "SUBMITION/DEVELOPED BY: Ramon Guarnes 942268924\n" +
                "CLASS: CS410P Advanced Programmin with Java\n" +
                "TEACHER: David Whitlock\n" +
                "DUE DATE: January 22, 2019 before 5:30PM\n" +
                "DESCRIPTION: In Project 1, my objective is to extend the AbstractAirline class with the class Airline, \n" +
                "extend the AbstractFlight class with the class flight, implement assigned functionality for the main function \n" +
                "within Project1.java, and to create test suites for all of these classes/files. \n\n" +
                "In the Airline class, aside from error handling, I implemented a constructor to consider the airline's name, and a \n" +
                "list of available flights the airline contains. In the Flight class, aside from error handling, I implemented a constructor\n" +
                "to consider the flight's unique number, three-letter source code, departure date and time, three-letter destination code, \n" +
                "and arrival date and time. Within the main function, the purpose of this function is to create an airline and flight to add\n" +
                "to the airline using the values of the user's input command line arguments. The user also has the option to print the descriptions\n" +
                "of the flight, as well as to be able to view this README.");*/
    System.out.println("PROJECT 2: DESIGNING AN AIRLINE APPLICATION\n" +
            "SUBMITION/DEVELOPED BY: Ramon Guarnes 942268924\n" +
            "CLASS: CS410P Advanced Programmin with Java\n" +
            "TEACHER: David Whitlock\n" +
            "DUE DATE: January 29, 2019 before 5:30PM\n" +
            "DESCRIPTION: In Project 1, my objective is to extend the AbstractAirline class with the class Airline, \n" +
            "extend the AbstractFlight class with the class flight, implement assigned functionality for the main function \n" +
            "within Project1.java, and to create test suites for all of these classes/files. \n\n" +
            "In the Airline class, aside from error handling, I implemented a constructor to consider the airline's name, and a \n" +
            "list of available flights the airline contains. In the Flight class, aside from error handling, I implemented a constructor\n" +
            "to consider the flight's unique number, three-letter source code, departure date and time, three-letter destination code, \n" +
            "and arrival date and time. Within the main function, the purpose of this function is to create an airline and flight to add\n" +
            "to the airline using the values of the user's input command line arguments. The user also has the option to print the descriptions\n" +
            "of the flight, as well as to be able to view this README.");
  }
}