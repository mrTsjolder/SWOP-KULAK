package domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This class represents the system clock
 * 
 * @author Frederic, Mathias, Pieter-Jan 
 */
public class Clock {
	
    /**
     * Time used to initialize the clock, when no time is given.
     */
    public static final LocalDateTime INCEPTION = LocalDateTime.of(2000, 1, 1, 0, 0);
    
    private LocalDateTime time;
    
    /**
     * Initializes this clock with the given time.
     * 
     * @param 	time 
     * 			The intitial time to start the clock on
     */
    public Clock(LocalDateTime time) {
        setTime(time);
    }
    
    /**
     * Initializes this clock with INCEPTION constant as its inception moment.
     */
    public Clock() {
        this(INCEPTION);
    }
    
    /**
     * Initialize a copy of a given clock.
     * 
     * @param 	c
     * 			The clock to be copied.
     */
    public Clock(Clock c) {
    	this(c.getTime());
    }
    
    /****************************************
     * Getters & setters					*
     ****************************************/
    
    /**
     * @return	the current time of this clock.
     */
    public LocalDateTime getTime() {
    	return this.time;
    }
    
    /**
     * Set the time of this clock.
     * 
     * @param 	time
     * 			The new time for this clock.
     */
    private void setTime(LocalDateTime time) {
    	this.time = time;
    }
    
    /**
     * Advance the current system time to the given time.
     * 
     * @param 	time 
     * 			The time in the future to which the system time must be advanced.
     * @throws 	IllegalArgumentException 
     * 			if the given time lays strictly in the past, compared to this clocks time.
     */
    public void advanceTime(LocalDateTime time) throws IllegalArgumentException{
        if(time.isBefore(this.time)){
            throw new IllegalArgumentException("The given timestamp is strictly before the current system time.");
        }
        
        setTime(time);
    }
    
    /**
     * Check whether the given time is strictly after this clock time
     * 
     * @param time The time to check
     * @return True if and only if the given time is strictly more in the future
     * than the time of this clock.
     */
    public boolean isAfter(LocalDateTime time){
        return getTime().isAfter(time);
    }
    
    /**
     * Check whether the given time is strictly before this clock time
     * 
     * @param time The time to check
     * @return True if and only if the given time is strictly more in the past
     * than the time of this clock.
     */
    public boolean isBefore(LocalDateTime time){
        return getTime().isBefore(time);
    }
    
    /**
     * Check wether the given object is equal to this clock.
     * 
     * @param o The other object to check.
     * @return True if and only if the given object is a Clock and it has the 
     * same time as this clock.
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Clock)){
            return false;
        }
    	return ((Clock) o).getTime().equals(getTime());
    }
    /**
     * 
     * @return The hahcode for this clock, clocks with the same time, have the 
     * same hashcode.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.time);
        return hash;
    }
}
