package com.cmc.classhub.reservation.port;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import java.util.Optional;

public interface OnedayClassReader {
    Optional<OnedayClass> getOnedayClass(Long onedayClassId);
}
