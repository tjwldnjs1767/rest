package rest.rest.screenFilter.application;

import android.app.Application;

public class SaveLightValue extends Application {
    public float lightValue;

    public float getLightValue() {
        return lightValue;
    }

    public void setLightValue(float lightValue) {
        this.lightValue = lightValue;
    }
}
