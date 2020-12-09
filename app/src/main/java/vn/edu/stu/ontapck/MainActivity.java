package vn.edu.stu.ontapck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import vn.edu.stu.model.SinhVien;

public class MainActivity extends AppCompatActivity {

    Button btnLop, btnSinhVien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLop = findViewById(R.id.btnLop);
        btnSinhVien = findViewById(R.id.btnSinhVien);


        btnSinhVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SinhVienActivity.class);
                startActivity(intent);
            }
        });

        btnLop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LopActivity.class);
                startActivity(intent);
            }
        });


    }
}