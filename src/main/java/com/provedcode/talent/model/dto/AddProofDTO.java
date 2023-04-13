package com.provedcode.talent.model.dto;

import org.hibernate.validator.constraints.URL;

public record AddProofDTO(
        @URL
        String link,
        String text
) {
}
