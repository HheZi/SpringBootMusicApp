package com.app.kafka.message;

import java.util.UUID;

public record ImageDeletionMessage(UUID imageName) {

}
