package com.example.oauth.service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.List;

@Service
public class EventService {
    public List<com.google.api.services.calendar.model.Event>getUserEvents(String accessToken, LocalDate date) throws IOException {

        DateTime startDateTime = new DateTime(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        DateTime endDateTime = new DateTime(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

        System.out.println(startDateTime);
        System.out.println(endDateTime);

        try {
            // Build the Google Calendar service using the access token
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

            Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("WhiffWhaff")
                .build();

            // Retrieve all events from the user's primary calendar
            Events events = service.events()
                .list("primary")
                .setTimeMin(startDateTime)
                .setTimeMax(endDateTime)
                .execute();

            // Get the list of events
            List<com.google.api.services.calendar.model.Event> eventList = events.getItems();

            return eventList;
        } catch (GeneralSecurityException e) {
            // Handle the GeneralSecurityException appropriately (e.g., logging, error handling)
            throw new IOException("Error creating Google Calendar service", e);
        }

    }
}
