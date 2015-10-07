/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taptech.spoonscore.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tap
 */
@Entity
@Table(name = "restaurant_details_stage")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RestaurantDetailsStage.findAll", query = "SELECT r FROM RestaurantDetailsStage r"),
    @NamedQuery(name = "RestaurantDetailsStage.findById", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.id = :id"),
    @NamedQuery(name = "RestaurantDetailsStage.findByRestaurantId", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.restaurantId = :restaurantId"),
    @NamedQuery(name = "RestaurantDetailsStage.findByFoundBy", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.foundBy = :foundBy"),
    @NamedQuery(name = "RestaurantDetailsStage.findByCompanyName", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.companyName = :companyName"),
    @NamedQuery(name = "RestaurantDetailsStage.findByCompanyPhone", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.companyPhone = :companyPhone"),
    @NamedQuery(name = "RestaurantDetailsStage.findByCompanyGrade", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.companyGrade = :companyGrade"),
    @NamedQuery(name = "RestaurantDetailsStage.findByCompanyScore", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.companyScore = :companyScore"),
    @NamedQuery(name = "RestaurantDetailsStage.findByCompanyAddress", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.companyAddress = :companyAddress"),
    @NamedQuery(name = "RestaurantDetailsStage.findByRating", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.rating = :rating"),
    @NamedQuery(name = "RestaurantDetailsStage.findByRatingCommentsLink", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.ratingCommentsLink = :ratingCommentsLink"),
    @NamedQuery(name = "RestaurantDetailsStage.findByImageUrl", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.imageUrl = :imageUrl"),
    @NamedQuery(name = "RestaurantDetailsStage.findByInspectionReportLink", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.inspectionReportLink = :inspectionReportLink"),
    @NamedQuery(name = "RestaurantDetailsStage.findByInspectionLink", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.inspectionLink = :inspectionLink"),
    @NamedQuery(name = "RestaurantDetailsStage.findByInspectionSearchLink", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.inspectionSearchLink = :inspectionSearchLink"),
    @NamedQuery(name = "RestaurantDetailsStage.findByLastUpdated", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.lastUpdated = :lastUpdated"),
    @NamedQuery(name = "RestaurantDetailsStage.findByStatus", query = "SELECT r FROM RestaurantDetailsStage r WHERE r.status = :status")})
public class RestaurantDetailsStage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "restaurant_id", nullable = false, length = 75)
    private String restaurantId;
    @Basic(optional = false)
    @Column(name = "found_by", nullable = false, length = 20)
    private String foundBy;
    @Basic(optional = false)
    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;
    @Column(name = "company_phone", length = 20)
    private String companyPhone;
    @Column(name = "company_grade")
    private Character companyGrade;
    @Column(name = "company_score")
    private Integer companyScore;
    @Column(name = "company_address", length = 150)
    private String companyAddress;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 8, scale = 8)
    private Float rating;
    @Column(name = "rating_comments_link", length = 150)
    private String ratingCommentsLink;
    @Column(name = "image_url", length = 100)
    private String imageUrl;
    @Column(name = "inspection_report_link", length = 150)
    private String inspectionReportLink;
    @Column(name = "inspection_link", length = 150)
    private String inspectionLink;
    @Column(name = "inspection_search_link", length = 250)
    private String inspectionSearchLink;
    @Basic(optional = false)
    @Column(name = "last_updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    private Integer status;
    @Basic(optional = false)
    @Column(name = "found_by_link", nullable = false, length = 250)
    private String foundByLink;
    @Basic(optional = false)
    @Column(name = "company_name_alt", nullable = false, length = 100)
    private String altCompanyName;
    public RestaurantDetailsStage() {
    }

    public RestaurantDetailsStage(Integer id) {
        this.id = id;
    }

    public RestaurantDetailsStage(Integer id, String restaurantId, String foundBy, String companyName, Date lastUpdated) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.foundBy = foundBy;
        this.companyName = companyName;
        this.lastUpdated = lastUpdated;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getFoundBy() {
        return foundBy;
    }

    public void setFoundBy(String foundBy) {
        this.foundBy = foundBy;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public Character getCompanyGrade() {
        return companyGrade;
    }

    public void setCompanyGrade(Character companyGrade) {
        this.companyGrade = companyGrade;
    }

    public Integer getCompanyScore() {
        return companyScore;
    }

    public void setCompanyScore(Integer companyScore) {
        this.companyScore = companyScore;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getInspectionReportLink() {
        return inspectionReportLink;
    }

    public void setInspectionReportLink(String inspectionReportLink) {
        this.inspectionReportLink = inspectionReportLink;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RestaurantDetailsStage{");
        sb.append("id=").append(id);
        sb.append(", restaurantId='").append(restaurantId).append('\'');
        sb.append(", foundBy='").append(foundBy).append('\'');
        sb.append(", companyName='").append(companyName).append('\'');
        sb.append(", companyPhone='").append(companyPhone).append('\'');
        sb.append(", companyGrade=").append(companyGrade);
        sb.append(", companyScore=").append(companyScore);
        sb.append(", companyAddress='").append(companyAddress).append('\'');
        sb.append(", rating=").append(rating);
        sb.append(", ratingCommentsLink='").append(ratingCommentsLink).append('\'');
        sb.append(", imageUrl='").append(imageUrl).append('\'');
        sb.append(", inspectionReportLink='").append(inspectionReportLink).append('\'');
        sb.append(", inspectionLink='").append(inspectionLink).append('\'');
        sb.append(", inspectionSearchLink='").append(inspectionSearchLink).append('\'');
        sb.append(", lastUpdated=").append(lastUpdated);
        sb.append(", status=").append(status);
        sb.append(", foundByLink='").append(foundByLink).append('\'');
        sb.append(", altCompanyName='").append(altCompanyName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getAltCompanyName() {
        return altCompanyName;
    }

    public void setAltCompanyName(String altCompanyName) {
        this.altCompanyName = altCompanyName;
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
        if (!(object instanceof RestaurantDetailsStage)) {
            return false;
        }
        RestaurantDetailsStage other = (RestaurantDetailsStage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
