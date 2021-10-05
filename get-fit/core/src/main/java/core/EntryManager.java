package core;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class EntryManager implements Iterable<LogEntry> {

    private HashMap<String, LogEntry> entryMap = new HashMap<String, LogEntry>();

    /**
     * An entry manager instance is a wrapper for a list of logEntries.
     * Functions as the API interface for the core-module.
     */
    public EntryManager() {}

    /**
     * Adds a fresh entry to this EntryManager. Generates an id internally.
     * @param title the title field for the new LogEntry as a string.
     * @param comment the comment field for the new LogEntry as a string.
     * @param date the date field for the new LogEntry as a time.LocalDate instance.
     * @param duration the duration field for the new LogEntry as a time.Duration instance.
     * @throws IllegalArgumentException if any of the arguments are null.
     * @return the generated id for the new LogEntry as a string.
     */
    public String addEntry(String title, String comment, LocalDate date, Duration duration) throws IllegalArgumentException {

        if (title == null || comment == null || date == null || duration == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        String id = String.valueOf(entryMap.size());
        addEntry(id, title, comment, date, duration);
        return id;
    }

    /**
     * Adds an already existing LogEntry (ie. one that has an id) to this EntryManager
     * @param id the id for the new LogEntry
     * @param title the title field for the new LogEntry as a string.
     * @param comment the comment field for the new LogEntry as a string.
     * @param date the date field for the new LogEntry as a time.LocalDate instance.
     * @param duration the duration field for the new LogEntry as a time.Duration instance.
     * @throws IllegalArgumentException if any of the arguments are null or the entry allready exists.
     */
    public void addEntry(String id, String title, String comment, LocalDate date, Duration duration) throws IllegalArgumentException {

        if (id == null || title == null || comment == null || date == null || duration == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        if (entryMap.containsKey(id)) {
            throw new IllegalArgumentException("Entry allready exists");
        }
        entryMap.put(id, new LogEntry(id, title, comment, date, duration));
    }

    /**
     * Gets a LogEntry instance by its id, if such a LogEntry exists.
     * @param id the id to be searched for.
     * @throws IllegalArgumentException if id is null or the entry doesn't exist
     * @return the LogEntry instance with the associated id.
     */
    public LogEntry getEntry(String id) throws IllegalArgumentException {

        if (id == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        if (entryMap.containsKey(id)) {
            return entryMap.get(id);
        } else {
            throw new IllegalArgumentException("Entry does not exits");
        }
    }

    /**
     * Removes a LogEntry by its id, if such a LogEntry exists.
     * @param id the id to be removed.
     * @throws IllegalArgumentException if id is null or the entry doesn't exist
     */
    public void removeEntry(String id) throws IllegalArgumentException {

        if (id == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        
        if (entryMap.containsKey(id)) {
            entryMap.remove(id);
        } else {
            throw new IllegalArgumentException("Entry does not exits");
        }
    }

    /**
     * @return the number of LogEntries in this EntryManager
     */
    public int entryCount() {
        return this.entryMap.size();
    }

    /**
     * Gives an iterator for the LogEntries in this Entrymanager in an arbitrary order.
     * @return an iterator of LogEntry instances.
     */
    @Override
    public Iterator<LogEntry> iterator() {
        return this.entryMap.values().iterator();
    }
}