package com.iiui.craapp.model;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by hknia on 2/7/2018.
 */

public class CrimeModelClass {
    public int crimeID ;

    public String crimeDescription ;

    public String crimeType ;

    public int getCrimeID() {
        return crimeID;
    }
    public Date crimeDateTime ;

    public int locID ;

    public String locName ;

    public BigDecimal locLattitude ;

    public BigDecimal locLongitude ;

    public String locationAddress ;

    public Double crimetable_crimeID;





    public void setCrimeID(int crimeID) {
        this.crimeID = crimeID;
    }

    public String getCrimeDescription() {
        return crimeDescription;
    }

    public void setCrimeDescription(String crimeDescription) {
        this.crimeDescription = crimeDescription;
    }

    public String getCrimeType() {
        return crimeType;
    }

    public void setCrimeType(String crimeType) {
        this.crimeType = crimeType;
    }

    public Date getCrimeDateTime() {
        return crimeDateTime;
    }

    public void setCrimeDateTime(Date crimeDateTime) {
        this.crimeDateTime = crimeDateTime;
    }

    public int getLocID() {
        return locID;
    }

    public void setLocID(int locID) {
        this.locID = locID;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public BigDecimal getLocLattitude() {
        return locLattitude;
    }

    public void setLocLattitude(BigDecimal locLattitude) {
        this.locLattitude = locLattitude;
    }

    public BigDecimal getLocLongitude() {
        return locLongitude;
    }

    public void setLocLongitude(BigDecimal locLongitude) {
        this.locLongitude = locLongitude;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public Double getCrimetable_crimeID() {
        return crimetable_crimeID;
    }

    public void setCrimetable_crimeID(Double crimetable_crimeID) {
        this.crimetable_crimeID = crimetable_crimeID;
    }



}
