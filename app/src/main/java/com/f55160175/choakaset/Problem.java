package com.f55160175.choakaset;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 10/31/2015.
 */
public class Problem {
    private String id;
    private String detail;
    private String date;
    private String status;
    private String topic;
    private String type;
    private String pbm_crop_id;

    public Problem(JSONObject m) throws JSONException {
        this.id = m.getString("tp_id");
        this.detail = m.getString("tp_detail");
        this.date = m.getString("tp_createdate");
        this.status = m.getString("tps_name");
        this.topic = m.getString("tp_title");
        this.type = m.getString("tpt_name");
        //this.pbm_crop_id = m.getString("pbm_crop_id");
    }

    public String getId() {
        return this.id;
    }

    public String getDeatil() {
        return this.detail;
    }

    public String getDate() {
        return this.date;
    }

    public String getStatus() {
        return this.status;
    }

    public String getTopic() {return this.topic;}

    public String getType() {return this.type;}

    /*public String getCropId() {
        return this.pbm_crop_id;
    }*/
}
