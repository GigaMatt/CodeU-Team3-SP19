package com.google.codeu.servlets;

import java.util.Map;
import java.util.HashMap;

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
@WebServlet("/feed")
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
    String term = request.getParameter("term");
    String location = request.getParameter("location");
    HashMap<String, String> param_map = new HashMap<String, String>() {{
      put("term", term);
      put("location", location);
    }};
    YelpQuery query = new YelpQuery("nHhrlF_fSJVeSago5RBBPT4Pm_My-QczgCQl7f1d0jMaicWX4eHG6RefrcuAn_HhXRp3sm-c1DR7M-iK7g1M7HCMklsQPQB4KJvh5w0qzv-T6dIDNifo_mxtam-YXHYx",param_map);
    String json = "";
    try {
      json = query.createQuery();//gson.toJson(messages);
    } catch (URISyntaxException e) { response.getOutputStream().println("aaa");}
    //response.getOutputStream().println(json);
    PrintWriter out = response.getWriter();
    out.println(json);
   }
}
