package com.android.chewbiteSensors.deviceStatus;

public class DeviceStatus {
    private Status airplane;
    private Status bluetooth;
    private Status wifi;
    private Status silence;

    public DeviceStatus(){}

    public DeviceStatus(Status airplane, Status bluetooth, Status wifi, Status silence) {
        this.airplane = airplane;
        this.bluetooth = bluetooth;
        this.wifi = wifi;
        this.silence = silence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceStatus that = (DeviceStatus) o;
        return airplane == that.airplane &&
                bluetooth == that.bluetooth &&
                wifi == that.wifi;
    }

    public Status getAirplane() {
        return airplane;
    }

    public void setAirplane(Status airplane) {
        this.airplane = airplane;
    }

    public Status getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(Status bluetooth) {
        this.bluetooth = bluetooth;
    }

    public Status getWifi() {
        return wifi;
    }

    public void setWifi(Status wifi) {
        this.wifi = wifi;
    }
    public Status getSilence() {
        return silence;
    }
    public void setSilence(Status silence) {
        this.silence = silence;
    }
}
