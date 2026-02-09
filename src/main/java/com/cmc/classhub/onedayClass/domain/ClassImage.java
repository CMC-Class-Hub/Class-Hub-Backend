package com.cmc.classhub.onedayClass.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "class_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClassImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    protected ClassImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static ClassImage of(String imageUrl) {
        return new ClassImage(imageUrl);
    }
}