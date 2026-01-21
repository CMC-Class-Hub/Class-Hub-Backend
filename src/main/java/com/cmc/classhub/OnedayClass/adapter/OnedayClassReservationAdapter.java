package com.cmc.classhub.OnedayClass.adapter;

import com.cmc.classhub.OnedayClass.domain.OnedayClass;
import com.cmc.classhub.OnedayClass.service.OnedayClassReaderService;
import com.cmc.classhub.reservation.port.OnedayClassReader;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnedayClassReservationAdapter implements OnedayClassReader {

    private final OnedayClassReaderService onedayClassReaderService;

    @Override
    public Optional<OnedayClass> getOnedayClass(Long onedayClassId) {
        return onedayClassReaderService.findById(onedayClassId);
    }
}
