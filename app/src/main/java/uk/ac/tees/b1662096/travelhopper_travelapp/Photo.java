package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;


public class Photo {
    private final Uri photoUri;
    private final String photoTitle;
    private final String photoDescription;
    private final String photoDateTaken;
    private final String photoLatitude;
    private final String photoLongitude;

    public Photo(Uri photoUri, String photoTitle, String photoDescription, String photoDateTaken, String photoLatitude, String photoLongitude) {
        this.photoUri = photoUri;
        this.photoTitle = photoTitle;
        this.photoDescription = photoDescription;
        this.photoDateTaken = photoDateTaken;
        this.photoLatitude = photoLatitude;
        this.photoLongitude = photoLongitude;
    }

    public final static List<Photo> photoList = new ArrayList<>();

    public Uri getPhotoUri() {
        return photoUri;
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public String getPhotoDescription() {
        return photoDescription;
    }

    public String getPhotoDateTaken() {
        return photoDateTaken;
    }

    public String getPhotoLatitude() {
        return photoLatitude;
    }

    public String getPhotoLongitude() {
        return photoLongitude;
    }

}

//
//    /**
//     * A map of sample (placeholder) items, by ID.
//     */
//    public static final Map<String, Photo> MEDIA_ITEM_MAP = new HashMap<String, Photo>();
//
//    private static final int COUNT = 25;
//
//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createMediaItem(i));
//        }
//    }
//
//    private static void addItem(Photo item) {
//        MEDIA_ITEMS.add(item);
//        MEDIA_ITEM_MAP.put(item.photoTitle, item);
//    }
//
//    private static Photo createMediaItem(int position) {
//        return new Photo(String.valueOf(position), "Item " + position, makeDetails(position));
//    }
//
//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
//        for (int i = 0; i < position; i++) {
//            builder.append("\nMore details information here.");
//        }
//        return builder.toString();
//    }

//}