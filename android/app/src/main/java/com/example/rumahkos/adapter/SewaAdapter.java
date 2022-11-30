package com.example.rumahkos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumahkos.R;
import com.example.rumahkos.bindcallback.OnSewaBindCallBack;
import com.example.rumahkos.model.SewaModel;
import com.example.rumahkos.util.api.UtilsApi;

import java.util.ArrayList;
import java.util.Locale;

public class SewaAdapter extends RecyclerView.Adapter<SewaAdapter.SewaViewHolder> {
    public OnSewaBindCallBack onBindCallBack;
    private ArrayList<SewaModel> arrayList;

    public SewaAdapter(ArrayList<SewaModel> dataList) {
        this.arrayList = dataList;
    }

    @NonNull
    @Override
    public SewaAdapter.SewaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_sewa, parent, false);
        return new SewaAdapter.SewaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SewaAdapter.SewaViewHolder holder, int position) {


        holder.tvNamaKos.setText(arrayList.get(position).getNama_kos());
        holder.tvNamaKamar.setText(arrayList.get(position).getNama_kamar());
        holder.tvNamaPenyewa.setText(arrayList.get(position).getNama_penyewa());

        String tglJatuhTempo = arrayList.get(position).getTgl_jatuh_tempo();

        holder.tvTglJatuhTempo.setText(String.format(Locale.US, "%s", tglJatuhTempo));
        holder.tvJmlBulanTagih.setText(String.format("%s Bulan", arrayList.get(position).getBulan_sewa()));

        String totalBayar = UtilsApi.formatRupiah(arrayList.get(position).getHarga_total());
        holder.tvNominalBayar.setText(totalBayar);

        holder.btnQrCode.setOnClickListener(view -> {
            if (onBindCallBack != null) {
                onBindCallBack.OnSewaViewBind("btnQrCodeOnClick", holder, position);
            }
        });

        holder.btnStopSewa.setOnClickListener(view -> {
            if (onBindCallBack != null) {
                onBindCallBack.OnSewaViewBind("btnStopSewaOnClick", holder, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class SewaViewHolder extends RecyclerView.ViewHolder {

        TextView tvNamaKos;
        TextView tvNamaKamar;
        TextView tvNamaPenyewa;
        TextView tvTglJatuhTempo;
        TextView tvJmlBulanTagih;
        TextView tvNominalBayar;

        Button btnStopSewa;
        Button btnQrCode;


        public SewaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaKos = itemView.findViewById(R.id.sewa_tvNamaKos);
            tvNamaKamar = itemView.findViewById(R.id.sewa_tvNamaKamar);
            tvNamaPenyewa = itemView.findViewById(R.id.sewa_tvNamaPenyewa);
            tvTglJatuhTempo = itemView.findViewById(R.id.sewa_tvTglJatuhTempo);
            tvJmlBulanTagih = itemView.findViewById(R.id.sewa_tvJmlBulanTagih);
            tvNominalBayar = itemView.findViewById(R.id.sewa_tvNominalBayar);


            btnStopSewa = itemView.findViewById(R.id.sewa_btnStopSewa);
            btnQrCode = itemView.findViewById(R.id.sewa_btnQrCode);
        }
    }

}
