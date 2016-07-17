package com.ezhome.rxfirebase;

import com.firebase.client.DataSnapshot;

/**
 * Created by Spiros I. Oikonomakis on 24/12/15.
 *
 * This class represents a firebase child event when we are
 * using the {@link com.firebase.client.ChildEventListener}
 */
public class FirebaseChildEvent {

  public enum EventType {
    ADDED, CHANGED, REMOVED, MOVED
  }

  /**
   * An {@link DataSnapshot} instance contains data from a Firebase location
   */
  private DataSnapshot dataSnapshot;

  /**
   * The key name of sibling location ordered before the new child
   */
  private String previousChildName;

  /**
   * Represents the type of the children event
   */
  private EventType eventType;

  public FirebaseChildEvent(DataSnapshot dataSnapshot, String previousChildName,
      EventType eventType) {
    this.dataSnapshot = dataSnapshot;
    this.previousChildName = previousChildName;
    this.eventType = eventType;
  }

  public FirebaseChildEvent(DataSnapshot dataSnapshot, EventType eventType) {
    this.dataSnapshot = dataSnapshot;
    this.eventType = eventType;
  }

  public DataSnapshot getDataSnapshot() {
    return dataSnapshot;
  }

  public void setDataSnapshot(DataSnapshot dataSnapshot) {
    this.dataSnapshot = dataSnapshot;
  }

  public String getPreviousChildName() {
    return previousChildName;
  }

  public void setPreviousChildName(String previousChildName) {
    this.previousChildName = previousChildName;
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }
}
