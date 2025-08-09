package dev.thesheep.onridePhotos.content;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoLayout {
    private List<FaceLayout> faceLayouts;
    private String backgroundImage;
    private String foregroundImage;

    public String getBackgroundImage()
    {
        return "image/" + backgroundImage;
    }

    public String getForegroundImage()
    {
        return "image/" + foregroundImage;
    }

    public PhotoLayout(String backgroundImage, String foregroundImage)
    {
        this.foregroundImage = foregroundImage;
        this.backgroundImage = backgroundImage;
        faceLayouts = new ArrayList<>();
    }

    public FaceLayout getFaceLayout(int ind)
    {
        if(ind > faceLayouts.size() - 1)
        {
            return null;
        }

        return faceLayouts.get(ind);
    }

    /**
     *
     * @param faceLayout
     * @return itself
     */
    public PhotoLayout withFace(FaceLayout faceLayout)
    {
        faceLayouts.add(faceLayout);
        return this;
    }

    public static PhotoLayout loadFromFile(File file) {
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            String background = json.get("background").getAsString();
            String foreground = json.get("foreground").getAsString();

            PhotoLayout layout = new PhotoLayout(background, foreground);

            JsonArray facesArray = json.getAsJsonArray("faces");
            for (JsonElement elem : facesArray) {
                JsonObject obj = elem.getAsJsonObject();
                String type = obj.get("type").getAsString();
                int x = obj.get("x").getAsInt();
                int y = obj.get("y").getAsInt();
                int width = obj.get("width").getAsInt();
                int height = obj.get("height").getAsInt();

                layout.withFace(new FaceLayout(FaceType.valueOf(type), x, y, width, height));
            }

            return layout;
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

}
