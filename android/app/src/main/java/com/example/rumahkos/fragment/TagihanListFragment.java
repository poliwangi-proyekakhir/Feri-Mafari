package com.example.rumahkos.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.adapter.KosAdapter;
import com.example.rumahkos.adapter.TagihanAdapter;
import com.example.rumahkos.model.KosModel;
import com.example.rumahkos.model.TagihanModel;
import com.example.rumahkos.modellist.KosModelList;
import com.example.rumahkos.modellist.TagihanModelList;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagihanListFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    ProgressDialog loading;

    private TagihanAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tagihan_list, container, false);
        swipe = root.findViewById(R.id.tagihan_swipeContainer);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);

        androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Data Tagihan");

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

        mBaseApiService.getTagihan(headers).enqueue(new Callback<TagihanModelList>() {
            @Override
            public void onResponse(@NonNull Call<TagihanModelList> call, @NonNull Response<TagihanModelList> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    generateRecyclerList(Objects.requireNonNull(response.body()).getArrayList());
                } else {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<TagihanModelList> call, Throwable t) {
                Toasty.error(mContext, "Ada kesalahan! :: " + t.getMessage(), Toast.LENGTH_LONG, true).show();
                loading.dismiss();
            }
        });


    }

    private void generateRecyclerList(ArrayList<TagihanModel> modelList) {

        recyclerView = requireView().findViewById(R.id.tagihan_recyclerList);
        adapter = new TagihanAdapter(modelList);

        adapter.onBindCallBack = (jenis, viewHolder, position) -> {

            if ("btnBayarOnClick".equals(jenis)) {

                   if(modelList.get(position).getKode_status_bayar().equals("BBU")){
                       Toasty.warning(mContext, "Silahkan menunggu validasi pembayaran!", Toast.LENGTH_LONG, true).show();

                   }else{
                       Bundle bundle = new Bundle();
                       bundle.putInt("id", modelList.get(position).getId());
                       bundle.putString("nama_kos",modelList.get(position).getNama_kos());
                       bundle.putString("nama_kamar",modelList.get(position).getNama_kamar());
                       bundle.putString("nama_penyewa",modelList.get(position).getNama_penyewa());

                       String tglJatuhTempo = modelList.get(position).getTgl_jatuh_tempo();
                       int hari_jatuh_tempo = modelList.get(position).getHari_jatuh_tempo();

                       String status_hari_jatuh_tempo = hari_jatuh_tempo > 0 ? "+":"-";
                       bundle.putString("tgl_jatuh_tempo",String.format(Locale.US,"%s ( %s %d hari )",tglJatuhTempo,status_hari_jatuh_tempo,hari_jatuh_tempo));

                       bundle.putString("bulan_sewa",modelList.get(position).getBulan_sewa());
                       bundle.putDouble("nominal",modelList.get(position).getHarga_total());


                       FormBayarFragment fragment = new FormBayarFragment();
                       fragment.setArguments(bundle);
                       AppCompatActivity activity = (AppCompatActivity) getView().getContext();

                       activity.getSupportFragmentManager()
                           .beginTransaction()
                           .replace(R.id.nav_host_fragment, fragment, FormBayarFragment.class.getSimpleName())
                           .addToBackStack(null)
                           .commit();
                   }


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


}
