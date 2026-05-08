package dev.thesheep.onridePhotos.database.providers;

import dev.thesheep.onridePhotos.OnridePhotos;
import dev.thesheep.onridePhotos.content.Photo;
import dev.thesheep.onridePhotos.database.Database;
import dev.thesheep.onridePhotos.database.ImageMetadata;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;


public class SupabaseProvider implements Database {

    String project;
    String key;
    String bucket;
    String table;

    public SupabaseProvider(String project, String key, String bucket, String table)
    {
        this.project = project;
        this.key = key;
        this.bucket = bucket;
        this.table = table;
    }

    @Override
    public void uploadPhoto(ImageMetadata metadata, byte[] file) {

        String layout = metadata.layout;
        String[] players = metadata.players;

        String playersJson = toJsonArray(players);


        // Setup the json so we can easily upload it all at once instead of making a document and doing all that stuff.
        String json = """
    {
      "layout": "%s",
      "players": "%s"
    }
    """.formatted(
                escapeJson(layout),
                playersJson
        );

        // First we put in the row
        insertRowAsync(json)
                .thenAccept(id -> {

                    if (id == null) {
                        return;
                    }

                    // We then use the key that the database assigned to upload the image to that path, so it's easy to find again later.

                    String path = id + ".png";

                    uploadImageAsync(path, file)
                            .thenAccept(success -> {

                                Bukkit.getScheduler().runTask(
                                        OnridePhotos.getInstance(),
                                        () -> {
                                            if (success) {
                                                // # TODO, maybe send a message to the players?
                                            } else {
                                                OnridePhotos.getInstance()
                                                        .getLogger()
                                                        .severe("Failed image upload");
                                            }
                                        }
                                );
                            });
                });
    }

    private String toJsonArray(String[] array) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.length; i++) {

            sb.append(escapeJson(array[i]));

            if (i < array.length - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    private String escapeJson(String input) {

        if (input == null) {
            return "";
        }

        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private CompletableFuture<Long> insertRowAsync(String jsonBody) {

        HttpClient client = HttpClient.newHttpClient();

        String url = "https://" + project + ".supabase.co";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/rest/v1/" + table))
                .header("apikey", key)
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return client.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {

                    if (response.statusCode() >= 200 &&
                            response.statusCode() < 300) {


                        // Get the ID, because that is the only thing we need.
                        String body = response.body();
                        String idString = body
                                .split("\"id\":")[1]
                                .split(",")[0]
                                .replace("}", "")
                                .replace("]", "")
                                .trim();

                        return Long.parseLong(idString);
                    }

                    // Throw an error on the main thread only.
                    Bukkit.getScheduler().runTask(
                            OnridePhotos.getInstance(),
                            () -> OnridePhotos.getInstance()
                                    .getLogger()
                                    .severe("Failed to create row! " +
                                            response.statusCode() +
                                            " " +
                                            response.body() +
                                            " >>> " +
                                            jsonBody)
                    );

                    return null;
                });
    }

    private CompletableFuture<Boolean> uploadImageAsync(
            String path,
            byte[] file
    ) {

        HttpClient client = HttpClient.newHttpClient();

        String url = "https://" + project + ".supabase.co";

        // Upload the image
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        url +
                                "/storage/v1/object/" +
                                bucket +
                                "/" +
                                path))
                .header("apikey", key)
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "image/png")
                .POST(HttpRequest.BodyPublishers.ofByteArray(file))
                .build();

        // Return a result based on the status code of the request.
        return client.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString())
                .thenApply(response ->
                        response.statusCode() >= 200 &&
                                response.statusCode() < 300
                );
    }
}
