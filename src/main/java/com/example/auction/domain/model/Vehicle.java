package com.example.auction.domain.model;

import java.time.LocalDate;

public record Vehicle(
    String make, String model, String color,
    LocalDate auctionDate, String auctioneer,
    DamageClassification damageClassification
) {}
