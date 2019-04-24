package com.google.codeu.api;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

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


  private Set<String> yelpParams;
  private Map<String, String> priceMap;
  private Map<String, String> distMap;

  public YelpQuery() throws IOException {
    this.apiKey = "nHhrlF_fSJVeSago5RBBPT4Pm_My-QczgCQl7f1d0jMaicWX4eHG6RefrcuAn_HhXRp3sm-c1DR7M-iK7g1M7HCMklsQPQB4KJvh5w0qzv-T6dIDNifo_mxtam-YXHYx";//readFile(new File("api.key").getAbsolutePath());
    this.params = new HashMap<String, String>() {{ 
      put("location", "Chicago");
      put("term", "ice cream");
    }};

    this.yelpParams = new HashSet<String>() {{
      add("term"); add("location"); add("latitude");
      add("categories"); add("radius"); add("longitude");
      add("locale"); add("limit"); add("price");
      add("sort_by"); add("offset"); add("open_now");
      add("open_at"); add("attributes");
    }};
    this.priceMap = new HashMap<String, String>() {{
      put("cheap", "1"); put("averagecost", "2");
      put("expensive", "3"); put("veryexpensive", "4");
    }};

    this.distMap = new HashMap<String, String>() {{
      put("fivemi", "8046"); put("tenmi", "16093");
      put("twentyfivemi", "32186"); put("none", "40000");
    }};
  }
 
  public YelpQuery(HashMap<String, String> params) throws IOException {
    this.apiKey = "nHhrlF_fSJVeSago5RBBPT4Pm_My-QczgCQl7f1d0jMaicWX4eHG6RefrcuAn_HhXRp3sm-c1DR7M-iK7g1M7HCMklsQPQB4KJvh5w0qzv-T6dIDNifo_mxtam-YXHYx";//readFile(new File("api.key").getAbsolutePath());

    this.params = params;
    for(String name : params.keySet()) {
        System.out.println(name);
    }
    this.yelpParams = new HashSet<String>() {{
      add("term"); add("location"); add("latitude");
      add("categories"); add("radius"); add("longitude");
      add("locale"); add("limit"); add("price");
      add("sort_by"); add("offset"); add("open_now");
      add("open_at"); add("attributes");
    }};
    this.priceMap = new HashMap<String, String>() {{
      put("cheap", "1"); put("averagecost", "2");
      put("expensive", "3"); put("veryexpensive", "4");
    }};

    this.distMap = new HashMap<String, String>() {{
      put("fivemi", "8046"); put("tenmi", "16093");
      put("twentyfivemi", "32186"); put("none", "40000");
    }};
  }

  // just uses basic business search; https://www.yelp.com/developers/documentation/v3/business_search
  public String createQuery() throws URISyntaxException, MalformedURLException, IOException {
    URIBuilder builder = new URIBuilder();
    HttpParams params = new BasicHttpParams();
    HttpClient httpClient = new DefaultHttpClient(params);
    builder.setScheme("https").setHost("api.yelp.com").setPath("/v3/businesses/search");
    // Adding parameters to search string: TODO error checking. note, we need a location here in the parameters at least.
    for(Map.Entry<String, String> entry : this.params.entrySet()) {
      String currentKey = entry.getKey();
      String currentValue = entry.getValue();
      if(this.yelpParams.contains(currentKey)) {
        builder.setParameter(currentKey, currentValue);
      }
      else if(currentKey.equals("priceFilter")) {
        builder.setParameter("price", this.priceMap.get(currentValue));
      }
      else if(currentKey.equals("radiusFilter")) {
        builder.setParameter("radius", this.distMap.get(currentValue));
      }
      else if(currentKey.equals("filter")) {
        builder.setParameter("term", currentValue);
      }
      else if(currentKey.equals("city_val")) {
        builder.setParameter("location", currentValue);
      }
    }
    //builder.setParameter("location", "Chicago"); 
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


