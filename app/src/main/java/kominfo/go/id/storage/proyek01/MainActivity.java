package kominfo.go.id.storage.proyek01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_STORAGE = 100;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            setTitle("Proyek1");

        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent(MainActivity.this, InsertAndViewActivity.class);
            Map<String, Object> item = (Map<String, Object>) parent.getAdapter().getItem(position);
            intent.putExtra("filename", item.get("name").toString());
            startActivity(intent);
        });
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Map<String, Object> item = (Map<String, Object>) parent.getAdapter().getItem(position);
            //hapus item pada list
            new AlertDialog.Builder(this)
                    .setTitle("konfirmasi hapus")
                    .setMessage("hapus item " + item.get("name").toString() + " ?")
                    .setPositiveButton("YES", (dialog, which) -> {

                        //hapus file
                        File file = new File(getExStorePath(), item.get("name").toString());
                        if (file.exists())
                            if (file.delete())
                                loadAllFilesFromFolder();
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();

            return true;
        });

    }

    private void loadAllFilesFromFolder() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(this, "external storage not available", Toast.LENGTH_SHORT).show();
            return;
        }

        File parent = new File(getExStorePath());

        if (parent.exists()) {

            File[] files = parent.listFiles();
            String[] fileNames = new String[files.length];
            String[] dateCreated = new String[files.length];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY HH:mm:ss");
            ArrayList<Map<String, Object>> itemList = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                fileNames[i] = files[i].getName();
                Date lastModDate = new Date(files[i].lastModified());
                dateCreated[i] = simpleDateFormat.format(lastModDate);
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", fileNames[i]);
                itemMap.put("date", dateCreated[i]);
                itemList.add(itemMap);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this, itemList, android.R.layout.simple_list_item_2,
                    new String[]{"name", "date"}, new int[]{android.R.id.text1, android.R.id.text2});

            listView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }

    }
    private String getExStorePath() {
        return getExternalFilesDir(null) + "/proyek01.kominfo";
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_tambah)
            startActivity(new Intent(this, InsertAndViewActivity.class));

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23 && permittedToSave())
            loadAllFilesFromFolder();
        else
            loadAllFilesFromFolder();

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadAllFilesFromFolder();
    }
}
