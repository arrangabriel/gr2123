package integration;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import client.LogClient;
import client.ServerResponseException;
import localpersistence.EntrySaverJson;
import restserver.GetfitController;
import restserver.GetfitApplication;
import restserver.GetfitService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = {GetfitController.class, GetfitApplication.class, GetfitService.class})
public class GetfitIntegrationTest {

    @LocalServerPort
    int port = 8080;
    

    @Autowired
    public GetfitController controller;

    private LogClient logClient;

    @BeforeEach
    public void startClient() throws InterruptedException {
        this.logClient = new LogClient("http://localhost", port);
        clearServerList();
    }

    @BeforeEach
    public void clearSaveData() {
        File f = new File(EntrySaverJson.SYSTEM_SAVE_LOCATION);
        f.delete();
    }

    /**
     * Iterates over the ids retreived from getLogEntryList and delets every LogEntry
     */
    private void clearServerList() {
        List<HashMap<String, String>> ids = null;
        try {
            ids = this.logClient.getLogEntryList();
        } catch (Exception e) {
            Assertions.fail();
        }
        ListIterator<HashMap<String, String>> it = ids.listIterator();
        while (it.hasNext()) {
            String id = it.next().get("id");
            try {
                this.logClient.deleteLogEntry(id);
            } catch (Exception e) {
                Assertions.fail();
            }
        }
    }

    @Test
    public void testCompilation() {
        Assertions.assertNotNull(controller);
    }

    @Test
    public void testEntryCreation() {
        HashMap<String, String> entry = new HashMap<>();

        entry.put("title", "Example title");
        entry.put("comment", "Example content");
        entry.put("date", "2020-01-01");
        entry.put("feeling", "7");
        entry.put("duration", "3600");
        entry.put("distance", "3.0");
        entry.put("maxHeartRate", "150");
        entry.put("exerciseCategory", "STRENGTH");
        entry.put("exerciseSubCategory", "PULL");

        try {
            logClient.addLogEntry(entry);
        } catch (Exception e) {
            Assertions.fail("Could not create entry");
        }
    }

    private String createEntry(
            String title, 
            String comment,
            String date,
            String feeling,
            String duration,
            String distance,
            String maxHeartRate,
            String exerciseCategory,
            String exerciseSubCategory
        ) {

        HashMap<String, String> entry = new HashMap<>();

        entry.put("title", title);
        entry.put("comment", comment);
        entry.put("date", date);
        entry.put("feeling", feeling);
        entry.put("duration", duration);
        entry.put("distance", distance);
        entry.put("maxHeartRate", maxHeartRate);
        entry.put("exerciseCategory", exerciseCategory);
        entry.put("exerciseSubCategory", exerciseSubCategory);

        try {
            return logClient.addLogEntry(entry);
        } catch (Exception e) {
            Assertions.fail();
            return null;
        }
    }

