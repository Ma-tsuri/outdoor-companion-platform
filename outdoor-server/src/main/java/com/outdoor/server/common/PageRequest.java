package com.outdoor.server.common;

import lombok.Data;

@Data
public class PageRequest {
    private Long page = 1L;
    private Long size = 10L;
}
