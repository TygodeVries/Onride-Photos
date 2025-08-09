package dev.thesheep.onridePhotos.content;

public class FaceLayout {
    private FaceType faceType;

    private int x;
    private int y;
    private int width;
    private int height;

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public FaceLayout(FaceType faceType, int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.faceType = faceType;
    }

    /**
     * Returns the URL of the mc-heads url from a face.
     * @return
     */
    public String getWebEndpointForFace(Face face)
    {
        ///  Flats
        if(faceType == FaceType.FLAT_HEAD)
            return "https://mc-heads.net/avatar/" + face.getPlayerUUID() + "/16";

        if(faceType == FaceType.FLAT_BODY)
            return "https://mc-heads.net/player/" + face.getPlayerUUID() + "/16";

        ///  Iso heads
        if(faceType == FaceType.ISO_HEAD_RIGHT)
            return "https://mc-heads.net/head/" + face.getPlayerUUID() + "/16/right";

        if(faceType == FaceType.ISO_HEAD_LEFT)
            return "https://mc-heads.net/head/" + face.getPlayerUUID() + "/16/left";

        /// Iso body
        if(faceType == FaceType.ISO_BODY_RIGHT)
            return "https://mc-heads.net/body/" + face.getPlayerUUID() + "/16/right";

        if(faceType == FaceType.ISO_BODY_LEFT)
            return "https://mc-heads.net/body/" + face.getPlayerUUID() + "/16/left";

        return null;
    }
}
