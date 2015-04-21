package domain;

import exception.ConflictException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a task
 *
 * @author Frederic, Mathias, Pieter-Jan
 */
public class Task implements DetailedTask {

    private static int nextId = 0;

    private final int id;
    private String description;
    private int acceptableDeviation;
    private Timespan timeSpan;
    private final Duration estimatedDuration;
    private Task alternativeTask;
    private List<Task> prerequisiteTasks;
    private Status status;
    private LocalDateTime plannedStartTime;
    private final Map<ResourceType, Integer> requiredResources;

    /****************************************
     * Constructors							*
     ****************************************/
    /**
     * Initializes this task based on the given description, estimated duration,
     * acceptable deviation and prerequisite tasks.
     *
     * @param description The description of this task.
     * @param duration The estimated duration of this task
     * @param accDev The acceptable deviation of this task expressed as an
     * integer between 0 and 100.
     * @param prereq The list of prerequisite tasks for this task.
     * @param resources The resources this task requires to be performed
     */
    Task(String description, Duration duration, int accDev, List<Task> prereq, Map<ResourceType, Integer> resources) {
        if(!ResourceType.isValidCombination(resources))
            throw new IllegalArgumentException("This combination of resourcetypes is not valid.");
        this.id = generateId();
        setDescription(description);
        this.estimatedDuration = duration;
        setAcceptableDeviation(accDev);
        if (prereq == null) {
            setPrerequisiteTasks(new ArrayList<>());
        } else {
            setPrerequisiteTasks(prereq);
        }
        
        
        this.requiredResources = resources;
        
        Status initStatus = new Available();
        setStatus(initStatus);
        initStatus.update(this);
    }

    /**
     * Initializes this task based on the given description, estimated duration
     * and acceptable deviation.
     *
     * @param description The description of this task.
     * @param duration The estimated duration of this task
     * @param accDev The acceptable deviation of this task expressed as an
     * integer between 0 and 100.
     */
    Task(String description, Duration duration, int accDev) {
        this(description, duration, accDev, null, new HashMap<ResourceType, Integer>());
    }

    /**
     * **************************************
     * Getters & Setters & Checkers	*
     ***************************************
     */
    /**
     * Generates an id for a new task.
     *
     * @return The id to be used for a newly created task.
     */
    private static int generateId() {
        return nextId++;
    }

    /**
     * @return The identification number of this task.
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * @return The description of this task.
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of this task to the given description.
     *
     * @param description The new description of this task.
     * @throws IllegalArgumentException If this task can't have the given
     * description as its description.
     */
    private void setDescription(String description) throws IllegalArgumentException {
        if (!canHaveAsDescription(description)) {
            throw new IllegalArgumentException("The given description is uninitialized.");
        } else {
            this.description = description;
        }
    }

    /**
     * Checks whether this task can have the given description as its
     * description.
     *
     * @param description The description to check.
     * @return True if and only if the given description is not equal to null
     * and the given is not empty.
     */
    public boolean canHaveAsDescription(String description) {
        return description != null && !description.isEmpty();
    }

    /**
     * @return The estimated duration of this task.
     */
    @Override
    public Duration getEstimatedDuration() {
        return this.estimatedDuration;
    }

    /**
     * @return The timeSpan indicating the actual start and end time if this
     * task has one, null otherwise.
     */
    @Override
    public Timespan getTimeSpan() {
        return this.timeSpan;
    }

    /**
     * Sets the time span of this task to the given time span.
     * (this method must only be used by a subclass of status.)
     * 
     * @param timeSpan The new time span of this task.
     */
    void setTimeSpan(Timespan timeSpan) throws IllegalArgumentException {
        this.timeSpan = timeSpan;
    }
    
    /**
     * @return The acceptable deviation of this task expressed as an integer
     * between 0 and 100.
     */
    @Override
    public int getAcceptableDeviation() {
        return this.acceptableDeviation;
    }
    
