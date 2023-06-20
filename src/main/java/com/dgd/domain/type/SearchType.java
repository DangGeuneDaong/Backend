package com.dgd.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchType {
    PARCEL("parcel"),
    ROAD("road");
    private final String value;
}
