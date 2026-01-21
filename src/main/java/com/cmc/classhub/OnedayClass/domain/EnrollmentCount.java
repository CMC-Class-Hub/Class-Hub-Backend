package com.cmc.classhub.OnedayClass.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentCount {
    private int currentNum = 0;
    private int capacity;

    public EnrollmentCount(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("정원은 1명 이상이어야 합니다.");
        this.capacity = capacity;
    }

    private EnrollmentCount(int currentNum, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("정원은 1명 이상이어야 합니다.");
        }
        if (currentNum < 0) {
            throw new IllegalArgumentException("참여 인원은 0명 이상이어야 합니다.");
        }
        if (currentNum > capacity) {
            throw new IllegalArgumentException("참여 인원이 정원을 초과할 수 없습니다.");
        }
        this.currentNum = currentNum;
        this.capacity = capacity;
    }

    public EnrollmentCount increase() {
        if (currentNum >= capacity) {
            throw new IllegalStateException("정원이 초과되었습니다.");
        }
        return new EnrollmentCount(this.currentNum + 1, this.capacity);
    }

    public EnrollmentCount decrease() {
        if (currentNum <= 0) {
            throw new IllegalStateException("참여 인원은 0명 미만이 될 수 없습니다.");
        }
        return new EnrollmentCount(this.currentNum - 1, this.capacity);
    }

    public EnrollmentCount updateCapacity(int newCapacity) {
        if (newCapacity < currentNum) {
            throw new IllegalArgumentException("현재 참여 인원보다 적은 정원으로 수정할 수 없습니다.");
        }
        return new EnrollmentCount(currentNum, newCapacity);
    }

    public boolean isFull() {
        return this.currentNum >= this.capacity;
    }

}