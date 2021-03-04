package com.example.fitastic;

public class RunHistData {

    private String dateHist;
    private String distanceHist;
    private String paceHist;
    private String timeHist;



    RunHistData(){

    }

    public RunHistData(String dateHist, String distanceHist, String paceHist, String timeHist) {
        this.dateHist = dateHist;
        this.distanceHist = distanceHist;
        this.paceHist = paceHist;
        this.timeHist = timeHist;
    }

    public String getDateHist() {
        return dateHist;
    }

    public void setDateHist(String dateHist) {
        this.dateHist = dateHist;
    }

    public String getDistanceHist() {
        return distanceHist;
    }

    public void setDistanceHist(String distanceHist) {
        this.distanceHist = distanceHist;
    }

    public String getPaceHist() {
        return paceHist;
    }

    public void setPaceHist(String paceHist) {
        this.paceHist = paceHist;
    }

    public String getTimeHist() {
        return timeHist;
    }

    public void setTimeHist(String timeHist) {
        this.timeHist = timeHist;
    }
}
