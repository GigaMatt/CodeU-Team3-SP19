import java.util.HashMap;

import java.io.BufferedReader;
import java.io.FileReader;

import java.net.URI;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder

import com.google.gson.Gson;

public class YelpQuery {
  private String apiKey;
  
  public YelpQuery() {
    this.apiKey = readFile("api.key");
  }
  // just uses basic business search; https://www.yelp.com/developers/documentation/v3/business_search
  public HttpGet createQuery(Map<String, String> parameters) {
    URIBuilder builder = new URIBuilder();
    builder.setScheme("https").setHost("api.yelp.com").setPath("/v3/businesses/search");
    // Adding parameters to search string: TODO error checking. note, we need a location here in the parameters at least.
    for(Map.Entry<String, String> entry : parameters.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      builder.setParameter(key, value);
    }
    URI uri = builder.build();
    return new HttpGet(uri);
  }

  static string readFile(String path) {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    return reader.readLine(); // TODO: error handling
  }

}


