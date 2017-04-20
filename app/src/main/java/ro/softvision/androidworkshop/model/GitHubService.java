package ro.softvision.androidworkshop.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by radu on 4/6/17.
 */

public interface GitHubService {
    @GET("/")
    Call<LoginData> checkAuth(@Header("Authorization") String auth);
    @GET("/user")
    Call<Profile> getUserProfile(@Header("Authorization") String auth);
    @GET("/user/repos")
    Call<List<Repository>> getUserRepositories(@Header("Authorization") String auth, @Query("affiliation") String affiliation);

    class Service {
        private static GitHubService sInstance;

        public synchronized static GitHubService Get() {
            if (sInstance == null) {
                sInstance = new Retrofit.Builder()
                        .baseUrl("https://api.github.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(GitHubService.class);
            }
            return sInstance;
        }
    }


}
