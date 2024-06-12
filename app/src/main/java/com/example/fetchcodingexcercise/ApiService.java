package com.example.fetchcodingexcercise;
import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;
public interface ApiService {
    @GET("hiring.json")
    abstract Call<List<Item>> getItems();
}
