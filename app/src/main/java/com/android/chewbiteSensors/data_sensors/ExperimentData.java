package com.android.chewbiteSensors.data_sensors;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.chewbiteSensors.settings.GetSettings;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExperimentData implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;
    private static final String DATE_FORMAT = "yyyy-MM-dd-HH:mm:ss";
    private static final String DATE_FORMAT_FOLDER = "yyyy-MM-dd_HH-mm-ss";

    private String timestamp;
    private Date startDate;
    private Date endDate;
    private int batteryAtStart;
    private int batteryAtEnd;
    private int batteryCapacity;
    private int fileNumber;
    private String experimentName;
    private String timestampFolder;


    @SuppressLint("SimpleDateFormat")
    public ExperimentData(Context context) {
        this.startDate = new Date();
        this.timestamp = new SimpleDateFormat(DATE_FORMAT).format(this.getStartDate());
        this.timestampFolder = new SimpleDateFormat(DATE_FORMAT_FOLDER).format(this.getStartDate());
        this.fileNumber = 1;
        this.experimentName = GetSettings.getExperimentName(context);
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getTimestampFolder() {
        return this.timestampFolder;
    }

    public String getExperimentName() {
        return this.experimentName;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getBatteryAtStart() {
        return batteryAtStart;
    }

    public void setBatteryAtStart(int batteryAtStart) {
        this.batteryAtStart = batteryAtStart;
    }

    public int getBatteryAtEnd() {
        return batteryAtEnd;
    }

    public void setBatteryAtEnd(int batteryAtEnd) {
        this.batteryAtEnd = batteryAtEnd;
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(int batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public int getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
    }

}
