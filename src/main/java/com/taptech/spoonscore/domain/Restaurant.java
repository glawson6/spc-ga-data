package com.taptech.spoonscore.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by tap on 3/26/15.
 */
public class Restaurant  implements Serializable {
    private String companyPhone;
    private String companyName;
    private String companyAddress;
    private String companyInspectionGrade;
    private String companyInspectionScore;
    private Float rating;
    private String ratingCommentsLink;
    private String inspectionLink;
    private String foundBy;
    private String restaurantID;
    private String inspectionSearchLink;
    private String imageURL;
    private Integer inspectionReportTries = 0;
    private Boolean foundReport = false;
    private String viewReportLink;
    private Date lastUpdated;
    private Integer status;
    private String foundByLink;
    private String alternateCompanyName;

    private Location location = new Location();

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyInspectionGrade() {
        return companyInspectionGrade;
    }

    public void setCompanyInspectionGrade(String companyInspectionGrade) {
        this.companyInspectionGrade = companyInspectionGrade;
    }

    public String getCompanyInspectionScore() {
        return companyInspectionScore;
    }

    public void setCompanyInspectionScore(String companyInspectionScore) {
        this.companyInspectionScore = companyInspectionScore;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getRatingCommentsLink() {
        return ratingCommentsLink;
    }

    public void setRatingCommentsLink(String ratingCommentsLink) {
        this.ratingCommentsLink = ratingCommentsLink;
    }

    public String getInspectionLink() {
        return inspectionLink;
    }

    public void setInspectionLink(String inspectionLink) {
        this.inspectionLink = inspectionLink;
    }
    public String getInspectionSearchLink() {
        return inspectionSearchLink;
    }

    public void setInspectionSearchLink(String inspectionSearchLink) {
        this.inspectionSearchLink = inspectionSearchLink;
    }

    public String getFoundBy() {
        return foundBy;
    }

    public void setFoundBy(String foundBy) {
        this.foundBy = foundBy;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public Integer getInspectionReportTries() {
        return inspectionReportTries;
    }

    public void setInspectionReportTries(Integer inspectionReportTries) {
        this.inspectionReportTries = inspectionReportTries;
    }

    public Boolean getFoundReport() {
        return foundReport;
    }

    public void setFoundReport(Boolean foundReport) {
        this.foundReport = foundReport;
    }

    public String getViewReportLink() {
        return viewReportLink;
    }

    public void setViewReportLink(String viewReportLink) {
        this.viewReportLink = viewReportLink;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFoundByLink() {
        return foundByLink;
    }

    public void setFoundByLink(String foundByLink) {
        this.foundByLink = foundByLink;
    }

    public String getAlternateCompanyName() {
        return alternateCompanyName;
    }

    public void setAlternateCompanyName(String alternateCompanyName) {
        this.alternateCompanyName = alternateCompanyName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Restaurant{");
        sb.append("companyPhone='").append(companyPhone).append('\'');
        sb.append(", companyName='").append(companyName).append('\'');
        sb.append(", companyAddress='").append(companyAddress).append('\'');
        sb.append(", companyInspectionGrade='").append(companyInspectionGrade).append('\'');
        sb.append(", companyInspectionScore='").append(companyInspectionScore).append('\'');
        sb.append(", rating=").append(rating);
        sb.append(", ratingCommentsLink='").append(ratingCommentsLink).append('\'');
        sb.append(", inspectionLink='").append(inspectionLink).append('\'');
        sb.append(", foundBy='").append(foundBy).append('\'');
        sb.append(", restaurantID='").append(restaurantID).append('\'');
        sb.append(", inspectionSearchLink='").append(inspectionSearchLink).append('\'');
        sb.append(", imageURL='").append(imageURL).append('\'');
        sb.append(", inspectionReportTries=").append(inspectionReportTries);
        sb.append(", foundReport=").append(foundReport);
        sb.append(", viewReportLink='").append(viewReportLink).append('\'');
        sb.append(", lastUpdated=").append(lastUpdated);
        sb.append(", status=").append(status);
        sb.append(", foundByLink='").append(foundByLink).append('\'');
        sb.append(", alternateCompanyName='").append(alternateCompanyName).append('\'');
        sb.append(", location=").append(location);
        sb.append('}');
        return sb.toString();
    }
}
