package kr.ac.ajou.healingme;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ArrayList<Category> categories = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();
    private String category;
    private List<Posting> postings = new ArrayList<>();
    private SearchModel model;
    private LikeModel likeModel;

    private Spinner spinner;
    private TextView noResultText;
    private android.widget.SearchView searchText;
    private RecyclerView searchRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_bar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noResultText = findViewById(R.id.no_result_text);
        noResultText.setVisibility(View.GONE);

        categories = getIntent().getParcelableArrayListExtra("categorylist");
        System.out.println("aaaa"+categories.size());
        for (int i = 0; i < categories.size(); i++) {
            categoryNames.add(categories.get(i).category);
        }

        spinner = findViewById(R.id.category_spinner);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);

        spinner.setPrompt("카테고리 선택");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = (String) spinner.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("aaaaa암것두야");
            }
        });

        model = new SearchModel();

        searchRecyclerview = findViewById(R.id.search_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecyclerview.setLayoutManager(layoutManager);

        searchRecyclerview.setAdapter(new RecyclerView.Adapter<PostingHolder>() {
            @Override
            public PostingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.recycler_item_search, parent, false);
                return new PostingHolder(view);
            }

            @Override
            public void onBindViewHolder(PostingHolder holder, int position) {
                String title = model.getTitle(position);
                String timestamp = model.getTimestamp(position);
                holder.setText(title, timestamp);
            }

            @Override
            public int getItemCount() {
                return postings.size();
            }
        });

        searchText = findViewById(R.id.search_text);

        searchText.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                if (postings.size() == 0){
                    noResultText.setText("'"+s+"' 검색 결과가 존재하지 않습니다");
                    noResultText.setVisibility(View.VISIBLE);
                }

                model.setOnSearchListener(new OnSearchListener() {
                    @Override
                    public void onLoad(List<Posting> newpostings) {
                        postings = newpostings;
                        searchRecyclerview.getAdapter().notifyDataSetChanged();

                        if (postings.size() < 1) {
                            noResultText.setText("'"+s+"' 검색 결과가 존재하지 않습니다");
                            noResultText.setVisibility(View.VISIBLE);
                        } else {
                            noResultText.setVisibility(View.GONE);
                        }
                    }
                }, category, s);

//                noResultText.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                CategoryFragment categoryFragment = new CategoryFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.add(R.id.container_body, categoryFragment);
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();

                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private class PostingHolder extends RecyclerView.ViewHolder {
        private TextView searchTitleView;
        private TextView searchDateView;

        public PostingHolder(View itemView) {
            super(itemView);

            searchTitleView = itemView.findViewById(R.id.search_posting_title);
            searchDateView = itemView.findViewById(R.id.search_posting_day);
        }

        public void setText(String title, String timestamp) {
            searchTitleView.setText(title);
            searchDateView.setText(timestamp);
        }

    }


}
