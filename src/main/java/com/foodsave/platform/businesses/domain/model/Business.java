package com.foodsave.platform.businesses.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Embedded;

@Entity
@Table(name = "businesses")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "owner_account_id", nullable = false)
    private Long ownerAccountId;
    @Column(nullable = false, length = 255)
    private String name;
    @Column(nullable = false, length = 255)
    private String address;
    @Column(nullable = false, length = 100)
    private String district;
    @Embedded
    private BusinessLocation location;
    @Column(name = "contact_phone", nullable = false, length = 30)
    private String contactPhone;
    @Column(name = "pickup_conditions", nullable = false, columnDefinition = "text")
    private String pickupConditions;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    @Column(name = "plus_partner", nullable = false)
    private boolean plusPartner;

    protected Business() {}

    public Business(Long ownerAccountId, String name, String address, String district,
                    double latitude, double longitude, String contactPhone,
                    String pickupConditions, boolean plusPartner) {
        this.ownerAccountId = ownerAccountId;
        this.name = name;
        this.address = address;
        this.district = district;
        this.location = new BusinessLocation(latitude, longitude);
        this.contactPhone = contactPhone;
        this.pickupConditions = pickupConditions;
        this.plusPartner = plusPartner;
    }

    public Long getId() { return id; }
    public Long getOwnerAccountId() { return ownerAccountId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getDistrict() { return district; }
    public double getLatitude() { return location.getLatitude(); }
    public double getLongitude() { return location.getLongitude(); }
    public String getContactPhone() { return contactPhone; }
    public String getPickupConditions() { return pickupConditions; }
    public boolean isActive() { return active; }
    public boolean isPlusPartner() { return plusPartner; }

    public void updateProfile(String name, String address, String district, double latitude, double longitude,
                              String contactPhone, String pickupConditions) {
        if (name == null || name.isBlank() || address == null || address.isBlank() || district == null || district.isBlank())
            throw new IllegalArgumentException("business.profile.required");
        this.name = name.trim(); this.address = address.trim(); this.district = district.trim();
        this.location = new BusinessLocation(latitude, longitude);
        this.contactPhone = contactPhone == null ? "" : contactPhone.trim();
        this.pickupConditions = pickupConditions == null ? "" : pickupConditions.trim();
    }
}