    @Test
    private void testEntryDeletion() {
        String id = createEntry(
                "Example title", 
                "Example content",
                "2020-01-01",
                "7",
                "3600",
                "3.0",
                "150",
                "STRENGTH",
                "PULL"
        );
        try {
            this.logClient.getLogEntry(id);
        } catch (Exception e) {
            Assertions.fail();
        }
        try {
            this.logClient.deleteLogEntry(id);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertThrows(ServerResponseException.class, () -> {
            this.logClient.getLogEntry(id);
        });
    }

    @Test
    public void testGetLogEtry() {

        HashMap<String, String> originalEntry = new HashMap<>();
        originalEntry.put("title", "Cool title");
        originalEntry.put("comment", "Example content");
        originalEntry.put("date", "2020-01-01");
        originalEntry.put("feeling", "7");
        originalEntry.put("duration", "3600");
        originalEntry.put("distance", "3.0");
        originalEntry.put("maxHeartRate", "150");
        originalEntry.put("exerciseCategory", "STRENGTH");
        originalEntry.put("exerciseSubCategory", "PULL");

        String id = createEntry(
            originalEntry.get("title"),
            originalEntry.get("comment"),
            originalEntry.get("date"),
            originalEntry.get("feeling"),
            originalEntry.get("duration"),
            originalEntry.get("distance"),
            originalEntry.get("maxHeartRate"),
            originalEntry.get("exerciseCategory"),
            originalEntry.get("exerciseSubCategory")
        );
        try {
            HashMap<String, String> retreivedEntry = this.logClient.getLogEntry(id);
            for (String key : originalEntry.keySet()) {
                Assertions.assertEquals(originalEntry.get(key), retreivedEntry.get(key));
            }
        } catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertThrows(ServerResponseException.class, () -> {
            this.logClient.getLogEntry("-1");
        });
    }

    @Test
    public void testGetLogEntryList() {

        HashMap<String, String> entry1 = new HashMap<>();
        entry1.put("title", "Cool title");
        entry1.put("comment", "Example content");
        entry1.put("date", "2020-01-01");
        entry1.put("feeling", "7");
        entry1.put("duration", "3600");
        entry1.put("distance", "3.0");
        entry1.put("maxHeartRate", "150");
        entry1.put("exerciseCategory", "STRENGTH");
        entry1.put("exerciseSubCategory", "PULL");

        try {
            this.logClient.addLogEntry(entry1);
        } catch (URISyntaxException | InterruptedException | ExecutionException | ServerResponseException e) {
            Assertions.fail("Failed to add log entry");
        }

        HashMap<String, String> entry2 = new HashMap<>();
        entry2.put("title", "Another cool title");
        entry2.put("comment", "This exercise was even cooler");
        entry2.put("date", "2020-01-02");
        entry2.put("feeling", "8");
        entry2.put("duration", "3600");
        entry2.put("distance", "4.2");
        entry2.put("maxHeartRate", "180");
        entry2.put("exerciseCategory", "STRENGTH");
        entry2.put("exerciseSubCategory", "LEGS");

        try {
            this.logClient.addLogEntry(entry2);
        } catch (URISyntaxException | InterruptedException | ExecutionException | ServerResponseException e) {
            Assertions.fail("Failed to add log entry");
        }

        HashMap<String, String> entry3 = new HashMap<>();
        entry3.put("title", "Coolest title");
        entry3.put("comment", "This was hands down the best exercise I have ever had");
        entry3.put("date", "2020-01-03");
        entry3.put("feeling", "10");
        entry3.put("duration", "10800");
        entry3.put("distance", "6.9");
        entry3.put("maxHeartRate", "230");
        entry3.put("exerciseCategory", "STRENGTH");
        entry3.put("exerciseSubCategory", "PUSH");

        try {
            this.logClient.addLogEntry(entry3);
        } catch (URISyntaxException | InterruptedException | ExecutionException | ServerResponseException e) {
            Assertions.fail("Failed to add log entry");
        }

        HashMap<String, String> entry4 = new HashMap<>();
        entry4.put("title", "Swimming");
        entry4.put("comment", "This was a very wet exercise");
        entry4.put("date", "2020-01-05");
        entry4.put("feeling", "10");
        entry4.put("duration", "3600");
        entry4.put("distance", "10");
        entry4.put("maxHeartRate", "200");
        entry4.put("exerciseCategory", "SWIMMING");
        entry4.put("exerciseSubCategory", "LONG");
        
        try {
            this.logClient.addLogEntry(entry4);
        } catch (URISyntaxException | InterruptedException | ExecutionException | ServerResponseException e) {
            Assertions.fail("Failed to add log entry");
        }

        try {
            List<HashMap<String, String>> entries = this.logClient.getLogEntryList();
            Assertions.assertEquals(4, entries.size());
        } catch (Exception e) {
            Assertions.fail();
        }
        
        try {
            List<HashMap<String, String>> entries = this.logClient.getLogEntryList(new LogClient.ListBuilder().category("STRENGTH"));
            Assertions.assertEquals(3, entries.size());
        } catch (Exception e) {
            Assertions.fail();
        }

        try {
            List<HashMap<String, String>> entries = this.logClient.getLogEntryList(new LogClient.ListBuilder().category("STRENGTH").subCategory("PUSH"));
            Assertions.assertEquals(1, entries.size());
        } catch (Exception e) {
            Assertions.fail();
        }
        
        try {
            List<HashMap<String, String>> entries = this.logClient.getLogEntryList(new LogClient.ListBuilder().date("2020-01-03-2020-01-04"));
            Assertions.assertEquals(2, entries.size());
        } catch (Exception e) {
            Assertions.fail();
        }

        try {
            List<HashMap<String, String>> entries = this.logClient.getLogEntryList(new LogClient.ListBuilder());
            List<HashMap<String, String>> entriesReversed = this.logClient.getLogEntryList(new LogClient.ListBuilder().reverse());
            Collections.reverse(entriesReversed);
            Assertions.assertEquals((Collection<HashMap<String, String>>) entriesReversed, entries);
        } catch (Exception e) {
            Assertions.fail();
        }

        try {
            List<HashMap<String, String>> entries = this.logClient.getLogEntryList(new LogClient.ListBuilder().sort("date"));
            List<HashMap<String, String>> expected = Arrays.asList(entry1, entry2, entry3);

            ListIterator<HashMap<String, String>> iterator = expected.listIterator();
            while (iterator.hasNext()) {
                HashMap<String, String> actualEntry = entries.get(iterator.nextIndex());
                HashMap<String, String> expectedEntry = iterator.next();

                for (String key : expectedEntry.keySet()) {
                    Assertions.assertEquals(expectedEntry.get(key), actualEntry.get(key));
                }
            }
        } catch (URISyntaxException | InterruptedException | ExecutionException |ServerResponseException e) {
            Assertions.fail();
        }
    }

}