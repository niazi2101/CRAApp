package com.iiui.craapp.model;

/**
 * Created by hknia on 2/14/2018.
 */

public class ReportModelClass {

    private String ReportDesc;
    private String NumOfVictoms = "0";
    private String NumOfWitness = "0";
    private String Suspects = "0";
    private String ReportSubmitTime;


    public String getReportDesc() {
        return ReportDesc;
    }

    public void setReportDesc(String reportDesc) {
        ReportDesc = reportDesc;
    }

    public String getNumOfVictoms() {
        return NumOfVictoms;
    }

    public void setNumOfVictoms(String numOfVictoms) {
        NumOfVictoms = numOfVictoms;
    }

    public String getNumOfWitness() {
        return NumOfWitness;
    }

    public void setNumOfWitness(String numOfWitness) {
        NumOfWitness = numOfWitness;
    }

    public String getSuspects() {
        return Suspects;
    }

    public void setSuspects(String suspects) {
        Suspects = suspects;
    }

    public String getReportSubmitTime() {
        return ReportSubmitTime;
    }

    public void setReportSubmitTime(String reportSubmitTime) {
        ReportSubmitTime = reportSubmitTime;
    }







}
