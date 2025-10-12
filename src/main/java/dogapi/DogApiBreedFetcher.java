package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    private static final String BASE_URL = "https://dog.ceo/api/breed";

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {

        final Request request = new Request.Builder()
                .url(String.format("%s/%s/list", BASE_URL, breed))
                .build();

        List<String> subBreeds = new ArrayList<>();

        try {
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            if (!responseBody.getString("status").equals("error")) {
                // getJSONArray retrives the array associated with that given key, so will give all dogs in this case
                JSONArray subBreedsArr = responseBody.getJSONArray("message");
                for (int i = 0; i < subBreedsArr.length(); i++) {
                    subBreeds.add(subBreedsArr.getString(i));
                }
            }
            else {
                throw new BreedFetcher.BreedNotFoundException(breed);
            }
        }
        catch (IOException | JSONException event) {
            throw new BreedFetcher.BreedNotFoundException(breed);
        }

        return subBreeds;
    }
}