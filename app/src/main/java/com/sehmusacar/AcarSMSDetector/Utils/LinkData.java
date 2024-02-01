package com.sehmusacar.AcarSMSDetector.Utils;

public class LinkData {

    private String links;
    private int start,end;

    public void setLinks(String links) {
        this.links = links;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getLinks() {
        return links;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

}
