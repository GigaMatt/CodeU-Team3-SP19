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

import com.google.appengine.api.datastore.FetchOptions;   //needed for function w/ total message count 
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/** Provides access to the data stored in Datastore. */
public class Datastore{

  private DatastoreService datastore;

  public Datastore(){
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message){
    Entity message_entity = new Entity("Message", message.getId().toString());
    message_entity.setProperty("user", message.getUser());
    message_entity.setProperty("text", message.getText());
    message_entity.setProperty("timestamp", message.getTimestamp());
    message_entity.setProperty("recipient", message.getRecipient());

    datastore.put(message_entity);
  }

  /**
   * Gets messages posted by a specific user.
   * @param user String identifying the user
   * @return messages a list of messages posted by the user.
   */
  public List<Message> getMessages(String user){
    List<Message> messages = new ArrayList<>();

    Query query = new Query("Message").setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user)).addSort("timestamp", SortDirection.DESCENDING);
    
    PreparedQuery results = datastore.prepare(query);
    for(Entity entity : results.asIterable()){
      try{
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String message_text = (String)entity.getProperty("text");
        long timestamp = (long)entity.getProperty("timestamp");
        String recipient = (String)entity.getProperty("recipient");
        Message user_message = new Message(id, user, message_text, timestamp, recipient);
        messages.add(user_message);
      }
      catch (Exception e){
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    return messages;
  }

  /** 
   * Retrieves total number of messages ∀ users. 
   * @return messages the total number of messages ∀ users
   */
  public int getTotalMessageCount(){
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withLimit(1000));
    /**
     * @MATT
     * When working on pull reuqests, emails will be sent, and doing a push will send a notification to Drew about the pull request update
     * With errros, run maven from cmd line and show error from there
     */
  }
}