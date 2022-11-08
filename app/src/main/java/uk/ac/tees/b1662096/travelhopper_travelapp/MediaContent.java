package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MediaContent {

    public final static ArrayList<String> mediaList = new ArrayList<>();

//    public static ArrayList<String> fetchImagesFromStorage() {
//        List mediaStoreColumns = List.of(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID);
//        Cursor imageCursor = ;
//        Media newMediaItem = new Media();
//
//        newMediaItem.mediaUri = Uri.fromFile(mediaFile);
//        newMediaItem.mediaDisplayName = getDisplayNameFromUri(newMediaItem.mediaDisplayName);
//        newMediaItem.mediaDate = getDateFromUri(newMediaItem.mediaUri);
//        mediaList.add(0, newMediaItem);
//    }

    private static String getDateFromUri(Uri mediaUri) {
        String[] uriPathSplit = mediaUri.getPath().split("/");
        String mediaFileName = uriPathSplit[uriPathSplit.length - 1];
        String mediaFileNameNoExt = mediaFileName.split("\\.")[0];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(new Date(Long.parseLong(mediaFileNameNoExt)));
    }

    private static String getDisplayNameFromUri(String mediaDisplayName) {
        return null;
    }

}