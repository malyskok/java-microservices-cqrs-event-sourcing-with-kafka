/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.techbank.cqrs.core.domain;

import com.techbank.cqrs.core.events.BaseEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AggregateRoot {
    protected String id;
    private int version = -1;

    private final List<BaseEvent> changes = new ArrayList<>();

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<BaseEvent> getUncommittedChanges(){
        return this.changes;
    }

    public void markChangesAsCommitted(){
        this.changes.clear();
    }

    protected void applyChanges(BaseEvent event, Boolean isNewEvent){
        try {
            var method = getClass().getDeclaredMethod("apply", event.getClass());
        } catch (NoSuchMethodException e) {
            log.warn("The 'apply' method was not found in aggregate for: " + event.getClass().getName());
        } catch (Exception e){
            log.error("Error occurred while applying event to aggregate");
        } finally {
            if(isNewEvent){
                changes.add(event);
            }
        }
    }

    public void raiseEvent(BaseEvent event){
        applyChanges(event, true);
    }

    public void replayEvents(Iterable<BaseEvent> events){
        events.forEach(event -> applyChanges(event, false));
    }
}