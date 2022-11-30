package com.example.rumahkos.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormBayarFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    int id;
    String nama_kos;
    String nama_kamar;
    String nama_penyewa;
    String tgl_jatuh_tempo;
    String bulan_sewa;
    Double nominal;
    @BindView(R.id.bayar_tvRumahKos)
    TextView bayarTvRumahKos;
    @BindView(R.id.bayar_etNamaKamar)
    EditText bayarEtNamaKamar;
    @BindView(R.id.bayar_tvNamaPenyewa)
    TextView bayarTvNamaPenyewa;
    @BindView(R.id.bayar_tvTanggalJatuhTempo)
    TextView bayarTvTanggalJatuhTempo;
    @BindView(R.id.bayar_etJmlBulanTagih)
    EditText bayarEtJmlBulanTagih;
    @BindView(R.id.bayar_etNominal)
    EditText bayarEtNominal;
    @BindView(R.id.bayar_btnUploadBukti)
    Button bayarBtnUploadBukti;
    @BindView(R.id.bayar_imgViewBukti)
    ImageView bayarImgViewBukti;
    @BindView(R.id.bayar_btnStopSewa)
    Button bayarBtnStopSewa;
    @BindView(R.id.bayar_btnBayar)
    Button bayarBtnBayar;

    /*
    *   bundle.putInt("kos_id", modelList.get(position).getId());
        bundle.putString("nama_kos",modelList.get(position).getNama_kos());
        bundle.putString("nama_kamar",modelList.get(position).getNama_kamar());
        bundle.putString("nama_penyewa",modelList.get(position).getNama_penyewa());

        String tglJatuhTempo = modelList.get(position).getTgl_jatuh_tempo();
        int hari_jatuh_tempo = modelList.get(position).getHari_jatuh_tempo();

        String status_hari_jatuh_tempo = hari_jatuh_tempo > 0 ? "+":"-";
        bundle.putString("tgl_jatuh_tempo",String.format(Locale.US,"%s ( %s %d hari )",tglJatuhTempo,status_hari_jatuh_tempo,hari_jatuh_tempo));

        bundle.putString("bulan_sewa",modelList.get(position).getBulan_sewa());
        bundle.putDouble("nominal",modelList.get(position).getHarga_total());
    *
    *
    * */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void EnableDisableEditText(boolean isEnabled, EditText editText) {
        editText.setFocusable(isEnabled);
        editText.setFocusableInTouchMode(isEnabled) ;
        editText.setClickable(isEnabled);
        editText.setLongClickable(isEnabled);
        editText.setCursorVisible(isEnabled) ;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_form_bayar, container, false);

        ButterKnife.bind(this, root);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);

        Bundle arguments = getArguments();
        if (arguments == null)
            Toast.makeText(getActivity(), "Arguments is NULL", Toast.LENGTH_LONG).show();
        else {
            id = getArguments().getInt("id", 0);
            nama_kos = getArguments().getString("nama_kos", "");
            nama_kamar = getArguments().getString("nama_kamar", "");
            nama_penyewa = getArguments().getString("nama_penyewa", "");

            tgl_jatuh_tempo = getArguments().getString("tgl_jatuh_tempo", "");
            bulan_sewa = getArguments().getString("bulan_sewa", "");
            nominal = getArguments().getDouble("nominal", 0);

        }

        bayarTvRumahKos.setText(nama_kos);
        bayarEtNamaKamar.setText(nama_kamar);
        bayarTvNamaPenyewa.setText(nama_penyewa);

        bayarTvTanggalJatuhTempo.setText(tgl_jatuh_tempo);
        bayarEtJmlBulanTagih.setText(bulan_sewa);
        bayarEtNominal.setText(String.format(Locale.US, "%.0f",nominal));

        if(spManager.getLevel().equals("penyewa")){

            EnableDisableEditText(false,bayarEtJmlBulanTagih);
            EnableDisableEditText(false,bayarEtNominal);
            EnableDisableEditText(false,bayarEtNamaKamar);
        }

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Booking Kamar Kos");

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.hide();

            floatingActionButton.setOnClickListener(view -> {


            });
        }

        return root;
    }


    @OnClick(R.id.bayar_btnUploadBukti)
    public void onBayarBtnUploadBuktiClicked() {
        Toasty.error(mContext, "Belum Diimplementasikan !", Toasty.LENGTH_LONG).show();
    }

    @OnClick(R.id.bayar_btnStopSewa)
    public void onBayarBtnStopSewaClicked() {
        Toasty.error(mContext, "Belum Diimplementasikan !", Toasty.LENGTH_LONG).show();
    }

    @OnClick(R.id.bayar_btnBayar)
    public void onBayarBtnBayarClicked() {

        ProgressDialog loading;

        loading = ProgressDialog.show(mContext, null, "Mengirimkan data...", true, false);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + spManager.getAccessToken());
        headers.put("Accept", "application/json");

        mBaseApiService.bayar(
            headers,
            this.id,
            bayarEtNamaKamar.getText().toString(),
            bayarEtJmlBulanTagih.getText().toString(),
            bayarEtNominal.getText().toString()
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String error_message = jsonObject.getString("error_msg");
                        Toasty.success(mContext, error_message, Toasty.LENGTH_LONG).show();

                        TagihanListFragment fragment = new TagihanListFragment();

                        getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment, TagihanListFragment.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();

                    } catch (JSONException | IOException e) {
                        Log.i("LOGIN", "Login GAGAL " + e.getMessage());
                    }

                } else {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("debug", "onFailure: ERROR > " + t.toString());
                loading.dismiss();
            }
        });

    }
}
