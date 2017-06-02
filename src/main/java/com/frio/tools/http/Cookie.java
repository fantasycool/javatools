package com.frio.tools.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cookie {
    private static Logger LOGGER = LoggerFactory.getLogger(Cookie.class.getSimpleName());
    private String name;
    private String value;
    private int maxAge = -1;
    private String domain;
    private String path = "/";
    private boolean secure;
    private boolean httpOnly;
    private int version;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Cookie(String name, String value, boolean httpOnly) {
        this.name = name;
        this.value = value;
        this.httpOnly = httpOnly;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setHttpOnly() {
        this.httpOnly = true;
    }

    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public String toString() {
        return "Cookie{name=\'" + this.name + '\'' + ", value=\'" + this.value + '\'' + ", maxAge=" + this.maxAge + ", domain=\'" + this.domain + '\'' + ", path=\'" + this.path + '\'' + ", secure=" + this.secure + ", version=" + this.version + '}';
    }
}
