package com.google.codeu.api;

import java.util.Map;
import java.util.HashMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.File;


import java.net.URL;
import java.net.URLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

import java.io.ByteArrayOutputStream;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.client.utils.URIBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.params.HttpParams;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;

import com.google.gson.Gson;


public class YelpQuery {
  private String apiKey;
  private Map<String, String> params;

  public YelpQuery() throws IOException {
    this.apiKey = readFile(new File("api.key").getAbsolutePath());
    this.params = new HashMap<String, String>() {{ 
      put("location", "Chicago");
      put("term", "ice cream");
    }};
  }
 
  public YelpQuery(HashMap<String, String> params) throws IOException {
    this.apiKey = readFile(new File("api.key").getAbsolutePath());

    this.params = params;
  }

  // just uses basic business search; https://www.yelp.com/developers/documentation/v3/business_search
  public String createQuery() throws URISyntaxException, MalformedURLException, IOException {
    URIBuilder builder = new URIBuilder();
    HttpParams params = new BasicHttpParams();
    HttpClient httpClient = new DefaultHttpClient(params);
    builder.setScheme("https").setHost("api.yelp.com").setPath("/v3/businesses/search");
    // Adding parameters to search string: TODO error checking. note, we need a location here in the parameters at least.
    for(Map.Entry<String, String> entry : this.params.entrySet()) {
      builder.setParameter(entry.getKey(), entry.getValue());
    }
    
    HttpGet httpget = new HttpGet(builder.build());
    httpget.setHeader("Authorization", "Bearer " + this.apiKey);
    httpget.setHeader("Accept", "application/json");
    HttpResponse httpresp = httpClient.execute(httpget);
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    httpresp.getEntity().writeTo(outStream);
    return outStream.toString("UTF-8");//uri.toURL();
  }

  public String getQueryResponse(URL url) throws IOException, FileNotFoundException {
    URLConnection connection = url.openConnection();
    InputStream stream = connection.getInputStream();
    String result = IOUtils.toString(stream, StandardCharsets.UTF_8);
    return new Gson().toJson(result); // get data from stream;
  }

  public static String readFile(String path) throws IOException, FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + path));
    return reader.readLine(); // TODO: error handling
  }
  
}


