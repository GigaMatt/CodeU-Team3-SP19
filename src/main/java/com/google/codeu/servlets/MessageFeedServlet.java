package com.google.codeu.servlets;

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

import java.io.IOException;
import java.util.List;
import java.net.URISyntaxException; 
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import com.google.codeu.api.YelpQuery;


/**
 * Handles fetching all messages for the public feed.
 */
@WebServlet("/resultsjava")
public class MessageFeedServlet extends HttpServlet{
  
  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /*
   * Responds with a JSON representation of Message data for all users.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {

    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8"); 
    List<Message> messages = datastore.getAllMessages();
    Gson gson = new Gson();
    //String term = request.getParameter("filter");
    //String location = request.getParameter("city_val");
    HashMap<String, String> paramMap = new HashMap<String, String>();
    PrintWriter out = response.getWriter();
    for(Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
        String currentName = entry.getKey();
        paramMap.put(currentName, entry.getValue()[0]);
    }
    YelpQuery query = new YelpQuery(paramMap);
    String json = "";
    try {
      json = query.createQuery();
    } catch (URISyntaxException e) { response.getOutputStream().println("aaa");}
    
    out.println(json);
   }
}
