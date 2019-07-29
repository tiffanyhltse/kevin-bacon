import java.io.IOException;
import java.util.*;
import java.io.*;

import au.com.bytecode.opencsv.CSVReader;
//import com.opencsv.CSVReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/*
    I used simple json and open csv
 */

public class Bacon {
    public static void main(String[] args){

        //Make sure that they provide a file name
        /*if(args.length < 1){
            System.out.println("You need to provide a file name!");
            return;
        }*/
        JSONParser parser = new JSONParser();
        HashMap<String,List<String>>neighbors = new HashMap<String, List<String>>();

        try {
            //open and read csv file line by line
            CSVReader reader = new CSVReader(new FileReader("/Users/tiffanytse/IdeaProjects/SixDegreesofKevinBacon/src/tmdb_5000_credits.csv"));
            String[] parts;
            parts = reader.readNext();

            //while there are still lines in csv file
            while (null != (parts = reader.readNext())) {

                //if we are not able to parse this line go to the next line
                if(parts.length != 4){
                    continue;
                }

                try {
                    //cast is the 3rd column
                    //crew is the 4th column

                    String cast = parts[2];
                    String crew = parts[3];
                    //convert cast and crew to json
                    //loop through them and pull out all the names
                    JSONArray json = (JSONArray) parser.parse(cast);
                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < json.size(); i++) {
                        JSONObject person = (JSONObject) json.get(i);
                        names.add((String) person.get("name"));
                    }
                    JSONArray json2 = (JSONArray) parser.parse(crew);
                    for (int i = 0; i < json2.size(); i++) {
                        JSONObject person = (JSONObject) json2.get(i);
                        names.add((String) person.get("name"));
                    }
                    //System.out.println("Names: " + names);
                    //loop through all the names and connect each combo of names using neighbors map
                    for (String person : names) {
                        person = person.toLowerCase();
                        for (String other : names) {
                            other = other.toLowerCase();
                            if (neighbors.containsKey(person)) {
                                neighbors.get(person).add(other);
                            } else {
                                neighbors.put(person, new ArrayList<String>());
                                neighbors.get(person).add(other);
                            }
                        }
                    }
                } catch (ParseException e) {
                }
            }
            reader.close();
        } catch (IOException e) {
        }
        while(true) {


            //ask the user for 2 actor names
            //if the name they provide doesn't exist
            //then ask user for another name
            Scanner s = new Scanner(System.in);
            System.out.print("Name of Actor 1: ");
            String name1 = s.nextLine().toLowerCase();
            while (!neighbors.containsKey(name1)) {
                System.out.println("Actor not found!");
                System.out.print("Name of Actor 1: ");
                name1 = s.nextLine().toLowerCase();
            }
            System.out.print("Name of Actor 2: ");
            String name2 = s.nextLine().toLowerCase();
            while (!neighbors.containsKey(name2)) {
                System.out.println("Actor not found!");
                System.out.print("Name of Actor 2: ");
                name2 = s.nextLine().toLowerCase();
            }
            //create a map to store the actor that linked to each other
            HashMap<String, String> previous = new HashMap<String, String>();
            Deque people = new ArrayDeque();
            people.addLast(name1);
            previous.put(name1, null);
            boolean found = false;
            //do BFS
            while (!people.isEmpty() && !found) {
                String person = (String) people.pollFirst();

                //loop throuh all the neighbors and add them to the queue
                // if not already visited
                for (String n : neighbors.get(person)) {
                    if (!previous.containsKey(n)) {
                        people.addLast(n);
                        previous.put(n, person);
                        //if we got to the person that we are looking for
                        if (n.equals(name2)) {
                            //print out all the people that led up to this person
                            String res = n;
                            String temp = person;
                            while (previous.get(temp) != null) {
                                res = temp + " -> " + res;
                                temp = previous.get(temp);
                            }
                            res = temp + " -> " + res;
                            System.out.println(res);
                            found = true;
                        }
                    }
                }
            }
            if(!found) {
                System.out.println("We cannot link between these 2 actors!");
            }
        }
    }
}
