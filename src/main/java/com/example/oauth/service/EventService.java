package com.example.oauth.service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Sets;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventService {
    public List<com.google.api.services.calendar.model.Event>getPrimaryUserEvents(String accessToken, LocalDate date) throws IOException {

        DateTime startDateTime = new DateTime(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        DateTime endDateTime = new DateTime(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

        System.out.println(startDateTime);
        System.out.println(endDateTime);

        try {

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

            Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("WhiffWhaff")
                .build();



            Events opponentEvents = service.events()
                .list("francesco.lamarca@xdesign.com")
                .setTimeMin(startDateTime)
                .setTimeMax(endDateTime)
                .execute();


            Events events = service.events()
                .list("primary")
                .setTimeMin(startDateTime)
                .setTimeMax(endDateTime)
                .execute();


            List<com.google.api.services.calendar.model.Event> eventList = events.getItems();
            List<com.google.api.services.calendar.model.Event> opponentEventList = opponentEvents.getItems();

            List<Event> allEvents = new ArrayList<Event>(eventList);
            allEvents.addAll(opponentEventList);


            return allEvents;
        } catch (GeneralSecurityException e) {

            throw new IOException("Error creating Google Calendar service", e);
        }

    }
}
