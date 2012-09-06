package org.ei.drishti.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.UUID.randomUUID;

@Component
public class IdGenerator {
    public UUID generateUUID() {
        return randomUUID();
    }

}
