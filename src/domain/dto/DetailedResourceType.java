package domain.dto;

import domain.ResourceType;

/**
 * This interface provides access to the properties of resourcetype, without 
 * exposing bussiness logic to the UI
 *  
 * @author Mathias, Frederic, Pieter-Jan
 */
public interface DetailedResourceType {

    public static final ResourceType DEVELOPER = new ResourceType("developer");

    /**
     *
     * @return The name of this resourcetype
     */
    String getName();
    
}
