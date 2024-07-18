package ru.practicum.events.mapper;

import ru.practicum.events.model.Location;
import ru.practicum.events.model.dto.LocationDto;

public class LocationMapper {

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lon(locationDto.getLon())
                .lat(locationDto.getLat())
                .build();
    }
}
