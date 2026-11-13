package com.foodsave.platform.businesses.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BusinessLocation {
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;

    protected BusinessLocation() {}

    public BusinessLocation(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("business.location.invalid");
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
