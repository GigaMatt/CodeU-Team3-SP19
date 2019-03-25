import java.util.Map;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.net.URL;
import java.net.URLConnection;
import java.net.URI;

import org.apache.http.client.utils.URIBuilder;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class YelpQuery {
  private String apiKey;
  
  public YelpQuery() {
    this.apiKey = readFile("api.key");
  }
  // just uses basic business search; https://www.yelp.com/developers/documentation/v3/business_search
  public URL createQuery(Map<String, String> parameters) {
    URIBuilder builder = new URIBuilder();
    builder.setScheme("https").setHost("api.yelp.com").setPath("/v3/businesses/search");
    // Adding parameters to search string: TODO error checking. note, we need a location here in the parameters at least.
    for(Map.Entry<String, String> entry : parameters.entrySet()) {
      builder.setParameter(entry.getKey(), entry.getValue());
    }
    URI uri = builder.build();
    return uri.toURL();
  }

  public String getQueryResponse(URL url) {
    URLConnection connection = url.openConnection();
    InputStream stream = connection.getInputStream();
    String result = IOUtils.toString(stream, StandardCharsets.UTF_8);
    return new Gson().toJson(result); // get data from stream;
  }

  public static String readFile(String path) {
    BufferedReader reader = new BufferedReader(new FileReader(path));
    return reader.readLine(); // TODO: error handling
  }
  
  public static void main(String[] args) {
    YelpQuery query = new YelpQuery();
    Map<String, String> params = new HashMap<String, String>();
    params.put("location", "Chicago");
    params.put("term", "ice cream");
    URL url = query.createQuery(params);
    System.out.println(query.getQueryResponse(url));
  }

}


