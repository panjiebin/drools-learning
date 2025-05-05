package com.pan.drools.metric;

/**
 * @author panjb
 */
public class AccessLog {
    private String domain;
    private String ip;
    private String ja3;
    private String url;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getJa3() {
        return ja3;
    }

    public void setJa3(String ja3) {
        this.ja3 = ja3;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
