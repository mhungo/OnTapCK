package vn.edu.stu.ontapck;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import vn.edu.stu.model.Lop;
import vn.edu.stu.model.SinhVien;

public class SinhVienActivity extends AppCompatActivity {

    final String SERVER = "http://172.19.201.66/OnCKAndroid/api.php";

    EditText txtma, txtten;
    Spinner spnMalop;
    Button btnLuu;
    ListView lvSinhVien;

    ArrayList<SinhVien> dsSV;
    ArrayAdapter<SinhVien> adapter;

    ArrayList<Lop> dsLop;
    ArrayAdapter<Lop> adapterLop;

    SinhVien chon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinh_vien);

        addControls();
        hienthiLop();
        hienthiSinhVien();
        addEvents();

    }

    private void addEvents() {
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chon == null) {
                    int ma = Integer.parseInt(txtma.getText().toString());
                    String ten = txtten.getText().toString();

                    Lop lop = (Lop) spnMalop.getSelectedItem();
                    int malop = lop.getMalop();

                    SinhVien sinhVien = new SinhVien(ma, ten, malop);
                    xuliThemSV(sinhVien);

                    txtma.setText("");
                    txtten.setText("");

                } else {
                    chon.setTen(txtten.getText().toString());

                    Lop lop = (Lop) spnMalop.getSelectedItem();
                    int malop = lop.getMalop();
                    chon.setMalop(malop);

                    xualySuaSV(chon);

                    txtma.setText("");
                    txtten.setText("");

                    chon = null;

                }

            }
        });


        lvSinhVien.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SinhVienActivity.this);

                builder.setTitle("Thông báo").setMessage("Bạn có muốn xóa hay sửa ?");
                builder.setCancelable(true);
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(SinhVienActivity.this);

                        builder.setTitle("Thông báo").setMessage("Bạn có muốn xóa không ?");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                chon = dsSV.get(i);
                                xuliXoaSV(chon);
                                chon = null;
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
                builder.setNegativeButton("Sửa", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        chon = dsSV.get(i);
                        txtma.setText(chon.getMa() + "");
                        txtten.setText(chon.getTen());

                        for (Lop lop : dsLop) {
                            if (lop.getMalop() == chon.getMalop()) {
                                spnMalop.setSelection(dsLop.indexOf(lop));
                            }
                        }


                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


                return true;
            }
        });
    }

    private void addControls() {

        txtma = findViewById(R.id.txtma);
        txtten = findViewById(R.id.txtten);
        spnMalop = findViewById(R.id.spnMaLop);
        btnLuu = findViewById(R.id.btnLuu);
        lvSinhVien = findViewById(R.id.lvSinhVien);

        dsSV = new ArrayList<>();
        adapter = new ArrayAdapter<>(SinhVienActivity.this, android.R.layout.simple_list_item_1, dsSV);
        lvSinhVien.setAdapter(adapter);

        dsLop = new ArrayList<>();
        adapterLop = new ArrayAdapter<>(SinhVienActivity.this, android.R.layout.simple_spinner_item, dsLop);
        adapterLop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMalop.setAdapter(adapterLop);

        chon = null;

    }

    private void hienthiSinhVien() {

        RequestQueue requestQueue = Volley.newRequestQueue(SinhVienActivity.this);

        Response.Listener<String> reponseListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dsSV.clear();

                            JSONArray jsonArray = new JSONArray(response);
                            int len = jsonArray.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int ma = jsonObject.getInt("ma");
                                String ten = jsonObject.getString("ten");
                                int malop = jsonObject.getInt("malop");
                                dsSV.add(new SinhVien(ma, ten, malop));
                            }
                            adapter.notifyDataSetChanged();

                        } catch (Exception ex) {

                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SinhVienActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        Uri.Builder builder = Uri.parse(SERVER).buildUpon();

        builder.appendQueryParameter("action", "getall");
        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                reponseListener,
                errorListener
        );

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(request);

    }

    private void hienthiLop() {
        RequestQueue requestQueue = Volley.newRequestQueue(SinhVienActivity.this);

        Response.Listener<String> reponseListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dsLop.clear();

                            JSONArray jsonArray = new JSONArray(response);
                            int len = jsonArray.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int malop = jsonObject.getInt("malop");
                                String tenlop = jsonObject.getString("tenlop");
                                dsLop.add(new Lop(malop, tenlop));
                            }
                            adapterLop.notifyDataSetChanged();

                        } catch (Exception ex) {

                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SinhVienActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        Uri.Builder builder = Uri.parse(SERVER).buildUpon();

        builder.appendQueryParameter("action", "getallLop");
        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                reponseListener,
                errorListener
        );

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(request);
    }

    private void xuliThemSV(SinhVien sv) {
        RequestQueue requestQueue = Volley.newRequestQueue(SinhVienActivity.this);
        Response.Listener<String> reStringListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean result = jsonObject.getBoolean("message");
                            if (result) {
                                Toast.makeText(SinhVienActivity.this, "Them thanh cong", Toast.LENGTH_SHORT).show();
                                hienthiSinhVien();
                            } else {
                                Toast.makeText(SinhVienActivity.this, "Them that bai", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(SinhVienActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SinhVienActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        Uri.Builder builder = Uri.parse(SERVER).buildUpon();
        builder.appendQueryParameter("action", "insert");
        builder.appendQueryParameter("ma", sv.getMa() + "");
        builder.appendQueryParameter("ten", sv.getTen());
        builder.appendQueryParameter("malop", sv.getMalop() + "");

        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                reStringListener,
                errorListener
        );
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(request);
    }

    private void xualySuaSV(SinhVien sv) {
        RequestQueue requestQueue = Volley.newRequestQueue(SinhVienActivity.this);
        Response.Listener<String> reStringListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean result = jsonObject.getBoolean("message");
                            if (result) {
                                Toast.makeText(SinhVienActivity.this, "Sua thanh cong", Toast.LENGTH_SHORT).show();
                                hienthiSinhVien();
                            } else {
                                Toast.makeText(SinhVienActivity.this, "Sua that bai", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {

                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SinhVienActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        Uri.Builder builder = Uri.parse(SERVER).buildUpon();
        builder.appendQueryParameter("action", "update");
        builder.appendQueryParameter("ma", sv.getMa() + "");
        builder.appendQueryParameter("ten", sv.getTen());
        builder.appendQueryParameter("malop", sv.getMalop() + "");
        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                reStringListener,
                errorListener
        );
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(request);
    }

    private void xuliXoaSV(SinhVien sv) {
        RequestQueue requestQueue = Volley.newRequestQueue(SinhVienActivity.this);
        Response.Listener<String> reStringListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean result = jsonObject.getBoolean("message");
                            if (result) {
                                Toast.makeText(SinhVienActivity.this, "Xoa thanh cong", Toast.LENGTH_SHORT).show();
                                hienthiSinhVien();
                            } else {
                                Toast.makeText(SinhVienActivity.this, "Xoa that bai", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {

                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SinhVienActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        Uri.Builder builder = Uri.parse(SERVER).buildUpon();
        builder.appendQueryParameter("action", "delete");
        builder.appendQueryParameter("ma", sv.getMa() + "");
        String url = builder.build().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                reStringListener,
                errorListener
        );
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        requestQueue.add(request);
    }
}