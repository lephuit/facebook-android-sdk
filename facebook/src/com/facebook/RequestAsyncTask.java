/**
 * Copyright 2012 Facebook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

@TargetApi(3)
public class RequestAsyncTask extends AsyncTask<Void, Void, List<Response>> {
    private static final String TAG = RequestAsyncTask.class.getCanonicalName();

    private final HttpURLConnection connection;
    private final List<Request> requests;

    private Handler handler;
    private Exception exception;

    public RequestAsyncTask(Request... requests) {
        this(Arrays.asList(requests));
    }

    public RequestAsyncTask(List<Request> requests) {
        this(Request.toHttpConnection(requests), requests);
    }

    public RequestAsyncTask(HttpURLConnection connection, Request... requests) {
        this(connection, Arrays.asList(requests));
    }

    public RequestAsyncTask(HttpURLConnection connection, List<Request> requests) {
        this.requests = requests;
        this.connection = connection;
    }

    protected final Exception getException() {
        return exception;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("{RequestAsyncTask: ").append(" connection: ").append(connection)
                .append(", requests: ").append(requests).append("}").toString();
    }

    Handler getHandler() {
        return handler;
    }

    void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // We want any callbacks to go to a handler on this thread.
        handler = new Handler();
    }

    @Override
    protected void onPostExecute(List<Response> result) {
        super.onPostExecute(result);

        if (exception != null) {
            Log.d(TAG, String.format("onPostExecute: exception encountered during request: ", exception.getMessage()));
        }
    }

    @Override
    protected List<Response> doInBackground(Void... params) {
        try {
            return Request.executeConnection(handler, connection, requests);
        } catch (Exception e) {
            exception = e;
            return null;
        }
    }
}
