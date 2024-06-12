package com.example.fetchcodingexcercise;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList = new ArrayList<>();

    String url = "https://fetch-hiring.s3.amazonaws.com/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Item>> call = apiService.getItems();

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Item> data = response.body();
                    // Filter out items where name is blank or null
                    List<Item> filteredList = data.stream()
                            .filter(item -> item.getName() != null && !item.getName().isEmpty())
                            .collect(Collectors.toList());

                    // Sort items first by listId, then by name
                    filteredList.sort(Comparator.comparingInt(Item::getListId)
                            .thenComparing(Item::getName, String.CASE_INSENSITIVE_ORDER));

                    // Group items by listId
                    Map<Integer, List<Item>> groupedItems = filteredList.stream()
                            .collect(Collectors.groupingBy(Item::getListId));

                    // Flatten the grouped items into a single list
                    List<Item> finalList = new ArrayList<>();
                    groupedItems.values().forEach(finalList::addAll);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            itemAdapter = new ItemAdapter(finalList);
                            recyclerView.setAdapter(itemAdapter);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Unable to fetch data", Toast.LENGTH_LONG).show();
            }
        });
    }
}