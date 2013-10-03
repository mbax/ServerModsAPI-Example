import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class is a barebones example of how to use the BukkitDev ServerMods API to check for file updates.
 * <br>
 * See the README file for further information of use.
 */
public class Update {

    public class FileInfo {
        public String name;
        public String downloadUrl;
        public String releaseType;
        public String fileName;
        public String gameVersion;
    }

    // The project's unique ID
    private final int projectID;

    // An optional API key to use, will be null if not submitted
    private final String apiKey;

    // Static information for querying the API
    private static final String API_QUERY = "/servermods/files?projectIds=";
    private static final String API_HOST = "https://api.curseforge.com";

    /**
     * Check for updates anonymously (keyless)
     *
     * @param projectID The BukkitDev Project ID, found in the "Facts" panel on the right-side of your project page.
     */
    public Update(int projectID) {
        this(projectID, null);
    }

    /**
     * Check for updates using your Curse account (with key)
     *
     * @param projectID The BukkitDev Project ID, found in the "Facts" panel on the right-side of your project page.
     * @param apiKey Your ServerMods API key, found at https://dev.bukkit.org/home/servermods-apikey/
     */
    public Update(int projectID, String apiKey) {
        this.projectID = projectID;
        this.apiKey = apiKey;

        query();
    }

    /**
     * Query the API to find the latest approved file's details.
     */
    public void query() {
        URL url = null;

        try {
            // Create the URL to query using the project's ID
            url = new URL(API_HOST + API_QUERY + projectID);
        } catch (MalformedURLException e) {
            // There was an error creating the URL

            e.printStackTrace();
            return;
        }

        try {
            // Open a connection and query the project
            URLConnection conn = url.openConnection();

            if (apiKey != null) {
                // Add the API key to the request if present
                conn.addRequestProperty("X-API-Key", apiKey);
            }

            // Add the user-agent to identify the program
            conn.addRequestProperty("User-Agent", "ServerModsAPI-Example (by Gravity)");

            // Read the response of the query
            // The response will be in a JSON format, so only reading one line is necessary.
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();

            // Parse the array of files from the query's response
            Type type = new TypeToken<List<FileInfo>>() {}.getType();
            List<FileInfo> array = new Gson().fromJson(response, type);

            if (array.size() > 0) {
                // Get the newest file's details
                FileInfo latest = array.get(array.size() - 1);

                // Get the version's title
                String versionName = latest.name;

                // Get the version's link
                String versionLink = latest.downloadUrl;

                // Get the version's release type
                String versionType = latest.releaseType;

                // Get the version's file name
                String versionFileName = latest.fileName;

                // Get the version's game version
                String versionGameVersion = latest.gameVersion;

                System.out.println(
                        "The latest version of " + versionFileName +
                                " is " + versionName +
                                ", a " + versionType.toUpperCase() +
                                " for " + versionGameVersion +
                                ", available at: " + versionLink
                );
            } else {
                System.out.println("There are no files for this project");
            }
        } catch (IOException e) {
            // There was an error reading the query

            e.printStackTrace();
            return;
        }
    }
}
