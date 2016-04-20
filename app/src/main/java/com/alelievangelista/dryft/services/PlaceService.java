package com.alelievangelista.dryft.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by aevangelista on 16-04-19.
 */
public class PlaceService extends IntentService {

    private final String LOG_TAG = PlaceService.class.getSimpleName();

    public static final String FETCH_PLACE = "com.alelievangelista.dryft.services.action.FETCH_PLACE";
    public static final String ADD_PLACE = "com.alelievangelista.dryft.services.action.ADD_PLACE";
    public static final String DELETE_PLACE = "com.alelievangelista.dryft.services.action.DELETE_PLACE";

    public static final String PLACE_ID = "it.jaschke.alexandria.services.extra.PLACE_ID";

    public PlaceService() {
        super("Dryft");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