    /**
     * Sets this task his acceptable deviation to the given acceptable
     * deviation.
     *
     * @param accDev The new acceptable deviation of this task.
     * @throws IllegalArgumentException If this task can't have the given
     * acceptable deviation as its acceptable deviation.
     */
    private void setAcceptableDeviation(int accDev) throws IllegalArgumentException {
        if (!canHaveAsAcceptableDeviation(accDev)) {
            throw new IllegalArgumentException(
                    "This task can't have the given acceptable deviation as its acceptable deviation.");
        }
        this.acceptableDeviation = accDev;
    }
    
    /**
     * Checks whether this task can have the given acceptable deviation as its
     * acceptable deviation.
     *
     * @param accDev The acceptable deviation.
     * @return True if and only if the given acceptable deviation is between 0
     * and 100.
     */
    public boolean canHaveAsAcceptableDeviation(int accDev) {
        return (0 <= accDev) && (accDev <= 100);
    }

    /**
     * @return The alternative task for this task.
     */
    @Override
    public Task getAlternativeTask() {
        return this.alternativeTask;
    }

    /**
     * Sets the alternative task of this task to the given alternative task.
     *
     * @param alternativeTask The alternative task for this task.
     * @param project The project to which this task and the given alternative
     * task belong to.
     * @throws IllegalStateException
     * @see Status#setAlternativeTask(domain.Task, domain.Project)
     * @see canHaveAsAlternativeTask
     */
    public void setAlternativeTask(Task alternativeTask, Project project) throws IllegalStateException, IllegalArgumentException {
        status.setAlternativeTask(this, alternativeTask, project);
    }
    
    /**
     * This method sets the alternative of this task without any checks
     * (Must only be used by subclasses of status.)
     * 
     * @param task The alternative task.
     */
    void setAlternativeTaskRaw(Task task){
        alternativeTask = task;
    }

    /**
     * Checks whether this task can have the given alternative task as its
     * alternative task.
     *
     * @param altTask The alternative task to check.
     * @return False if the given alternative task is equal to null.
     *         False if the given alternative task is equal to this task.
     *         False if the given alternative task depends on this task.
     *         False if this task doesn't belong to the given project.
     *         False if the given alternative task doesn't belong to the given project.
     *         True otherwise.
     * @see dependsOn
     */
    public boolean canHaveAsAlternativeTask(Task altTask, Project project) {
        if (altTask == null) {
            return false;
        }
        if (altTask.equals(this)) {
            return false;
        }
        if (altTask.dependsOn(this)) {
            return false;
        }
        if(!project.hasTask(getId()))
        	return false;
        if(!project.hasTask(altTask.getId()))
        	return false;
        return true;
    }

    /**
     * @return The list of prerequisite tasks for this task.
     */
    @Override
    public List<Task> getPrerequisiteTasks() {
        return new ArrayList<>(this.prerequisiteTasks);
    }

    /**
     * Sets the list of prerequisite tasks to the given list of prerequisite
     * tasks.
     *
     * @param prereq The new list of prerequisite tasks for this task.
     * @throws IllegalArgumentException If this task can't have the given list
     * of prerequisite tasks as its list of prerequisite tasks.
     */
    private void setPrerequisiteTasks(List<Task> prereq) throws IllegalArgumentException {
        if (!canHaveAsPrerequisiteTasks(prereq)) {
            throw new IllegalArgumentException(
                    "This task can't have the given list of prerequisite tasks as its prerequisite tasks.");
        }
        this.prerequisiteTasks = prereq;
    }

