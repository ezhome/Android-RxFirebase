/**
 * Copyright 2016 Ezhome Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ezhome.rxfirebase2;

import com.google.firebase.database.DataSnapshot;

/**
 * This class represents a firebase child event when we are
 * using the Child event listener
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
