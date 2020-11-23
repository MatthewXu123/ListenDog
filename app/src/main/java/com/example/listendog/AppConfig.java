package com.example.listendog;

public class AppConfig {

    public static final AppConfig INSTANCE = new AppConfig();

    private String callNumber;

    private String requiredNumberGroup;

    private int defaultSim;

    private int checkPeriod;

    private int numberMissThreshold;

    private int runDuration;

    private AppConfig(){

    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getRequiredNumberGroup() {
        return requiredNumberGroup;
    }

    public void setRequiredNumberGroup(String requiredNumberGroup) {
        this.requiredNumberGroup = requiredNumberGroup;
    }

    public int getDefaultSim() {
        return defaultSim;
    }

    public void setDefaultSim(int defaultSim) {
        this.defaultSim = defaultSim;
    }

    public int getCheckPeriod() {
        return checkPeriod;
    }

    public void setCheckPeriod(int checkPeriod) {
        this.checkPeriod = checkPeriod;
    }

    public int getNumberMissThreshold() {
        return numberMissThreshold;
    }

    public void setNumberMissThreshold(int numberMissThreshold) {
        this.numberMissThreshold = numberMissThreshold;
    }

    public int getRunDuration() {
        return runDuration;
    }

    public void setRunDuration(int runDuration) {
        this.runDuration = runDuration;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "callNumber='" + callNumber + '\'' +
                ", requiredNumberGroup='" + requiredNumberGroup + '\'' +
                ", defaultSim=" + defaultSim +
                ", checkPeriod=" + checkPeriod +
                ", numberMissThreshold=" + numberMissThreshold +
                ", runDuration=" + runDuration +
                '}';
    }
}