    /**
     * Checks whether this task can have the given list of prerequisite tasks as
     * its prerequisite tasks.
     *
     * @param prereq The list of prerequisite tasks to check.
     * @return False if the list is null. False if any of the tasks in the given
     * list is null. False if any of the tasks in the given list is equal to
     * this task. False if any of the tasks in the given list depends on this
     * task. True otherwise.
     * @see dependsOn
     */
    public boolean canHaveAsPrerequisiteTasks(List<Task> prereq) {
        if (prereq == null) {
            return false;
        }
        for (Task t : prereq) {
            if (t == null || t == this || t.dependsOn(this)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return The status of this task.
     */
    @Override
    public final Status getStatus() {
        status.update(this);
        return this.status;
    }

    /**
     * Sets the status of this task to the given status.
     * Must only be accessed by objects that are subclasses of status
     *
     * @param status The new status of this task.
     */
    final void setStatus(Status status) {
        this.status = status;
    }

    /**
     * **************************************
     * Other methods	*
     ***************************************
     */
    /**
     * Calculates the duration by which this task been delayed.
     *
     * @return The duration by which this task has been delayed if this task has
     * a time span, based on the maximum duration of this task. If this task
     * doesn't have a time span then the result is a duration of 0 minutes.
     */
    @Override
    public Duration getDelay() {
        if (getTimeSpan() == null) {
            return new Duration(0);
        }

        return getTimeSpan().getExcess(calculateMaxDuration());
    }

    /**
	 * @return the requiredResources
	 */
	public Map<ResourceType, Integer> getRequiredResources() {
            return requiredResources;
	}

	/**
     * Calculates the maximum duration of this task by which this task will
     * still be finished on time.
     *
     * @return The estimated duration of this task multiplied by (100 + (the
     * acceptable deviation of this task))/100
     */
    Duration calculateMaxDuration() {
        return getEstimatedDuration().multiplyBy((100d + getAcceptableDeviation()) / 100d);
    }

    /**
     * Updates the status and the time span of this task.
     *
     * @param start The beginning of the time span of this task.
     * @param end The end of the time span of this task.
     * @param status The new status of this task.
     * @param project The project this task belongs to.
     * @throws IllegalArgumentException The given status is neither FAILED nor
     * FINISHED and is therefore not a valid status that can be assigned to this
     * task.
     * @throws IllegalArgumentException If the start and/or end time are not
     * initialized.
     * @throws IllegalArgumentException If the given project doesn't contain
     * this task.
     * @throws IllegalArgumentException If The given project's creation time is
     * before the given start time.
     */
    final void update(LocalDateTime start, LocalDateTime end, Status status, Project project) throws IllegalArgumentException {
        if (start == null || end == null) {
            throw new IllegalArgumentException("The given start and/or end time are not initialized.");
        }
        if (project.getTask(getId()) == null) {
            throw new IllegalArgumentException("The given project doesn't contain this task.");
        }
        if (project.getCreationTime().compareTo(start) > 0) {
            throw new IllegalArgumentException("A task can't have started before its project.");
        }
        
        //TODO
        Timespan timespan = new Timespan(start, end);
        if(status instanceof Failed){
            getStatus().fail(this, timespan);
        }else if(status instanceof Finished){
            getStatus().finish(this, timespan);
        }else{
            throw new IllegalArgumentException("Invalid status");
        }
        
        
    }
    /** TODO: clear future reservations
     * Fail this task
     * 
     * @param timespan The timespan of this failed task
     * 
     */
    void fail(Timespan timespan)
    {
    	getStatus().fail(this, timespan);
    }
    
    /** TODO: clear future reservations
     * Finish this task
     * 
     * @param timespan The timespan of this finished task
     * 
    */
    void finish(Timespan timespan)
    {
    	getStatus().finish(this, timespan);
    }
    
    void clearFutureReservations()
    {
    	for(Resource resource : getRequiredResources())
    	{
    		resource.clearFutureReservations();
    	}
    }
    /**
     * Checks whether this task is available.
     *
     * @return True if and only if the status of this task is available.
     */
    public boolean isAvailable() {
        return (getStatus() instanceof Available);
    }

    /**
     * Checks whether this task has been fulfilled.
     *
     * @return True if and only if this task is finished or if it has an
     * alternative task and this alternative task is fulfilled.
     */
    public boolean isFulfilled() {
        return getStatus().isFulfilled(this);
    }

    /**
     * Checks whether this task ends before the given time span.
     *
     * @param timeSpan The time span to check.
     * @return True if the time span of this task ends before the start of the
     * given time span.
     * @throws IllegalStateException If this task does not have a time span.
     */
    public boolean endsBefore(Timespan timeSpan) throws IllegalStateException {
        if (getTimeSpan() == null) {
            throw new IllegalStateException(
                    "Tried to check whether this task ends before the given time while this task doesn't have a time span.");
        }
        return getTimeSpan().endsBefore(timeSpan);
    }
    
    /**
     * Checks whether this task has a time span.
     *
     * @return True if and only if the time span of this task is not null.
     */
    public boolean hasTimeSpan() {
        return getTimeSpan() != null;
    }

    /**
     * Checks whether this task directly or indirectly depends on the given
     * task.
     *
     * @param task The task to check.
     * @return True if this task has an alternative task and the alternative
     * task is equal to the given task. True if a prerequisite task of this task
     * is equal to the given task. True if a prerequisite task depends on the
     * given task. False otherwise.
     */
    public boolean dependsOn(Task task) {
        if (getAlternativeTask() != null && getAlternativeTask().equals(task)) {
            return true;
        }
        for (Task prereqTask : getPrerequisiteTasks()) {
            if (prereqTask.equals(task)) {
                return true;
            }

            if (prereqTask.dependsOn(task)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the status of this task is equal to finished.
     *
     * @return True if and only if the status of this task is equal to finished.
     */
    public boolean isFinished() {
        return getStatus() instanceof Finished;
    }

    /**
     * The estimated amount of work time needed for this task.
     *
     * @return If this task is finished or failed, a duration of 0. If this task
     * is unavailable, the sum of the amounts of estimated work time needed of
     * the prerequisite tasks of this task + the estimated duration of this task
     * + the estimated durations of the alternatives of the prerequisites of
     * this task. If this task is available, the estimated duration of this
     * task.
     *
     */
    public Duration estimatedWorkTimeNeeded() {
        return getStatus().estimatedWorkTimeNeeded(this);
    }

    /**
     * Checks whether this task can be fulfilled or already is fulfilled.
     *
     * @return True if this task is finished or available. False if this task is
     * failed and doesn't have an alternative task. True if this task is failed
     * and has an alternative task that can be fulfilled. False if this task is
     * unavailable and any of the prerequisite tasks can't be fulfilled. True if
     * this task is unavailable and all prerequisite tasks can be fulfilled.
     */
    public boolean canBeFulfilled() {
        return getStatus().canBeFulfilled(this);
    }

    /**
     * Calculates the amount of time spent on this task.
     *
     * @return If this task has a time span then the result is equal to the sum
     * of the maximum amount of time spent on a prerequisite task of this task
     * and the alternative task if this task has an alternative task and the
     * duration of the time span of this task. Otherwise a duration of 0 minutes
     * is returned.
     */
    public Duration getTimeSpent() {
        if (hasTimeSpan()) {
            Duration temp, max = Duration.ZERO;
            for (Task t : getPrerequisiteTasks()) {
                temp = t.getTimeSpent();
                if (temp.compareTo(max) > 0) {
                    max = temp;
                }
            }
            temp = getTimeSpan().getDuration().add(max);
            if (hasAlternativeTask()) {
                return temp.add(getAlternativeTask().getTimeSpent());
            }
            return temp;
        }
        return new Duration(0);
    }

    /**
     * Checks whether this task has an alternative task.
     *
     * @return True if and only if this task has an alternative task.
     */
    public boolean hasAlternativeTask() {
        return getAlternativeTask() != null;
    }
    
    /**
     * Returns the etimated end time of this task
     * 
     * @param clock The clock to use to determine the start time of this task
     * @return The time of the given clock + the estimated work time needed
     * by this task.
     */
    public LocalDateTime getEstimatedEndTime(Clock clock)
    {
        return estimatedWorkTimeNeeded().getEndTimeFrom(clock.getTime());
    }
    
    /**
     * Move this task to the executing state
     */
    public void execute(){
        getStatus().execute(this);
    }
    
    /**
     * Check whether this task is planned
     * 
     * @return True if and only if this task has a planned start time.
     */
    public boolean isPlanned(){
        return plannedStartTime != null;
    }
    
    /**
     * Plan this task at the given start time
     * 
     * @param startTime The time this task is planned to start
     * @param developers The developers to assign to this task
     * @param resources The resources to assign to this task // TODO unificeren met devs??
     * @throws exception.ConflictException The task's reservations 
     * conflict with another task
     */
    public void plan(LocalDateTime startTime, List<User> developers, List<Resource> resources) throws ConflictException{
        
        Timespan timespan = new Timespan(startTime, getEstimatedDuration().getEndTimeFrom(startTime));
        for(ResourceType type : requiredResources.keySet()){
            for(requiredResources.get(type))
        }
    }
}
