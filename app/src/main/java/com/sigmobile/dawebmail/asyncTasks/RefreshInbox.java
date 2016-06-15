package com.sigmobile.dawebmail.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sigmobile.dawebmail.database.EmailMessage;
import com.sigmobile.dawebmail.database.User;
import com.sigmobile.dawebmail.database.UserSettings;
import com.sigmobile.dawebmail.network.RestAPI;
import com.sigmobile.dawebmail.utils.Constants;

import java.util.ArrayList;

/**
 * Created by rish on 6/10/15.
 */
public class RefreshInbox extends AsyncTask<Void, Void, Void> {

    private RefreshInboxListener listener;
    private Context context;
    private ArrayList<EmailMessage> refreshedEmails;
    private String refreshType;
    private String folder;
    private User user;
    private boolean result;

    public RefreshInbox(User user, Context context, RefreshInboxListener refreshInboxListener, String folder, String refreshType) {
        this.context = context;
        this.listener = refreshInboxListener;
        this.refreshType = refreshType;
        this.folder = folder;
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onPreRefresh();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        RestAPI restAPI = new RestAPI(user, context);
        if (refreshType.equals(Constants.REFRESH_TYPE_REFRESH))
            result = restAPI.refresh(folder);
        else if (refreshType.equals(Constants.REFRESH_TYPE_LOAD_MORE))
            result = restAPI.loadMore(folder, 15);
        refreshedEmails = restAPI.getNewEmails();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (result)
            UserSettings.setLastRefreshed(context, "" + System.currentTimeMillis());

        listener.onPostRefresh(result, refreshedEmails, user);
    }
}