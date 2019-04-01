package com.google.codeu.api;

import java.util.Map;
import java.util.HashMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.net.URL;
import java.net.URLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

import org.apache.http.client.utils.URIBuilder;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;


public class YelpQuery {
  private String apiKey;
  private Map<String, String> params;

  public YelpQuery() throws IOException {
    this.apiKey = "nHhrlF_fSJVeSago5RBBPT4Pm_My-QczgCQl7f1d0jMaicWX4eHG6RefrcuAn_HhXRp3sm-c1DR7M-iK7g1M7HCMklsQPQB4KJvh5w0qzv-T6dIDNifo_mxtam-YXHYx";//readFile("api.key");
    this.params = new HashMap<String, String>() {{ 
      put("location", "Chicago");
      put("term", "ice cream");
    }};
  }
 
  public YelpQuery(String apiKey, HashMap<String, String> params) throws IOException {
    this.apiKey = apiKey;
    this.params = params;
  }

  // just uses basic business search; https://www.yelp.com/developers/documentation/v3/business_search
  public URL createQuery() throws URISyntaxException, MalformedURLException, IOException {
    URIBuilder builder = new URIBuilder();
    HttpGet httppost = new HttpPost("https://api.yelp.com")
    builder.setScheme("https").setHost("api.yelp.com").setPath("/v3/businesses/search");
    // Adding parameters to search string: TODO error checking. note, we need a location here in the parameters at least.
    for(Map.Entry<String, String> entry : this.params.entrySet()) {
      builder.setParameter(entry.getKey(), entry.getValue());
    }
    httppost.setHeader("Authorization", "Bearer " + this.apiKey);
    URI uri = builder.build();
    return uri.toURL();
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


