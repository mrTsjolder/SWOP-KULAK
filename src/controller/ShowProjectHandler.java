package controller;


import domain.Clock;
import domain.DetailedProject;
import domain.DetailedTask;
import domain.Project;
import domain.ProjectContainer;
import java.util.ArrayList;
import java.util.List;



/**
 * This handler, handles the show project use case
 * 
 * @author Frederic, Mathias, Pieter-Jan
 */
public class ShowProjectHandler {
    
    private final ProjectContainer manager;
    
    private Project currentProject;
    private final Clock clock;
    
    /**
     * Initialize a new show project handler with the given projectContainer.
     * 
     * @param manager The projectContainer to use in this handler. 
     * @param clock The clock to use in this handler
     */   
    public ShowProjectHandler(ProjectContainer manager, Clock clock){
        this.manager = manager;
        this.clock = clock;
    }
    
    /**
     * 
     * @return A list of projects of this projectContainer
     */
    public List<DetailedProject> getProjects(){
        return new ArrayList<>(manager.getProjects());
    }
    
    /**
     * Select the project with the given id in this projectContainer and set it as
     * this current project to show
     * 
     * @param projectId The id of the project ro retrieve 
     */
    public void selectProject(int projectId){
        currentProject = manager.getProject(projectId);
    }
    /**
     * @return The current project of this handler.
     */  
    public DetailedProject  getProject(){
        return currentProject;
    }
    
    /**
     * Returns the task with the given id of the project with the given id.
     * 
     * @param taskId The id of the task to retrieve.
     * @return The task with the given id in the current project of this handler.
     * @throws IllegalStateException The current project is null.
     */
    public DetailedTask getTask(int taskId) throws IllegalStateException{
        if(currentProject == null){
            throw new IllegalStateException("No project is currently selected in this handler.");
        }
        return currentProject.getTask(taskId);
    }
    
    /**
     * 
     * @return The clock used by this handler.
     */
    public Clock getClock(){
        return clock;
    }
}
