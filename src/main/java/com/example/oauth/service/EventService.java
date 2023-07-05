package com.example.oauth.service;

import com.example.oauth.model.EventDetails;
import com.example.oauth.model.GenericResponse;
import com.example.oauth.model.Match;
import com.example.oauth.model.RequestStatus;
import com.example.oauth.model.User;
import com.example.oauth.repository.MatchRepository;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    MatchRepository matchRepository;

    public Boolean sendCalendarInvite(String accessToken, User user1, User user2, LocalDateTime eventTime) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);


        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName("WhiffWhaff")
            .build();

        // Create the event
        Event event = new Event()
            .setSummary("🏓" + user1.getName() + " Invited you to play Whiff Whaff!");

        List<EventAttendee> attendees = new ArrayList<>();

        EventAttendee attendee1 = new EventAttendee()
            .setEmail(user1.getEmail());
        attendees.add(attendee1);

        EventAttendee attendee2 = new EventAttendee()
            .setEmail(user2.getEmail());
        attendees.add(attendee2);

        EventAttendee whiffWhaffCalendar = new EventAttendee()
            .setEmail("service_whiffwhaff@xdesign.com");
        attendees.add(attendee2);

        event.setAttendees(attendees);


        ZoneId zoneId = ZoneId.systemDefault();

        Date startD = Date.from(eventTime.atZone(zoneId).toInstant());
        DateTime googleStart = new DateTime(startD);


        EventDateTime start = new EventDateTime()
            .setDateTime(googleStart)
            .setTimeZone("Europe/London");
        event.setStart(start);

        LocalDateTime endTime = eventTime.plusMinutes(30);

        Date endD = Date.from(endTime.atZone(zoneId).toInstant());

         DateTime googleEnd = new DateTime(endD);

        EventDateTime end = new EventDateTime()
            .setDateTime(googleEnd)
            .setTimeZone("Europe/London");

        event.setEnd(end);

        // Insert the event
        try {
            service.events().insert("primary", event).execute();

            return true;
        } catch (GoogleJsonResponseException e) {
           return false;
        }
    }


    public GenericResponse checkEvent(String accessToken, String email, String challengedEmail, long eventId) throws IOException {

        Match match = matchRepository.getMatchById(eventId);

        Date convertableDate = Date.from(match.getMatchDateTime().atZone(ZoneId.systemDefault()).toInstant());
        Date convertableEndDate = Date.from(match.getMatchDateTime().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());

        try {

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

            Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("WhiffWhaff")
                .build();


            Events events = service.events()
                .list(email)
                .setTimeMin(new DateTime(convertableDate))
                .setTimeMax(new DateTime(convertableEndDate))
                .execute();

            List<Event> eventList = events.getItems();




            for (Event event : eventList){

                final List<EventAttendee> users = event.getAttendees();

                for ( EventAttendee user : users){
                   if( user.getEmail() == challengedEmail && user.getResponseStatus().equals("accepted")){


                    match.setResponse(RequestStatus.ACCEPTED);
                    matchRepository.save(match);
                   }
                }

               if( event.getAttendees().stream().anyMatch(eventAttendee -> eventAttendee.getResponseStatus().equals("declined"))) {
                   matchRepository.deleteById(eventId);
                   return new GenericResponse(" DELETED");
               }

            }

            // FIGURE OUT A BETTER RETURN FOR THIS AND ALSO TEST

                return new GenericResponse(" nothing updated");
        } catch (GeneralSecurityException e) {

            throw new IOException("Error creating Google Calendar service", e);
        }

    }



    public List<EventDetails>getEvents(String accessToken, LocalDate date, String email) throws IOException {

        DateTime startDateTime = new DateTime(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        DateTime endDateTime = new DateTime(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());


        try {

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

            Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("WhiffWhaff")
                .build();


            Events events = service.events()
                .list(email)
                .setTimeMin(startDateTime)
                .setTimeMax(endDateTime)
                .execute();



            final var filteredEvents = filterEvents(events);


            return filteredEvents;
        } catch (GeneralSecurityException e) {

            throw new IOException("Error creating Google Calendar service", e);
        }

    }

    private List<EventDetails>filterEvents(Events events) {
        List<Event> eventList = events.getItems();

        Set<String> eventIds = new HashSet<>();
        Set<String> eventSummaries = new HashSet<>();
        List<EventDetails> filteredEventList = new ArrayList<>();

        for (Event event : eventList) {
            if (!eventIds.contains(event.getId()) && !"cancelled".equals(event.getStatus()) && !eventSummaries.contains(event.getSummary())) {


                System.out.println(event);
                System.out.println("bawbag");


                Instant instant = Instant.ofEpochMilli(event.getStart().getDateTime().getValue());
                LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of(event.getStart().getTimeZone()));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String formattedTime = dateTime.format(formatter);

                EventDetails details = new EventDetails();

                details.setTitle(event.getSummary());
                details.setTime(formattedTime);

                filteredEventList.add(details);
                eventIds.add(event.getId());
                eventSummaries.add(event.getSummary());



            }


        }
        Collections.sort(filteredEventList, Comparator.comparing(EventDetails::getTime));

        return filteredEventList;
    }

}
