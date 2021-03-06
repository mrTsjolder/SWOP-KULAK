package domain.time;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This class represents a timespan with a start and end time
 *
 * @author Frederic, Mathias, Pieter-Jan
 */
public final class Timespan implements Comparable<Timespan> {

    private final LocalDateTime startTime, endTime;

    /**
     * Initialize this time span with the given begin and end time.
     *
     * @param startTime The start time of this time span
     * @param endTime The end time of this time span
     * @throws IllegalArgumentException If the given start and end time don't
     * form a valid interval.
     */
    public Timespan(LocalDateTime startTime, LocalDateTime endTime) throws IllegalArgumentException {
        if (!canHaveAsTime(startTime) || !canHaveAsTime(endTime)) {
            throw new IllegalArgumentException("The start time is not valid.");
        }
        if (!canHaveAsTimeInterval(startTime, endTime)) {
            throw new IllegalArgumentException("The start time is later than the end time.");
        }

        this.startTime = startTime;
        this.endTime = endTime;

    }

    /**
     * Initialize an infinite time span with given begin time.
     *
     * @param startTime The start time of this time span.
     */
    public Timespan(LocalDateTime startTime) {
        this(startTime, LocalDateTime.MAX);
    }

    /**
     * Initialize this time span based on the given begin time and duration.
     *
     * @param startTime The start time of this time span
     * @param duration The duration of this time span
     */
    public Timespan(LocalDateTime startTime, Duration duration) {
        this(startTime, startTime.plusMinutes(duration.toMinutes()));
    }

    /**
     * Check whether the given start and end time form a valid interval
     *
     * @param start The start time of the interval
     * @param end The end time of the interval
     * @return True if and only if the start time is strictly before the end
     * time
     */
    private boolean canHaveAsTimeInterval(LocalDateTime start, LocalDateTime end) {
        return start.isBefore(end);
    }

    /**
     * @return The time this time span starts
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * @return The time this time span ends
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Check whether this time span overlaps with the given time span
     *
     * @param anotherTimespan The time span to check the overlap with
     * @return True if and only if both time spans share a certain period of
     * time.
     */
    public boolean overlapsWith(Timespan anotherTimespan) {
    	return anotherTimespan.getStartTime().compareTo(this.getEndTime()) < 0
                && this.getStartTime().compareTo(anotherTimespan.getEndTime()) < 0;
    }

    /**
     * Check whether this timespan contains the given time.
     *
     * @param time The local date time to check
     * @return True if and only if the start time of this time span falls before
     * the given time and the end time of this time span falls after the given
     * time. (Not strict)
     */
    public boolean overlapsWith(LocalDateTime time) {
        return time.compareTo(getStartTime()) >= 0 && time.compareTo(getEndTime()) <= 0;
    }

    /**
     * Returns the duration by which the duration of this time span exceeds the
     * given duration.
     *
     * @param duration The duration to check the delay.
     * @return A duration representing the amount of time the duration of this
     * time span exceeds the given duration. If the duration of this time span
     * is shorter than the given duration, a duration of 0 is returned.
     */
    public Duration getExcess(Duration duration) {
        if (getDuration().compareTo(duration) < 0) {
            return new Duration(0);
        }

        return getDuration().subtract(duration);
    }

    /**
     * @return The duration of this time span
     */
    public Duration getDuration() {
        return new Duration(startTime, endTime);
    }

    /**
     * Check whether this time span can have the given time as its time.
     *
     * @param time The time to check
     * @return True if and only if the given time is not null.
     */
    public boolean canHaveAsTime(LocalDateTime time) {
        return time != null;
    }

    /**
     * Check whether this time span ends before the given time span, not
     * necessarily strictly before
     *
     * @param anotherTimespan The time span to compare to
     * @return True if and only if the end time of this time span is before or
     * equals the start time of the given time span.
     */
    public boolean endsBefore(Timespan anotherTimespan) {
        return getEndTime().compareTo(anotherTimespan.getStartTime()) <= 0;
    }

    /**
     * Check whether the given time is before the end of this time span.
     *
     * @param time The time to check
     * @return True if and only if the end time of this time span happens
     * strictly before the given time.
     */
    public boolean endsAfter(LocalDateTime time) {
        return getEndTime().isAfter(time);
    }

    /**
     * Compares this time span with the given time.
     *
     * @param time The time to compare with.
     * @return 0 if this time span overlaps with the given time, -1 if the given
     * time is strictly after the start time of this time span, 1 otherwise.
     */
    public int compareTo(LocalDateTime time) {
        if (overlapsWith(time)) {
            return 0;
        } else if (getStartTime().isBefore(time)) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Compares this time span with another time span. This is done by comparing
     * starting times. In case of equal start times, end times are being
     * compared.
     *
     * @param	other The time span to be compared with.
     * @return	0 if this time span equals other, negative if other starts or
     * ends before this, positive if other starts or ends after this.
     */
    @Override
    public int compareTo(Timespan other) {
        int res = this.getStartTime().compareTo(other.getStartTime());
        
        if (res == 0) {
            res = this.getEndTime().compareTo(other.getEndTime());
        }
        
        return res;
    }

    /**
     * Checks whether this time span starts after the given time.
     *
     * @param time The time to check.
     * @return True if and only if the start time of this time span is after the
     * given time.
     */
    public boolean startsAfter(LocalDateTime time) {
        return getStartTime().isAfter(time);
    }

    /**
     * Checks whether this time span starts before the given time.
     *
     * @param time The time to check.
     * @return True if and only if the start time of this time span is before the
     * given time.
     */
    public boolean startsBefore(LocalDateTime time) {
        return getStartTime().isBefore(time);
    }
    
    /**
     * 
     * @return A textual representation of this time span. 
     */
    @Override
    public String toString() {
        return "[" + startTime.toString() + " to " + endTime.toString() + "]";
    }

    /**
     * Checks whether the given object is a time span and whether it represents
     * the same span over time as this time span.
     *
     * @param other The object to compare with
     * @return True if and only if the other object is also a time span and the
     * hash code of the other time span matches the hash code of this time span.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Timespan)) {
            return false;
        }
        return this.hashCode() == other.hashCode();
    }

    /**
     *
     * @return a hash code based on the start and end time of this time span.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.startTime);
        hash = 59 * hash + Objects.hashCode(this.endTime);
        return hash;
    }

}
