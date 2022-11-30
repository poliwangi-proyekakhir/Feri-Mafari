package com.example.rumahkos;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rumahkos.fragment.KosListFragment;
import com.example.rumahkos.fragment.ScannerFragment;
import com.example.rumahkos.model.GeneralModel;
import com.example.rumahkos.model.WilayahModel;
import com.example.rumahkos.modellist.WilayahModelList;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public FloatingActionButton fab;

    public Boolean getmStateActionFilter() {
        return mStateActionFilter;
    }

    public void setmStateActionFilter(Boolean mStateActionFilter) {
        this.mStateActionFilter = mStateActionFilter;
    }

    private Boolean mStateActionFilter;

    SPManager spManager;
    BaseApiService mBaseApiService;
    Context mContext;

    private AppBarConfiguration mAppBarConfiguration;
    private String selected_kecamatan;
    private String selected_kelurahan;
    private String selected_max_harga;
    private String selected_jenisKos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);

        spManager = new SPManager(this);

        Log.e("search Filter", spManager.getKosSearchFilter());

        if (getIntent().hasExtra("redirect")) {
            Log.e("REDIRECT", "REDIRECT");
        } else {
            requestMultiplePermissions();
            mContext = this;
            mBaseApiService = UtilsApi.getAPIService();
            CheckTypesTask loading = new CheckTypesTask();
            loading.execute();
        }

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        ImageView navFoto = headerView.findViewById(R.id.navheader_imageView);
        Picasso.get().invalidate(UtilsApi.BASE_URL + "uploads/" + spManager.getFoto());
        Picasso.get().load(UtilsApi.BASE_URL + "uploads/" + spManager.getFoto()).into(navFoto);

        TextView navUsername = headerView.findViewById(R.id.navheader_tvName);
        navUsername.setText(spManager.getNama());

        TextView navUserEmail = headerView.findViewById(R.id.navheader_tvEmail);
        navUserEmail.setText(spManager.getEmail());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
            .setDrawerLayout(drawer)
            .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //menampilkan group menu berdasarkan level user
        Menu nav_Menu = navigationView.getMenu();

        nav_Menu.findItem(R.id.nav_group_pemilik).setVisible(false);
        nav_Menu.findItem(R.id.nav_group_penyewa).setVisible(false);

        if(spManager.getLevel().equals("pemilik")){
            nav_Menu.findItem(R.id.nav_group_pemilik).setVisible(true);
        }else{
            nav_Menu.findItem(R.id.nav_group_penyewa).setVisible(true);
        }
    }

    public FloatingActionButton getFloatingActionButton() {
        return fab;
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(getApplicationContext(), "Selamat datang kembali!", Toast.LENGTH_SHORT).show();
                    }

                    if (report.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).
            withErrorListener(error -> Toast.makeText(getApplicationContext(), String.format("Some Error! %s", error.toString()), Toast.LENGTH_SHORT).show())
            .onSameThread()
            .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem action_filter = menu.findItem(R.id.action_filter);
        action_filter.setVisible(mStateActionFilter);

        MenuItem action_add_kos = menu.findItem(R.id.action_add_kos);
        action_add_kos.setVisible(spManager.getLevel().equals("pemilik"));

        MenuItem action_scanner = menu.findItem(R.id.action_scanner);
        action_scanner.setVisible(spManager.getLevel().equals("pemilik"));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {

            String[] searchFilter = spManager.getKosSearchFilter().split(Pattern.quote("|"));
            String filterKecamatan = searchFilter[0];
            String filterKelurahan = searchFilter[1];
            String filterJenisKos = searchFilter[2];
            String filterHargaMax = searchFilter[3];

            Log.e("searchfilter", filterKecamatan);

            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.fragment_filter_search, null);

            Spinner spKecamatan, spKelurahan, spJenis;
            EditText etHargaMax;

            dialog.setView(dialogView);
            dialog.setCancelable(true);
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setTitle("Filter Pencarian");

            spKecamatan = (Spinner) dialogView.findViewById(R.id.filter_spKecamatan);
            spKelurahan = (Spinner) dialogView.findViewById(R.id.filter_spKelurahan);
            spJenis = (Spinner) dialogView.findViewById(R.id.filter_spJenis);
            etHargaMax = (EditText) dialogView.findViewById(R.id.filter_etHargaMax);

            etHargaMax.setText(filterHargaMax.equals("All") ? "" : filterHargaMax);


            //fill data jenis
            ArrayList<GeneralModel> jenisKosList = new ArrayList<>();
            jenisKosList.add(new GeneralModel("All", "Semua"));
            jenisKosList.add(new GeneralModel("PTR", "KOS PUTRA"));
            jenisKosList.add(new GeneralModel("PUT", "KOS PUTRI"));
            jenisKosList.add(new GeneralModel("CMP", "KOS CAMPUR"));

            ArrayAdapter<GeneralModel> jenisKosAdapter = new ArrayAdapter<GeneralModel>(mContext, android.R.layout.simple_spinner_dropdown_item, jenisKosList);
            spJenis.setAdapter(jenisKosAdapter);
            spJenis.setSelection(jenisKosAdapter.getPosition(new GeneralModel(filterJenisKos, "")));

            spJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    GeneralModel jenis = (GeneralModel) adapterView.getSelectedItem();
                    selected_jenisKos = jenis.getKode();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    WilayahModel kecamatan = (WilayahModel) adapterView.getSelectedItem();
                    selected_kecamatan = kecamatan.getKode();
                    //load kelurahan data berdasarkan kecamatan yang dipilih
                    new getKelurahan(spKelurahan, kecamatan.getKode(), filterKelurahan).execute();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //load kecamatan data
            new getKecamatan(spKecamatan, filterKecamatan).execute();

            spKelurahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    WilayahModel kelurahan = (WilayahModel) adapterView.getSelectedItem();
                    selected_kelurahan = kelurahan.getKode();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int i) {
                    String filter = selected_kecamatan + "|" + selected_kelurahan + "|" + selected_jenisKos + "|" + (etHargaMax.getText().toString().equals("") ? "All" : etHargaMax.getText().toString());
                    spManager.saveString(spManager.KOS_SEARCH_FILTER, filter);

                    Bundle bundle = new Bundle();
                    // bundle.putInt("put something key", someting value);
                    KosListFragment fragment = new KosListFragment();
                    fragment.setArguments(bundle);

                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();


                    dialog.dismiss();
                }
            });

            dialog.setNegativeButton("RESET", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    spManager.defaultSearchFilter();

                    Bundle bundle = new Bundle();
                    // bundle.putInt("put something key", someting value);
                    KosListFragment fragment = new KosListFragment();
                    fragment.setArguments(bundle);

                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();

                    dialog.dismiss();
                }
            });

            dialog.show();

            return true;
        }

        if (id == R.id.action_scanner) {
            //Toast.makeText(this, "scanner", Toast.LENGTH_LONG).show();

            spManager.defaultSearchFilter();

            Bundle bundle = new Bundle();
            // bundle.putInt("put something key", someting value);
            ScannerFragment fragment = new ScannerFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment, ScannerFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();


            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
            || super.onSupportNavigateUp();
    }

    public class CheckTypesTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);
        String typeStatus;

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            asyncDialog.setMessage(getString(R.string.mengambil_data));
            //show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            //don't touch dialog here it'll break the application
            //do some lengthy stuff like calling login webservice

            FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(MainActivity.class.getSimpleName(), "getInstanceId failed", task.getException());
                        return;
                    }
                    String token = Objects.requireNonNull(task.getResult()).getToken();
                    Log.d(MainActivity.class.getSimpleName(), token);

                    sendRegistrationToServer(token);
                });

            return null;
        }

        private void sendRegistrationToServer(String token_id) {

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
            headers.put("Accept", "application/json");

            mBaseApiService.sendFirebaseToken(headers, token_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getString("error").equals("true")) {
                                    String error_message = jsonObject.getString("error_msg");
                                    Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                    }
                });
        }

        @Override
        protected void onPostExecute(Void result) {
            //hide the dialog
            asyncDialog.dismiss();

            super.onPostExecute(result);
        }
    }

    private class getKecamatan extends AsyncTask<Void, Void, Void> {

        Spinner spKecamatan;
        String defaultKode;

        getKecamatan(Spinner spKecamatan, String defaultKode) {
            this.spKecamatan = spKecamatan;
            this.defaultKode = defaultKode;
            Log.e("Kecamatan Default", defaultKode);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
            headers.put("Accept", "application/json");

            mBaseApiService.getKecamatan(headers)
                .enqueue(new Callback<WilayahModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<WilayahModelList> call, @NotNull Response<WilayahModelList> response) {
                        if (response.isSuccessful()) {
                            //loading.dismiss();
                            WilayahModel defaultAll = new WilayahModel("00.00.00", "Semua");
                            ArrayList<WilayahModel> arrKecamatan = new ArrayList<>();

                            arrKecamatan.add(defaultAll);
                            arrKecamatan.addAll(Objects.requireNonNull(response.body()).getArrayList());

                            ArrayAdapter<WilayahModel> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, arrKecamatan);
                            spKecamatan.setAdapter(adapter);
                            spKecamatan.setSelection(adapter.getPosition(new WilayahModel(defaultKode, "")));

                        } else {
                            // loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<WilayahModelList> call, Throwable t) {
                        Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        //loading.dismiss();
                    }
                });

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class getKelurahan extends AsyncTask<Void, Void, Void> {

        private String kode_kecamatan;
        private Spinner spKelurahan;
        private String defaultKode;

        getKelurahan(Spinner spKelurahan, String kode_kecamatan, String defaultKode) {
            this.kode_kecamatan = kode_kecamatan;
            this.spKelurahan = spKelurahan;
            this.defaultKode = defaultKode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loading = ProgressDialog.show(mContext, null, "Mengambil data kelurahan...", true, false);
        }

        @Override
        protected Void doInBackground(Void... voids) {


            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
            headers.put("Accept", "application/json");

            mBaseApiService.getKelurahan(headers, this.kode_kecamatan)
                .enqueue(new Callback<WilayahModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<WilayahModelList> call, @NotNull Response<WilayahModelList> response) {
                        if (response.isSuccessful()) {
                            //loading.dismiss();

                            //ArrayList<WilayahModel> arrkelurahan = Objects.requireNonNull(response.body()).getArrayList();
                            WilayahModel defaultAll = new WilayahModel("00.00.00.0000", "Semua");

                            ArrayList<WilayahModel> arrKelurahan = new ArrayList<>();

                            arrKelurahan.add(defaultAll);
                            arrKelurahan.addAll(Objects.requireNonNull(response.body()).getArrayList());

                            ArrayAdapter<WilayahModel> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, arrKelurahan);
                            spKelurahan.setAdapter(adapter);
                            spKelurahan.setSelection(adapter.getPosition(new WilayahModel(defaultKode, "")));

                        } else {
                            //loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<WilayahModelList> call, Throwable t) {
                        Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        //loading.dismiss();
                    }
                });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}
