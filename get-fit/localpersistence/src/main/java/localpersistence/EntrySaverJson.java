package localpersistence;

import java.util.HashMap;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.time.Duration;

import core.EntryManager;
import core.LogEntry;
import core.LogEntry.EXERCISE_CATEGORIES;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EntrySaverJson {
    /**
     * Iterates over every entry in the provided EntryManager and adds their data as a string to a hashmap.
     * Saves the hashmap to SavedData.json.
     * @param entryManager the EntryManager instance to be saved.
     * @throws IOException if there was an issue during write.
     * @throws IllegalArgumentException if entryManager is null.
     */
    public static void save(EntryManager entryManager) throws IOException, IllegalArgumentException{
        save(entryManager, "SavedData.json");
    }

    /**
     * Iterates over every entry in the provided EntryManager and adds their data as a string to a hashmap.
     * Saves the hashmap to the specified JSON file.
     * @param entryManager the EntryManager instance to be saved.
     * @throws IOException if there was an issue during write.
     * @throws IllegalArgumentException if entryManager or saveFile is null.
     */
    public static void save(EntryManager entryManager, String saveFile) throws IOException, IllegalArgumentException{

        if (entryManager == null || saveFile == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        JSONObject json = new JSONObject();

        for (LogEntry entry : entryManager){
            HashMap<String, String> innerMap = new HashMap<>();
            innerMap.put("title", entry.getTitle());
            innerMap.put("comment", entry.getComment());
            innerMap.put("date", entry.getDate().toString());
            innerMap.put("duration", String.valueOf(entry.getDuration().getSeconds()));
            innerMap.put("feeling", entry.getFeeling().toString());
            innerMap.put("distance", entry.getDistance().toString());
            innerMap.put("maxHeartRate", entry.getMaxHeartRate().toString());
            innerMap.put("exerciseCategory", entry.getExerciseCategory().toString());
            innerMap.put("exerciseSubCategory", entry.getExerciseSubCategories().toString());


            json.put(entry.getId(), innerMap);
        }
        File file = new File(saveFile);
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(json.toJSONString());
        writer.flush();

        writer.close();
        }
    
    /**
     * Loads SavedData.json and constructs LogEntries which it appends to the provided EntryManager.
     * @param entryManager the EntryManager to load data into.
     * @throws FileNotFoundException if SavedData.JSON could not be found.
     * @throws IllegalArgumentException if entryManager is null.
     */
    public static void load(EntryManager entryManager) throws FileNotFoundException, IllegalArgumentException {
        load(entryManager, "SavedData.json");
    }

    /**
     * Loads a specified JSON file and constructs LogEntries which it appends to the provided EntryManager.
     * @param entryManager the EntryManager to load data into.
     * @param saveFile the path of the JSON file to load from.
     * @throws FileNotFoundException if the specified path could not be found.
     * @throws IllegalArgumentException if the entryManager or saveFile is null.
     */
    public static void load(EntryManager entryManager, String saveFile) throws FileNotFoundException, IllegalArgumentException {

        if (entryManager == null || saveFile == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        JSONParser jsonParser = new JSONParser();
        File file = new File(saveFile);
        Scanner reader = new Scanner(file);
        String dataString = "";

        while (reader.hasNextLine()){
            dataString += reader.nextLine();
        }
        reader.close();

        try{
            JSONObject jsonObject = (JSONObject) jsonParser.parse(dataString);

            for (Object key : jsonObject.keySet()){
                String id = (String) key;
                
                //Suppressed unchecked warning. Any better solution Stefan?:
                @SuppressWarnings("unchecked")
                HashMap<String, String> innerMap = (HashMap<String, String>) jsonObject.get(key);

                LogEntry.Subcategories subCategory = null;
                for (EXERCISE_CATEGORIES category : LogEntry.EXERCISE_CATEGORIES.values()) {
                    for (LogEntry.Subcategories sub : category.getSubcategories()) {
                       try {
                           subCategory = sub.getValueOf(innerMap.get("exerciseSubCategory"));
                       } catch (Exception e) {

                       }
                    }   
                }

                if (subCategory == null) {
                    throw new IllegalStateException("Malformed save file");
                }

                entryManager.addEntry(
                    id, 
                    innerMap.get("title"),
                    innerMap.get("comment"),
                    LocalDate.parse(innerMap.get("date")),
                    Duration.ofSeconds(Long.parseLong(innerMap.get("duration"))),
                    Double.parseDouble(innerMap.get("feeling")),
                    Double.parseDouble(innerMap.get("distance")),
                    Double.parseDouble(innerMap.get("maxHeartRate")),
                    LogEntry.EXERCISE_CATEGORIES.valueOf(innerMap.get("exerciseCategory")),
                    subCategory
                    
                );
            }

        }
        catch (ParseException pException){
            throw new IllegalStateException("Could not load data from file");
        }
    }
}
