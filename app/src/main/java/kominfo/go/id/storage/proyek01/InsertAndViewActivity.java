package kominfo.go.id.storage.proyek01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class InsertAndViewActivity extends AppCompatActivity {
        private EditText etFileName, etCatatan;
        public static final int REQUEST_CODE_STORAGE = 100;
        private int eventID;
        private String tempCatatan = "";



        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_and_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            etFileName = findViewById(R.id.et_filename);
            etCatatan = findViewById(R.id.et_catatan);
            Button btSimpan = findViewById(R.id.bt_simpan);


            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

                if (getIntent().getExtras() != null) {
                    etFileName.setText(getIntent().getStringExtra("filename"));
                    setTitle("Ubah Catatan");
                } else
                    setTitle("Tambah Catatan");
            }

            eventID = 1;

            if (Build.VERSION.SDK_INT >= 23 && permittedToSave())
                readFile();
            else
                readFile();

            btSimpan.setOnClickListener(v -> {
                eventID = 2;

                if (!tempCatatan.equals(etCatatan.getText().toString())) {
                    if (Build.VERSION.SDK_INT >= 23 && permittedToSave())
                        showDialogSaveCatatan();
                    else
                        showDialogSaveCatatan();
                }
            });
        }
    private void showDialogSaveCatatan() {
        new AlertDialog.Builder(this)
                .setTitle("Simpan Catatan")
                .setMessage("Yakin menyimpan catatan ini?")
                .setPositiveButton("YES", (dialog, which) -> buatDanUbah())
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void buatDanUbah() {
        String state = Environment.getExternalStorageState();
        if(!Environment.MEDIA_MOUNTED.equals(state)){
            Toast.makeText(this, "External Storage not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        File parent = new File(getExStorePath());

        if (parent.exists()) {
            File file = new File(getExStorePath(), etFileName.getText().toString().trim());
            try {
                if (file.createNewFile()) {
                    FileOutputStream fos = new FileOutputStream(file);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.append(etCatatan.getText());
                    osw.flush();
                    osw.close();
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (parent.mkdir()) {
                File file = new File(getExStorePath(), etFileName.getText().toString().trim());
                FileOutputStream fos;
                try {
                    if (file.createNewFile()) {
                        fos = new FileOutputStream(file, false);
                        fos.write(etCatatan.getText().toString().getBytes());
                        fos.flush();
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        onBackPressed();
    }

    private String getExStorePath() {
        return getExternalFilesDir(null) + "/proyek01.kominfo";
    }

    private void readFile() {
        String state = Environment.getExternalStorageState();
        if(!Environment.MEDIA_MOUNTED.equals(state)){
            Toast.makeText(this, "external storage not available", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(getExStorePath(), etFileName.getText().toString().trim());

        if (file.exists()) {

            StringBuilder texts = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line = br.readLine();

                while (line != null) {
                    texts.append(line);
                    texts.append(System.getProperty("line.separator"));
                    line = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            tempCatatan = texts.toString();
            etCatatan.setText(texts.toString());
        }

    }

    private boolean permittedToSave() {
        if (Build.VERSION.SDK_INT >= 23)
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                return true;
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);

                return false;
            }

        else return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (eventID == 1)
                readFile();
            else
                showDialogSaveCatatan();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!tempCatatan.equals(etCatatan.getText().toString().trim()))
            showDialogSaveCatatan();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}

