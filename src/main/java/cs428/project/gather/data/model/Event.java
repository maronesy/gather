package cs428.project.gather.data.model;

import cs428.project.gather.data.*;
import cs428.project.gather.data.repo.*;

import java.util.*;
import java.sql.Timestamp;
import javax.persistence.*;
import org.joda.time.DateTime;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import lombok.Data;

@Data
@Entity
public class Event {
    private static final double ONE_MILE_IN_DEGREES_LATITUDE = 0.014554;
    private static final double ONE_MILE_IN_DEGREES_LONGITUDE = 0.014457;

    private @Id @Column(name="ID") @GeneratedValue Long id;

    private String name;
    private String description;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Location location;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_id")
    private List<Occurrence> occurrences = new ArrayList<Occurrence>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_id")
    private Set<Feedback> feedbacks = new HashSet<Feedback>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_id")
    private Set<ChangeLog> changeLog = new HashSet<ChangeLog>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Registrant> subscribers = new HashSet<Registrant>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Registrant> owners = new HashSet<Registrant>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Registrant> participants = new HashSet<Registrant>();

    @ManyToOne(optional=false)
    private Category category;
    //private String category;

    public Event() {}

    public Event(String name) {
        setName(name);
    }

    // Setters and Getters

    public Long getId() {
        return id;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public boolean addOccurrence(Occurrence occurrence) {
        Assert.notNull(occurrence);
        return this.occurrences.add(occurrence);
    }

    public boolean addFeedback(Feedback feedback) {
        Assert.notNull(feedback);
        return this.feedbacks.add(feedback);
    }

    public boolean addChangeLog(ChangeLog changeLog){
        Assert.notNull(changeLog);
        return this.changeLog.add(changeLog);
    }

    public List<Occurrence> getOccurrences() {
        return Collections.unmodifiableList(occurrences);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        Assert.hasText(description);
        this.description = description;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        Assert.hasText(name);
        this.name = name;
    }

    public Set<Feedback> getFeedbacks() {
        return Collections.unmodifiableSet(feedbacks);
    }

    public Set<ChangeLog> getChangeLog() {
        return Collections.unmodifiableSet(changeLog);
    }

    public Set<Registrant> getParticipants(){
        return Collections.unmodifiableSet(participants);
    }

    public Set<Registrant> getOwners(){
        return Collections.unmodifiableSet(owners);
    }

    public boolean addParticipant(Registrant aUser){
        return participants.add(aUser);
    }

    public boolean addOwner(Registrant anOwner){
        return owners.add(anOwner);
    }
    public Location getLocation() {
        return this.location;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory(){
        return this.category;
    }

    public boolean removeParticipant(Registrant aUser){
        return participants.remove(aUser);
    }

    public boolean removeOwner(Registrant aUser){
        return owners.remove(aUser);
    }

    public boolean removeOccurrence(Occurrence occurrence){
        return occurrences.remove(occurrence);
    }

    public boolean containsOwner(Registrant owner, Errors errors) {
        if (! getOwners().contains(owner)) {
            errors.reject("-9", "Cannot update event. The request Registrant is not the event owner.");
            return false;
        }
        return true;
    }

    public static List<Event> queryForEvents(EventsQueryData queryParams, EventRepository eventRepo) {
        // Calculate the upper and lower latitude bounds.
        double latitudeRadiusAdjustment = ONE_MILE_IN_DEGREES_LATITUDE * queryParams.getRadiusMi();
        Double latitudeLowerBound = new Double(queryParams.getLatitude() - latitudeRadiusAdjustment);
        Double latitudeUpperBound = new Double(queryParams.getLatitude() + latitudeRadiusAdjustment);

        // Calculate the upper and lower longitude bounds.
        double longitudeRadiusAdjustment = ONE_MILE_IN_DEGREES_LONGITUDE * queryParams.getRadiusMi();
        Double longitudeLowerBound = new Double(queryParams.getLongitude() - longitudeRadiusAdjustment);
        Double longitudeUpperBound = new Double(queryParams.getLongitude() + longitudeRadiusAdjustment);

        Timestamp timeWindow = new Timestamp( (queryParams.getHour() == -1) ?
                                                DateTime.now().plusYears(1).getMillis() : // All events this year
                                                DateTime.now().plusHours(queryParams.getHour()).getMillis() ); // Events in the next <queryParams.getHour() > hours

        List<Event> events = eventRepo.findByLocationAndOccurrenceTimeWithin(latitudeLowerBound, latitudeUpperBound,
                                        longitudeLowerBound, longitudeUpperBound, timeWindow);

        // Filter out events by matching categories, only if categories was provided (not empty)
        List<Event> categoryFilteredEvents = events;
        if (! queryParams.getCategories().isEmpty()) {
            categoryFilteredEvents = new ArrayList<Event>();
            for (Event ev : events) {
                if (queryParams.getCategories().contains(ev.getCategory().getName()))
                    categoryFilteredEvents.add(ev);
            }
        }

        return categoryFilteredEvents;
    }

    public static Event buildEventFrom(NewEventData newEventData, Registrant owner, CategoryRepository categoryRepo, Errors errors) {
        Event newEvent = new Event(newEventData.getEventName());
        newEvent.setDescription(newEventData.getEventDescription());
        newEvent.setLocation(new Location(newEventData.getEventCoodinates()));

        if (!newEvent.addParticipant(owner)) {
            String message = "Cannot create event. Failed to add creator as participant.";
            errors.reject("-7", message);
        }

        if (!newEvent.addOwner(owner)) {
            String message = "Cannot create event. Failed to add creator as owner.";
            errors.reject("-7", message);
        }

        Occurrence occurrence = new Occurrence("", new Timestamp(newEventData.getEventTime()));
        if (!newEvent.addOccurrence(occurrence)) {
            String message = "Cannot create event. Failed to add first occurrence to event.";
            errors.reject("-7", message);
        }

        Category category = categoryRepo.findByName(newEventData.getEventCategory()).get(0);
        newEvent.setCategory(category);
        return newEvent;
    }

    public static Event updateEventUsing(UpdateEventData updateEventData, Registrant owner, EventRepository eventRepo, CategoryRepository categoryRepo, Errors errors) {
        Event targetEvent = eventRepo.findOne(updateEventData.getEventId());

        if (! targetEvent.containsOwner(owner,errors)) {
            return targetEvent;
        }

        targetEvent.setName(updateEventData.getEventName());
        targetEvent.setDescription(updateEventData.getEventDescription());
        targetEvent.setLocation(new Location(updateEventData.getEventCoodinates()));
        if(!updateEventData.getOccurrencesToAdd().isEmpty()){
            for(int i=0;i<updateEventData.getOccurrencesToAdd().size();i++){
                if (!targetEvent.addOccurrence(updateEventData.getOccurrencesToAdd().get(i))){
                    String message = "Cannot update event. Failed to add a coccurrence.";
                    errors.reject("-7", message);
                }
            }
        }
        if(!updateEventData.getParticipantsToAdd().isEmpty()){
            for(int i=0;i<updateEventData.getParticipantsToAdd().size();i++){
                if (!targetEvent.addParticipant(updateEventData.getParticipantsToAdd().get(i))){
                    String message = "Cannot update event. Failed to add a participant.";
                    errors.reject("-7", message);
                }
            }
        }
        if(!updateEventData.getOwnersToAdd().isEmpty()){
            for(int i=0;i<updateEventData.getOwnersToAdd().size();i++){
                if (!targetEvent.addOwner(updateEventData.getOwnersToAdd().get(i))){
                    String message = "Cannot update event. Failed to add an owner.";
                    errors.reject("-7", message);
                }
            }
        }

        if(!updateEventData.getOccurrencesToRemove().isEmpty()){
            for(int i=0;i<updateEventData.getOccurrencesToRemove().size();i++){
                if (!targetEvent.removeOccurrence(updateEventData.getOccurrencesToRemove().get(i))){
                    String message = "Cannot update event. Failed to remove a coccurrence.";
                    errors.reject("-8", message);
                }
            }
        }
        if(!updateEventData.getParticipantsToRemove().isEmpty()){
            for(int i=0;i<updateEventData.getParticipantsToRemove().size();i++){
                if (!targetEvent.removeParticipant(updateEventData.getParticipantsToRemove().get(i))){
                    String message = "Cannot update event. Failed to remove a participant.";
                    errors.reject("-8", message);
                }
            }
        }
        if(!updateEventData.getOwnersToRemove().isEmpty()){
            for(int i=0;i<updateEventData.getOwnersToRemove().size();i++){
                if (!targetEvent.removeOwner(updateEventData.getOwnersToRemove().get(i))){
                    String message = "Cannot update event. Failed to add an owner.";
                    errors.reject("-8", message);
                }
            }
        }

        Category category = categoryRepo.findByName(updateEventData.getEventCategory()).get(0);
        targetEvent.setCategory(category);
        return targetEvent;
    }
}
