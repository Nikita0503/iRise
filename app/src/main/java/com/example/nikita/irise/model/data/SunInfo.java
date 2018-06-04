
package com.example.nikita.irise.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SunInfo {

    @SerializedName("results")
    @Expose
    private Results results;

    public Results getResults() {
        return results;
    }

}
