/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  private DatastoreService datastore;

  public Datastore(){
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Returns a cleaned version of the input text */
  public String cleanedMessage(String text) {
    String cleanText = Jsoup.clean(text, Whitelist.none()); // Currently removing HTML and converting to BBCode.
    return cleanText; // TODO: BBCode insertion here, possibly prime-transformer via maven
  }
 

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message){
    Entity message_entity = new Entity("Message", message.getId().toString());
    message_entity.setProperty("user", message.getUser());
    message_entity.setProperty("text", cleanedMessage(message.getText()));
    message_entity.setProperty("timestamp", message.getTimestamp());
    message_entity.setProperty("recipient", message.getRecipient());

    datastore.put(message_entity);
  }

  /**
   * Gets messages based on a particular query and user (optional).
   * @param query Query object to filter messages
   * @param (opt) user String identifying the user
   * @return a list of messages based on the input query and user, empty list if there are no messages
   * posted matching the query and user. If user is blank, then get messages from all users matching the query.
   *  Output list is sorted by time, descending order.
   */

  public List<Message> getMessagesByQuery(Query query, String user) {

    List<Message> messages = new ArrayList<>();
    boolean useAnyMessage = user.isEmpty();


    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient))
            .addSort("timestamp", SortDirection.DESCENDING);
    
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        if(useAnyMessage) {
          user = (String) entity.getProperty("user");
        }
        long timestamp = (long) entity.getProperty("timestamp");

        
        Message message = new Message(id, user, text, timestamp, recipient);

        messages.add(message);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    return messages;
  }

  /**
   * Gets messages posted by a specific user.
   * @param user String identifying a user
   * @return a list of messages posted by the user, or empty list if user has never posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getMessages(String user) {

    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user))
            .addSort("timestamp", SortDirection.DESCENDING);

    return getMessagesByQuery(query, user);
  }
  
  /**
   * Gets all messages posted.
   *
   * @return a list of messages currently stored, or empty list if there are none.
   * List is sorted by time descending.
   */
  public List<Message> getAllMessages() {
   
    Query query = new Query("Message")
      .addSort("timestamp", SortDirection.DESCENDING);
    
    return getMessagesByQuery(query, "");
  }

  /** 
   * Retrieves total number of messages for all users. 
   * @return messages the total number of messages for all users
   */
  public int getTotalMessageCount() {
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withLimit(1000));
  }

  /** Stores the User in Datastore. */
  public void storeUser(User user) {
    Entity userEntity = new Entity("User", user.getEmail());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("aboutMe", user.getAboutMe());
    datastore.put(userEntity);
  }
  
  /**
    * Returns the User owned by the email address, or
    * null if no matching User was found.
    */
  public User getUser(String email) {
  
    Query query = new Query("User")
      .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();
    if(userEntity == null) {
      return null;
    }
    
    String aboutMe = (String) userEntity.getProperty("aboutMe");
    User user = new User(email, aboutMe);
    
    return user;
  }

}
