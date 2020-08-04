import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Alicia Grevsten
 * This program has 3 main features:
 * It allows the user to look up a character by a number. (Display basic info for that character.)
 * It then optionally allow the user to display the names of all sworn members of the previous characters
 * house.
 * It then looks up all pov characters in the books published by "Bantam Books" and then display this
 * information in a grid/table/visually appealing way.
 */

public class Main {

    public static void main(String[] args) {

        String characterNumber = introduction();                            // Gets the user input

        JSONObject character = getCharacter(characterNumber);               // Stores information about character
        displayCharacter(character);                                        // Prints the information

        String houseURL = getHouseUrl(character);                           // The url to get information about house
        JSONObject house = getJSONObject(houseURL);                         // Stores information about house

        ArrayList<String> swornMembers = getSwornMembersList(house);        // A list of all the sworn members names

        displayMembers(swornMembers);                                       // Prints all the sworn members

        String publisher = "Bantam Books";
        Map<String, String[]> povCharacters = getPOVCharacters(publisher);  // Stores a list of pov and connects them
                                                                            // to the right book
        displayPOVCharacters(povCharacters, publisher);                     // Displays the povs and book names

    }

    public static String introduction() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Fire and Ice, character search.");
        System.out.print("Character number: ");

        // -- Insert code for validating if user input is a number --

        return scanner.next();
    }

    public static JSONObject getCharacter(String characterNumber) {
        String characterSearchUrl = "https://anapioficeandfire.com/api/characters/" + characterNumber;
        return getJSONObject(characterSearchUrl);
    }

    public static String getHouseUrl(JSONObject character) {
        String houseURL = character.get("allegiances").toString();
        houseURL = houseURL.substring(2, houseURL.length() - 2);              // Removes extra "[ and ]" from beginning and end (I feel like there's a better way to do this..)
        return houseURL;
    }

    public static ArrayList<String> getSwornMembersList(JSONObject house) {
        ArrayList<String> swornMembersNames = new ArrayList<>();

        String swornMembersURLs = house.get("swornMembers").toString();     // Gets all the members in one single string
        JSONArray membersArray = new JSONArray(swornMembersURLs);              // Creates an array of all the members urls

        for (int i = 0; i < membersArray.length(); i++) {
            String memberUrl = membersArray.getString(i);                      // Gets the url as a string from the jsonarray
            JSONObject member = getJSONObject(memberUrl);                   // Gets all the information of a specific member and stores it in a JSONObject
            String memberName = member.get("name").toString();              // Fetches the name of the member
            swornMembersNames.add(memberName);                                   // Adds members names to member arraylist
        }

        return swornMembersNames;
    }

    public static Map<String, String[]> getPOVCharacters(String publisher) {
        JSONArray books = getAllBooksBy(publisher);

        Map<String, String[]> bookPovListPair = new HashMap<>();

        // Stores all the povCharacter arrays in an array
        for (int i = 0; i < books.length(); i++) {
            JSONObject book = books.getJSONObject(i);    //   Temporarly stores a book

            JSONArray povCharacters = new JSONArray(book.get("povCharacters").toString());
            String[] characters = new String[povCharacters.length()];

            for (int j = 0; j < povCharacters.length(); j++) {
                String characterUrl = povCharacters.getString(j);
                JSONObject character = getJSONObject(characterUrl);
                String characterName = character.get("name").toString();
                characters[j] = characterName;
            }

            bookPovListPair.put(book.get("name").toString(), characters);   //  Stores books and povCharacters as a pair
        }

        return bookPovListPair;
    }

    public static JSONArray getAllBooksBy(String publisher) {
        String allBooksURL = "https://www.anapioficeandfire.com/api/books";
        JSONArray allBooks = getJSONArray(allBooksURL);     //  Gets all the books
        JSONArray booksPublishedBy = new JSONArray();       //  Stores the books we're searching for

        for (int i = 0; i < allBooks.length(); i++) {
            JSONObject book = allBooks.getJSONObject(i);    //   Temporarly stores a book

            if (book.get("publisher").toString().equals(publisher)) // Checks if author key has value of publisher
                booksPublishedBy.put(book);                 // Adds the book to the array of search-books
        }
        return booksPublishedBy;
    }

    public static JSONArray getJSONArray(String u) {
        try {
            URL url = new URL(u);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String inputLine;
                StringBuffer content = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    content.append(inputLine);
                }
                br.close();

                return new JSONArray((content.toString()));

            } else {
                System.out.println("Error");
                System.out.println("Server responded with: " + con.getResponseCode());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static JSONObject getJSONObject(String u) {
        try {
            URL url = new URL(u);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String inputLine;
                StringBuffer content = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    content.append(inputLine);
                }
                br.close();

                return new JSONObject((content.toString()));

            } else {
                System.out.println("Error");
                System.out.println("Server responded with: " + con.getResponseCode());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static void displayCharacter(JSONObject character) {
        System.out.println("Name: " + character.get("name").toString());
        System.out.println("Aliases: " + character.get("aliases").toString());
        System.out.println("Gender: " + character.get("gender").toString());
        System.out.println("Born: " + character.get("born").toString());
        System.out.println("Played By: " + character.get("playedBy").toString());
        System.out.println("Culture: " + character.get("culture").toString());
        System.out.println();
        System.out.println("Working..");
    }

    public static void displayMembers(ArrayList<String> swornMembers) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to display all the sworn members of this characters house [yes/no]? ");
        String answer = scanner.next().toLowerCase();
        switch (answer) {
            case "yes":
                for (int i = 0; i < swornMembers.size(); i++) {
                    System.out.println((i + 1) +". " + swornMembers.get(i));
                }
                break;
            case "no":
                System.out.println("As you wish.");
                break;
            default:
                System.out.println("I take that as a no.");
                break;
        }
        scanner.close();
        System.out.println();
        System.out.println("Working...");
        System.out.println();
    }

    public static void displayPOVCharacters(Map<String, String[]> bookPovListPair, String publisher) {
        System.out.println("All books published by \"" + publisher + "\": ");
        for (Map.Entry<String, String[]> pair: bookPovListPair.entrySet()) {
            System.out.println();
            System.out.println("Book: \"" + pair.getKey() + "\"");
            System.out.print("POV: ");
            for (int i = 0; i < pair.getValue().length; i++) {
                if (i < pair.getValue().length - 1) {
                    System.out.print(pair.getValue()[i] + ", ");
                } else {
                    System.out.print(pair.getValue()[i]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }


}
