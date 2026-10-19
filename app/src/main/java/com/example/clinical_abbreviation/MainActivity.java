package com.example.clinical_abbreviation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.clinical_abbreviation.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private AbbreviationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 开启边到边显示模式
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. 处理窗口缩进，防止状态栏与底部导航栏遮挡列表
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.appBarLayout.setPadding(0, systemBars.top, 0, 0);
            binding.rvMain.setPadding(0, 0, 0, systemBars.bottom);
            binding.rvAbbreviations.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        setupRecyclerView();
        loadData();
        setupSearch();
    }

    private void setupRecyclerView() {
        binding.rvMain.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAbbreviations.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {
        List<AbbreviationModel> abbreviationList = loadDataFromAsset();
        Log.i(TAG, "Loaded abbreviation count: " + abbreviationList.size());
        if (!abbreviationList.isEmpty()) {
            adapter = new AbbreviationAdapter(abbreviationList);

            adapter.setOnItemClickListener(new AbbreviationAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(AbbreviationModel item) {
                    copyToClipboard(item);
                }

                @Override
                public void onItemLongClick(AbbreviationModel item) {
                    copyToClipboard(item);
                }
            });

            adapter.setOnFilterResultListener(count -> {
                boolean isEmpty = (count == 0);
                binding.tvEmptyMain.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                binding.tvEmptySearch.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            });

            binding.rvMain.setAdapter(adapter);
            binding.rvAbbreviations.setAdapter(adapter);

            // 在搜索栏副标题或提示中显示总条目数
            binding.searchBar.setHint(getString(R.string.item_count_format, abbreviationList.size()));
        } else {
            Log.e(TAG, "Warning: abbreviation list is empty!");
            binding.tvEmptyMain.setVisibility(View.VISIBLE);
            binding.searchBar.setHint(getString(R.string.item_count_format, 0));
        }
    }

    private void copyToClipboard(AbbreviationModel item) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            String content = item.toString();
            ClipData clip = ClipData.newPlainText("Clinical Abbreviation", content);
            clipboard.setPrimaryClip(clip);
            Snackbar.make(binding.getRoot(), getString(R.string.copied_to_clipboard, content), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setupSearch() {
        // 当用户在搜索框按下搜索键时
        binding.searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            String text = binding.searchView.getText().toString();
            binding.searchBar.setText(text);
            if (adapter != null) {
                adapter.filter(text);
            }
            binding.searchView.hide();
            return false;
        });

        // 实时过滤搜索结果
        binding.searchView.getEditText().addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s != null ? s.toString() : "");
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        binding.searchView.addTransitionListener((searchView, previousState, newState) -> {
            if (newState == com.google.android.material.search.SearchView.TransitionState.HIDDEN) {
                // 当搜索框关闭时，如果是空的则重置列表
                if (binding.searchView.getText().toString().isEmpty()) {
                    if (adapter != null) {
                        adapter.filter("");
                    }
                }
            }
        });
    }

    private List<AbbreviationModel> loadDataFromAsset() {
        try (InputStream is = getAssets().open("abbreviations.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            Gson gson = new Gson();
            AbbreviationModel[] array = gson.fromJson(reader, AbbreviationModel[].class);
            if (array != null && array.length > 0) {
                return new ArrayList<>(Arrays.asList(array));
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error loading abbreviations from assets", ex);
        }
        return new ArrayList<>();
    }
}