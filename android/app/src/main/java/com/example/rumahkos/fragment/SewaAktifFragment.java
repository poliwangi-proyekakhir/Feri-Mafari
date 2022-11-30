package com.example.rumahkos.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.WebAppInterface;
import com.example.rumahkos.adapter.SewaAdapter;
import com.example.rumahkos.model.SewaModel;
import com.example.rumahkos.modellist.SewaModelList;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SewaAktifFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    ProgressDialog loading;

    private SewaAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sewa_list, container, false);
        swipe = root.findViewById(R.id.sewa_swipeContainer);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);

        androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Data Sewa Aktif");

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.hide();
        }

        //recyclerView = root.findViewById(R.id.kos_recyclerList);

        swipe.setOnRefreshListener(() -> {
            swipe.setRefreshing(false);
            loadData();
        });

        loadData();

        //tampilkan search filter
        ((MainActivity) getActivity()).setmStateActionFilter(false);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        return root;
    }

    private void loadData() {

        loading = ProgressDialog.show(mContext, null, "Mengambil data ...", true, false);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + spManager.getAccessToken());
        headers.put("Accept", "application/json");

        mBaseApiService.getSewa(headers).enqueue(new Callback<SewaModelList>() {
            @Override
            public void onResponse(@NonNull Call<SewaModelList> call, @NonNull Response<SewaModelList> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    generateRecyclerList(Objects.requireNonNull(response.body()).getArrayList());
                } else {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SewaModelList> call, Throwable t) {
                Toasty.error(mContext, "Ada kesalahan! :: " + t.getMessage(), Toast.LENGTH_LONG, true).show();
                loading.dismiss();
            }
        });


    }

    private void generateRecyclerList(ArrayList<SewaModel> modelList) {

        recyclerView = requireView().findViewById(R.id.sewa_recyclerList);
        adapter = new SewaAdapter(modelList);

        adapter.onBindCallBack = (jenis, viewHolder, position) -> {

            if ("btnStopSewaOnClick".equals(jenis)) {
                Toasty.error(mContext, "Belum Diimplementasikan !", Toasty.LENGTH_LONG).show();
            }else{
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                LayoutInflater inflater = getLayoutInflater();
                View root = inflater.inflate(R.layout.fragment_qrcode, null);
                dialog.setView(root);
                dialog.setCancelable(true);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle("QRCode");

                SwipeRefreshLayout swipe;
                swipe = root.findViewById(R.id.qrcode_swipeContainer);

                swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadWebViewQrCode(modelList.get(position).getKos_id(), root, swipe);
                        swipe.setRefreshing(false);
                    }
                });

                loadWebViewQrCode(modelList.get(position).getKos_id(), root, swipe);
                swipe.setEnabled(false);

                dialog.setNegativeButton("TUTUP", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

        };

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void loadWebViewQrCode(int kos_id, View root, SwipeRefreshLayout swipe) {

        WebView mWebView = root.findViewById(R.id.qrcode_webview);
        //mWebView.addJavascriptInterface(new WebAppInterface(mContext, kos_id), "Android");
        mWebView.loadUrl(UtilsApi.BASE_URL_WEBVIEW + "get-qrcode/" + kos_id);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        swipe.setRefreshing(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                mWebView.loadUrl("file:///android_asset/error.html");
            }

            public void onPageFinished(WebView view, String url) {
                //ketika loading selesai, ison loading akan hilang
                swipe.setRefreshing(false);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //loading akan jalan lagi ketika masuk link lain
                // dan akan berhenti saat loading selesai
                if (100 == mWebView.getProgress()) {
                    swipe.setRefreshing(false);
                } else {
                    swipe.setRefreshing(true);
                }
            }
        });

    }

}
