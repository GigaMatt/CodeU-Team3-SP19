/**
 * SERVELET FOR STATS PAGE
 * Responds with a hard-coded message for testing purposes.
 */
package com.google.codeu.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

//Modified to output message data as JSON
import com.google.codeu.data.Datastore;
import com.google.gson.JsonObject;

 //Handles fetching site statistics
@WebServlet("/stats")
public class StatsPageServlet extends HttpServlet{

  private Datastore datastore;

  //Automatically called when servlet is first created
  @Override
  public void init(){
    datastore = new Datastore();  //Creates + stores DS instance
  }

  // Responds with site stats in JSON
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
    int message_count = 0;
    
    response.setContentType("application/json");
    message_count = datastore.getTotalMessageCount();
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("messageCount", message_count);

    response.getOutputStream().println(jsonObject.toString());
 }
}