package domain;


import domain.datainterface.DetailedResource;
import domain.time.Clock;
import domain.time.Timespan;
import exception.ConflictException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Resource implements ClockObserver, DetailedResource {

	private static int nextId = 0;

	//TODO: wordt het niet stilletjesaan interessant om een id-klasse te maken?
	private final int id;
	private final String name;
	private final Set<Reservation> reservations;
	private final Set<Reservation> previousReservations;

	/**
	 * Initialize a resource with a given name.
	 * 
	 * @param 	name
	 *       	The name for this resource.
     * @param clock The clock this resource has to observe
	 */
	public Resource(String name, Clock clock) {
		if(!canHaveAsName(name))
			throw new IllegalArgumentException("This resource can't have the given name as name.");
	    if(clock == null)
	        throw new IllegalArgumentException("This resource can't observe the given clock.");
		this.id = generateId();
		this.name = name;
		this.reservations = new HashSet<>();
		this.previousReservations = new HashSet<>();
	    clock.attach(this);
	}

	public Resource(String name) {
		this(name, new Clock());
	}

	/****************************************************
	 * Getters & Setters                                *
	 ****************************************************/

	/**
	 * Generates an id for a new Resource.
	 *
	 * @return The id to be used for a newly created task.
	 */
	private static int generateId() {
		return nextId++;
	}

	/**
	 * @return the id
	 */
    @Override
	public int getId() {
		return id;
	}

	public boolean canHaveAsName(String name) {
		return name != null && name.length() > 0;
	}

	/**
	 * @return the name
	 */
    @Override
	public String getName() {
		return name;
	}

	/**
	 * @return the reservations
	 */
	public Set<Reservation> getReservations() {
		return new HashSet<>(reservations);
	}

	/**
	 * @return The previous reservations.
	 */
	public Set<Reservation> getPreviousReservations() {
		return new HashSet<>(previousReservations);
	}

	/****************************************************
	 * Accessors                                       *
	 ****************************************************/

	/**
	 * Return whether or not this resource is available during a given time span.
	 * 
	 * @param 	span
	 * 			The time span the resource should be available in.
	 * @return	{@code true} if there exist no reservation of this resource 
	 *        	with a time span overlapping span.
	 */
	public boolean isAvailable(Timespan span) {
		for(Reservation r : reservations) {
			if(r.conflictsWith(span))
				return false;
		}
		return true;
	}

	/**
	 * Get the set of reservations that conflict with a given time span.
	 * 
	 * @param 	span
	 *       	The time span the reservation conflicts with.
	 * @return	all reservation that conflict with span.
	 * @see		Reservation#conflictsWith(Timespan)
	 */
	public Set<Reservation> getConflictingReservations(Timespan span) {
		Set<Reservation> result = new HashSet<>();
		for(Reservation r : reservations)
			if(r.conflictsWith(span))
				result.add(r);
		return result;
	}

	/**
	 * Get the set of tasks that cause conflicts with a given time span.
	 * 
	 * @param 	span
	 * 	     	The time span the tasks conflict with.
	 * @return	the tasks from all conflicting reservations.
	 * @see		#getConflictingReservations(Timespan)
	 */
	public Set<Task> findConflictingTasks(Timespan span) {
		Set<Reservation> temp = getConflictingReservations(span);
		Set<Task> result = new HashSet<>();
		for(Reservation r : temp)
			result.add(r.getTask());
		return result;
	}
	
	/**
	 * Get all the reservations that involve a given task.
	 * 
	 * @param	t
	 *       	The task to get the reservations for.
	 * @return	the reservation linked to the given task.
	 */
	public Reservation getReservation(Task t) {
		for(Reservation r : reservations)
			if(r.getTask().equals(t))
				return r;
		return null;
	}
	
	/**
	 * Get the reservations from a given point in time.
	 * 
	 * @param 	from
	 *       	The time stamp to start looking for reservations.
	 * @return	A sorted set of reservations which are not yet finished on from.
	 */
	public SortedSet<Reservation> getReservations(LocalDateTime from) {
		SortedSet<Reservation> result = new TreeSet<>(Reservation.timespanComparator());
		for(Reservation r : getReservations()) {
			if(!r.expiredBefore(from))
				result.add(r);
		}
		return result;
	}
	
	/**
	 * Get the time spans this resource is available from a given point in time.
	 * 
	 * @param 	from
	 *       	The time stamp to start looking for free time spans.
	 * @return	a sorted set of time spans in which this resource is free.
	 *        	The last time span is an infinite time span.
	 */
	public SortedSet<Timespan> nextAvailableTimespans(LocalDateTime from) {
		SortedSet<Timespan> result = new TreeSet<>();
		SortedSet<Reservation> reservations = getReservations(from);
		if(reservations.isEmpty()) {
			result.add(new Timespan(from));
			return result;
		}
		
		Iterator<Reservation> it = reservations.iterator();
		Reservation r1, r2 = it.next();
		if(r2.startsAfter(from))
			result.add(new Timespan(from, r2.getStartTime()));
		
		while(it.hasNext()) {
			r1 = r2;
			r2 = it.next();
			if(!r1.conflictsWith(r2))
				result.add(new Timespan(r1.getEndTime(), r2.getStartTime()));
		}
		
		result.add(new Timespan(r2.getEndTime()));
		return result;
	}
	
	/****************************************************
	 * Mutators                                         *
	 ****************************************************/

	/**
	 * Reserve this resource for a given task and a given time span.
	 * 
	 * @param 	task
	 *       	The task for which this resource needs to be reserved.
	 * @param 	span
	 *       	The time span to reserve this resource for.
	 * @throws 	ConflictException
	 *        	if this resource is not available during span.
	 */
	public Reservation makeReservation(Task task, Timespan span) throws ConflictException {
		if(getReservation(task) != null)
			throw new IllegalArgumentException("This resource has already been reserved for the given task.");
		if(!isAvailable(span)) {
			Set<Task> confl = findConflictingTasks(span);
			throw new ConflictException("This resource is not available for the given timespan.", task, confl);
		}
		Reservation r = new Reservation(task, span);
		reservations.add(r);
		return r;
	}
	
	/**
	 * Clears the future reservations of the given task. If the reservation is only partially in the future,
	 * the already consumed part will be archived.
	 * 
	 * @param currentTime The current time.
	 * @param task The task of which the future reservations are cleared.
	 */
	public void clearFutureReservations(LocalDateTime currentTime, Task task)
	{
		for(Iterator<Reservation> iterator = getReservations().iterator(); iterator.hasNext();)
		{
			Reservation reservation = iterator.next();
			if(reservation.getTask().equals(task))
			{
				if(reservation.getTimespan().getStartTime().isAfter(currentTime)) // in the future TODO reservation logica
				{
					iterator.remove();
				}
				else if (reservation.getTimespan().overlapsWith(currentTime)) // partially consumed TODO reservation logica?
				{
					iterator.remove();
					Timespan newTimeSpan = new Timespan(reservation.getTimespan().getStartTime(), currentTime); // TODO timespan logica?
					archiveReservation(new Reservation(reservation.getTask(), newTimeSpan));
				}
			}
		}
	}

	void archiveReservation(Reservation r)
	{
		previousReservations.add(r);
	}
	
	/**
	 * Archives the reservations which are in the past compared to the given current time.
	 */
        @Override
	public void update(LocalDateTime currentTime)
	{
		for(Iterator<Reservation> iterator = getReservations().iterator(); iterator.hasNext();)
		{
			Reservation reservation = iterator.next();
			if(reservation.getTimespan().compareTo(currentTime) <= 0) // TODO logica misschien verplaatsen
			{
				iterator.remove();
				archiveReservation(reservation);
			}
		}
	}
	
	/**
	 * Removes the given reservation from this resources reservations.
	 * 
	 * @param reservation The reservation to remove.
	 * @return True if this resource contained the given reservation.
	 */
	public boolean removeReservation(Reservation reservation)
	{
		return getReservations().remove(reservation);
	}
        
           /**
         * Make the given reservation for this resource
         * 
         * @param reservation The reservation to make 
     * @throws exception.ConflictException The given reservation conflicts with
     * another reservation.
         */
    public void makeReservation(Reservation reservation) throws ConflictException {
        makeReservation(reservation.getTask(), reservation.getTimespan());
    }
    
    /**
     * Creates a memento for this resource.
     * 
     * @return A memento which stores the the state of this resource.
     */
    public Memento createMemento()
    {
    	return new Memento(reservations, previousReservations);
    }
    
    /**
     * Sets the state of this task to the state stored inside the given memento.
     * 
     * @param memento The memento containing the new state of this task.
     */
    public void setMemento(Memento memento)
    {
    	this.reservations.clear();
    	this.reservations.addAll(memento.getReservations());
    	
    	this.previousReservations.clear();
    	this.previousReservations.addAll(memento.getPreviousReservations());
    }
    
    /**
     * 
     * @return The name of this resource
     */
    @Override
    public String toString(){
        return getName();
    }
    
    public class Memento {
		private final Set<Reservation> reservations;
		private final Set<Reservation> previousReservations;
		
		private Set<Reservation> getReservations()
		{
			return new HashSet<>(this.reservations);
		}
		
		private Set<Reservation> getPreviousReservations()
		{
			return new HashSet<>(this.previousReservations);
		}
		
		private Memento(Set<Reservation> reservations, Set<Reservation> previousReservations)
		{
			this.reservations = new HashSet<>(reservations);
			this.previousReservations = new HashSet<>(previousReservations);
		}
    }
}
