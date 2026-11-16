package com.outdoor.server.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String phone;
    private String email;
    private String avatar;
}
