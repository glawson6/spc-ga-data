package com.taptech.spoonscore.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author tap
 */
@Entity
@Table(name = "ZIP_CODES")
@XmlRootElement
public class ZipCodes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(nullable = false, length = 2)
    private String country;
    @Basic(optional = false)
    @NotNull
    @Column(name = "zip_code", nullable = false)
    private int zipCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(nullable = false, length = 30)
    private String city;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(nullable = false, length = 30)
    private String state;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "state_abbrev", nullable = false, length = 2)
    private String stateAbbrev;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(nullable = false, length = 30)
    private String county;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "county_code", nullable = false, length = 5)
    private String countyCode;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private float latitude;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private float longitude;

    public ZipCodes() {
    }

    public ZipCodes(Integer id) {
        this.id = id;
    }

    public ZipCodes(Integer id, String country, int zipCode, String city, String state, String stateAbbrev, String county, String countyCode, float latitude, float longitude) {
        this.id = id;
        this.country = country;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.stateAbbrev = stateAbbrev;
        this.county = county;
        this.countyCode = countyCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateAbbrev() {
        return stateAbbrev;
    }

    public void setStateAbbrev(String stateAbbrev) {
        this.stateAbbrev = stateAbbrev;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZipCodes)) {
            return false;
        }
        ZipCodes other = (ZipCodes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ZipCodes{");
        sb.append("id=").append(id);
        sb.append(", country='").append(country).append('\'');
        sb.append(", zipCode=").append(zipCode);
        sb.append(", city='").append(city).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", stateAbbrev='").append(stateAbbrev).append('\'');
        sb.append(", county='").append(county).append('\'');
        sb.append(", countyCode='").append(countyCode).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }
}
