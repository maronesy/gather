package cs428.project.gather.data.model;

import cs428.project.gather.data.form.*;
import cs428.project.gather.data.repo.*;

import java.util.*;
import java.sql.Timestamp;
import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.joda.time.DateTime;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * 
 * @author Team Gather
 *
 * This is the main model class of the application in charge of all the event variables and the association 
 * between the event and different classes in the model
 * 
 */

@Entity
public class Event {
    private static final double ONE_MILE_IN_DEGREES_LATITUDE = 0.014554;
    private static final double ONE_MILE_IN_DEGREES_LONGITUDE = 0.014457;

    private @Id @Column(name="ID") @GeneratedValue Long id;

    private String name;
    private String description;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Location location;

    //@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch( FetchMode.SELECT)
    @JoinColumn(name = "event_id")
    private List<Occurrence> occurrences = new ArrayList<Occurrence>();

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

    public boolean setOccurrencesFrom(List<Long> newTimestamps) {
        Assert.notNull(newTimestamps);
        this.occurrences.clear();
        for(int i=0; i<newTimestamps.size(); i++){
            if(!this.occurrences.add(new Occurrence("",new Timestamp(newTimestamps.get(i))))){
                return false;
            }
        }
        return true;
    }

    private boolean setParticipantsFrom(List<String> participantNames, RegistrantRepository registrantRepo) {
         Assert.notNull(participantNames);
            participants.clear();
            for(int i=0; i<participantNames.size(); i++){
                Registrant aParticipant = registrantRepo.findByDisplayName(participantNames.get(i));
                if(!this.participants.add(aParticipant)){
                    return false;
                }
            }
            return true;
    }

    private boolean setOwnersFrom(List<String> ownerNames, RegistrantRepository registrantRepo) {
         Assert.notNull(ownerNames);
            owners.clear();
            for(int i=0; i<ownerNames.size(); i++){
                Registrant aParticipant = registrantRepo.findByDisplayName(ownerNames.get(i));
                if(!this.owners.add(aParticipant)){
                    return false;
                }
            }
            return true;
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

    public void removeAllOccurrences(){
        occurrences.clear();
    }

    public boolean containsOwner(Registrant owner, Errors errors) {
        if (! getOwners().contains(owner)) {
            errors.reject("-9", "Cannot update event. The request Registrant is not the event owner.");
            return false;
        }
        return true;
    }

    /**
     * This method takes data regarding the events requested and user and finds and returns a list of events near
     * the user
     * 
     * @param queryParams: This variable is the information regarding the events the the user wants returned
     * @param eventRepo: This variable is used to fetch the desired events from the event repo
     * @param maybeUser: This variable is the registrant object of the user
     * @param errors: This variable is in charge of return an error if the maybeUser variable is null
     * @return The method will return a list of events matching the query parameters
     * 
     */
    public static List<Event> queryForEvents(EventsQueryData queryParams, EventRepository eventRepo, Registrant maybeUser, Errors errors) {
        Set<String> filterCategories = queryParams.getCategories();
        if (queryParams.getUseRegistrantProfile() == true) {
            if (maybeUser == null) {
                errors.reject("-7", "Cannot query for events. useRegistrantProfile was set to true, but user is not authenticated.");
                return null;
            }

            // If the useRegistrantProfile flag is true, then we will use the registrant's profile settings to query our events
            queryParams.setRadiusMi(maybeUser.getDefaultRadiusMi());
            queryParams.setHour(maybeUser.getDefaultTimeWindow());
            filterCategories = new HashSet<String>();
            for (Category cat : maybeUser.getPreferences()) {
                filterCategories.add(cat.getName());
            }
        }

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
        if (! filterCategories.isEmpty()) {
            categoryFilteredEvents = new ArrayList<Event>();
            for (Event ev : events) {
                if (filterCategories.contains(ev.getCategory().getName()))
                    categoryFilteredEvents.add(ev);
            }
        }

        return categoryFilteredEvents;
    }

    /**
     * 
     * This method creates an event base on the parameters and returns it
     * 
     * @param newEventData: Information about the new event
     * @param owner: The owner trying to clear the event
     * @param categoryRepo: The category repository
     * @param errors: Used for reporting errors if event creation is unsuccessful
     * @return The newly created event is returned
     * 
     */
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

        if (!newEvent.setOccurrencesFrom(newEventData.getOccurrences())) {
            String message = "Cannot create event. Failed to add occurrences to event.";
            errors.reject("-7", message);
        }

        Category category = categoryRepo.findByName(newEventData.getEventCategory()).get(0);
        newEvent.setCategory(category);
        return newEvent;
    }

    /**
     * This method update the information of an existing event
     * 
     * @param updateEventData: Updates that the user wants to make to the event
     * @param owner: The user making the updates
     * @param registrantRepo: The Registrant repository
     * @param categoryRepo: The Category repository
     * @param errors: Used for reporting errors if event update is unsuccessful
     * @return: returns the updated event
     * 
     */
    public Event updateEventUsing(UpdateEventData updateEventData, Registrant owner, RegistrantRepository registrantRepo, CategoryRepository categoryRepo, Errors errors) {
        if (! this.containsOwner(owner,errors)) {
            return this;
        }

        if(updateEventData.getEventName()!=null){
            this.setName(updateEventData.getEventName());
        }

        if(updateEventData.getEventDescription()!=null){
            this.setDescription(updateEventData.getEventDescription());
        }

        if(updateEventData.getEventCoodinates()!=null){
            this.setLocation(new Location(updateEventData.getEventCoodinates()));
        }

        if(updateEventData.getOccurrences()!=null){
            if (!this.setOccurrencesFrom(updateEventData.getOccurrences())) {
                String message = "Cannot create event. Failed to add occurrences to event.";
                errors.reject("-7", message);
            }
        }

        if(updateEventData.getParticipants()!=null){
            System.out.println("not null participants");
            if (!this.setParticipantsFrom(updateEventData.getParticipants(),registrantRepo)) {
                String message = "Cannot create event. Failed to add participants to event.";
                errors.reject("-7", message);
            }
        }

        if(updateEventData.getOwners()!=null){
            if (!this.setOwnersFrom(updateEventData.getOwners(),registrantRepo)) {
                String message = "Cannot create event. Failed to add owners to event.";
                errors.reject("-7", message);
            }
        }

        if(updateEventData.getEventCategory()!=null){
            Category category = categoryRepo.findByName(updateEventData.getEventCategory()).get(0);
            this.setCategory(category);
        }
        return this;
    }
}
