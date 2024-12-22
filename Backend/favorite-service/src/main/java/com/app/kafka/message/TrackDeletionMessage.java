package com.app.kafka.message;

public record TrackDeletionMessage(Long trackId, String audioName) {}
