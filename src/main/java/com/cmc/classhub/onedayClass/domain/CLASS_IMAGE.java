package com.cmc.classhub.onedayClass.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CLASS_IMAGE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CLASS_IMAGE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    protected CLASS_IMAGE(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static CLASS_IMAGE of(String imageUrl) {
        return new CLASS_IMAGE(imageUrl);
    }
}