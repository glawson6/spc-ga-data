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
@Table(name = "restaurant_details")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RestaurantDetails.findAll", query = "SELECT r FROM RestaurantDetails r"),
    @NamedQuery(name = "RestaurantDetails.findById", query = "SELECT r FROM RestaurantDetails r WHERE r.id = :id"),
    @NamedQuery(name = "RestaurantDetails.findByRestaurantId", query = "SELECT r FROM RestaurantDetails r WHERE r.restaurantId = :restaurantId"),
    @NamedQuery(name = "RestaurantDetails.findByFoundBy", query = "SELECT r FROM RestaurantDetails r WHERE r.foundBy = :foundBy"),
    @NamedQuery(name = "RestaurantDetails.findByCompanyName", query = "SELECT r FROM RestaurantDetails r WHERE r.companyName = :companyName"),
    @NamedQuery(name = "RestaurantDetails.findByCompanyPhone", query = "SELECT r FROM RestaurantDetails r WHERE r.companyPhone = :companyPhone"),
    @NamedQuery(name = "RestaurantDetails.findByCompanyGrade", query = "SELECT r FROM RestaurantDetails r WHERE r.companyGrade = :companyGrade"),
    @NamedQuery(name = "RestaurantDetails.findByCompanyScore", query = "SELECT r FROM RestaurantDetails r WHERE r.companyScore = :companyScore"),
    @NamedQuery(name = "RestaurantDetails.findByCompanyAddress", query = "SELECT r FROM RestaurantDetails r WHERE r.companyAddress = :companyAddress"),
    @NamedQuery(name = "RestaurantDetails.findByRating", query = "SELECT r FROM RestaurantDetails r WHERE r.rating = :rating"),
    @NamedQuery(name = "RestaurantDetails.findByRatingCommentsLink", query = "SELECT r FROM RestaurantDetails r WHERE r.ratingCommentsLink = :ratingCommentsLink"),
    @NamedQuery(name = "RestaurantDetails.findByImageUrl", query = "SELECT r FROM RestaurantDetails r WHERE r.imageUrl = :imageUrl"),
    @NamedQuery(name = "RestaurantDetails.findByInspectionReportLink", query = "SELECT r FROM RestaurantDetails r WHERE r.inspectionReportLink = :inspectionReportLink"),
    @NamedQuery(name = "RestaurantDetails.findByInspectionLink", query = "SELECT r FROM RestaurantDetails r WHERE r.inspectionLink = :inspectionLink"),
    @NamedQuery(name = "RestaurantDetails.findByInspectionSearchLink", query = "SELECT r FROM RestaurantDetails r WHERE r.inspectionSearchLink = :inspectionSearchLink"),
    @NamedQuery(name = "RestaurantDetails.findByLastUpdated", query = "SELECT r FROM RestaurantDetails r WHERE r.lastUpdated = :lastUpdated"),
    @NamedQuery(name = "RestaurantDetails.findByStatus", query = "SELECT r FROM RestaurantDetails r WHERE r.status = :status")})
public class RestaurantDetails implements Serializable {
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

    @Column(name = "company_name_alt", nullable = false, length = 100)
    private String altCompanyName;

    public RestaurantDetails() {
    }

    public RestaurantDetails(Integer id) {
        this.id = id;
    }

    public RestaurantDetails(Integer id, String restaurantId, String foundBy, String companyName, Date lastUpdated) {
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
        if (!(object instanceof RestaurantDetails)) {
            return false;
        }
        RestaurantDetails other = (RestaurantDetails) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RestaurantDetails{");
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
        sb.append(", altCompanyName='").append(altCompanyName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
