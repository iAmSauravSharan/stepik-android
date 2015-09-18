package org.stepic.droid.web;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.stepic.droid.base.MainApplication;
import org.stepic.droid.configuration.IConfig;
import org.stepic.droid.model.Course;
import org.stepic.droid.util.SharedPreferenceHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Api implements IApi {
    Context mContext;

    @Inject
    public Api(Context context) {
        mContext = context;
        MainApplication.component(context).inject(this);
    }

    @Inject
    IConfig mConfig;

    @Inject
    IHttpManager mHttpManager;

    @Inject
    SharedPreferenceHelper mSharedPreferencesHelper;

    @Override
    public IStepicResponse authWithLoginPassword(String username, String password) {
        Bundle params = new Bundle();
        params.putString("grant_type", mConfig.getGrantType());
        params.putString("username", username);
        params.putString("password", password);

        String url = mConfig.getBaseUrl() + "/oauth2/token/";

        String json = null;
        try {
            json = mHttpManager.post(url, params);
        } catch (IOException i) {

            int ignore = 123456789;
            //ignore
            //Too many follow-up requests: 21 when incorrect user/password
        }

        Gson gson = new GsonBuilder().create();

        return gson.fromJson(json, AuthenticationStepicResponse.class);
    }

    @Override
    public IStepicResponse signUp(String firstName, String secondName, String email, String password) {

        JsonObject innerObject = new JsonObject();
        innerObject.addProperty("first_name", firstName);
        innerObject.addProperty("last_name", secondName);
        innerObject.addProperty("email", email);
        innerObject.addProperty("password", password);

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("user", innerObject);


        String url = mConfig.getBaseUrl() + "/api/users/";
        //todo implement registration

        String json = null;
        try {
            json = mHttpManager.postJson(url, jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int test = 9000;
        return null;
    }

    @Override
    public List<Course> getEnrolledCourses() {

        updateToken();
        Bundle params = new Bundle();
        params.putString("enrolled", "true");

        String url = mConfig.getBaseUrl() + "/api/courses/";

        String json = null;
        try {
            json = mHttpManager.get(url, params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (json == null) return null;
        JsonElement jElement = new JsonParser().parse(json);//bottle neck
        JsonObject jObject = jElement.getAsJsonObject();
        JsonArray jsonArray = jObject.getAsJsonArray("courses");


        Gson gson = new Gson();
        Type listType = new TypeToken<List<Course>>() {
        }.getType();

        return (List<Course>) gson.fromJson(jsonArray.toString(), listType);

    }


    private void updateToken() {
        AuthenticationStepicResponse response = mSharedPreferencesHelper.getAuthResponseFromStore(mContext);
        Bundle params = new Bundle();
        params.putString("grant_type", mConfig.getRefreshGrantType());
        params.putString("refresh_token", response.getRefresh_token());


        String url = mConfig.getBaseUrl() + "/oauth2/token/";

        String json = null;
        try {
            json = mHttpManager.post(url, params);
        } catch (IOException i) {

            int ignore = 123456789;
            //ignore
            //Too many follow-up requests: 21 when incorrect user/password
        }

        Gson gson = new GsonBuilder().create();

        AuthenticationStepicResponse newResp = gson.fromJson(json, AuthenticationStepicResponse.class);

        mSharedPreferencesHelper.storeAuthInfo(mContext, newResp);

    }


}