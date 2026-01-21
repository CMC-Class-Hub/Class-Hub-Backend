package com.cmc.classhub.reservation.port;

public interface OnedayClassReader {
    Optional<OnedayClass> getOnedayClass(Long onedayClassId);
}
