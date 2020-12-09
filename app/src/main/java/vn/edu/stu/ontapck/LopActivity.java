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
import java.util.List;

import vn.edu.stu.model.Lop;
import vn.edu.stu.model.SinhVien;

public class LopActivity extends AppCompatActivity {

    EditText txtmalop, txttenlop;
    Button btnLuuLop;
    ListView lvLop;
    ArrayAdapter<Lop> adapter;
    ArrayList<Lop> dsLop;

    Lop chon;

    final String SERVER = "http://172.19.201.66/OnCKAndroid/api.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lop);

        addControls();
        hienthiLop();
        addEvents();
    }

    private void addEvents() {

        btnLuuLop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chon == null) {
                    int malop = Integer.parseInt(txtmalop.getText().toString());
                    String tenlop = txttenlop.getText().toString();

                    Lop lop = new Lop(malop, tenlop);
                    xuliThemLop(lop);

                    txtmalop.setText("");
                    txttenlop.setText("");

                } else {
                    chon.setTenop(txttenlop.getText().toString());

                    xualySuaLop(chon);

                    txtmalop.setText("");
                    txttenlop.setText("");

                    chon = null;

                }
            }
        });


        lvLop.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LopActivity.this);

                builder.setTitle("Thông báo").setMessage("Bạn có muốn xóa hay sửa ?");
                builder.setCancelable(true);
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(LopActivity.this);

                        builder.setTitle("Thông báo").setMessage("Bạn có muốn xóa không ?");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                chon = dsLop.get(i);
                                xuliXoaLop(chon);
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
                        chon = dsLop.get(i);
                        txtmalop.setText(chon.getMalop() + "");
                        txttenlop.setText(chon.getTenop());

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


                return true;
            }
        });
    }

    private void addControls() {

        txttenlop = findViewById(R.id.txtenlop);
        txtmalop = findViewById(R.id.txtmalop);
        btnLuuLop = findViewById(R.id.btnLuuLop);

        dsLop = new ArrayList<>();
        adapter = new ArrayAdapter<>(LopActivity.this, android.R.layout.simple_list_item_1, dsLop);

        lvLop = findViewById(R.id.lvLop);
        lvLop.setAdapter(adapter);

        chon = null;
    }


    private void hienthiLop() {
        RequestQueue requestQueue = Volley.newRequestQueue(LopActivity.this);

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
                            adapter.notifyDataSetChanged();

                        } catch (Exception ex) {

                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LopActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void xuliThemLop(Lop lop) {
        RequestQueue requestQueue = Volley.newRequestQueue(LopActivity.this);
        Response.Listener<String> reStringListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean result = jsonObject.getBoolean("message");
                            if (result) {
                                Toast.makeText(LopActivity.this, "Them thanh cong", Toast.LENGTH_SHORT).show();
                                hienthiLop();
                            } else {
                                Toast.makeText(LopActivity.this, "Them that bai", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(LopActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LopActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        Uri.Builder builder = Uri.parse(SERVER).buildUpon();
        builder.appendQueryParameter("action", "insertLop");
        builder.appendQueryParameter("malop", lop.getMalop() + "");
        builder.appendQueryParameter("tenlop", lop.getTenop());

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

    private void xualySuaLop(Lop lop) {
        RequestQueue requestQueue = Volley.newRequestQueue(LopActivity.this);
        Response.Listener<String> reStringListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean result = jsonObject.getBoolean("message");
                            if (result) {
                                Toast.makeText(LopActivity.this, "Sua thanh cong", Toast.LENGTH_SHORT).show();
                                hienthiLop();
                            } else {
                                Toast.makeText(LopActivity.this, "Sua that bai", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {

                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LopActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        Uri.Builder builder = Uri.parse(SERVER).buildUpon();
        builder.appendQueryParameter("action", "updateLop");
        builder.appendQueryParameter("malop", lop.getMalop() + "");
        builder.appendQueryParameter("tenlop", lop.getTenop());
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

    private void xuliXoaLop(Lop lop) {
        RequestQueue requestQueue = Volley.newRequestQueue(LopActivity.this);
        Response.Listener<String> reStringListener =
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean result = jsonObject.getBoolean("message");
                            if (result) {
                                Toast.makeText(LopActivity.this, "Xoa thanh cong", Toast.LENGTH_SHORT).show();
                                hienthiLop();
                            } else {
                                Toast.makeText(LopActivity.this, "Xoa that bai", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {

                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LopActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        Uri.Builder builder = Uri.parse(SERVER).buildUpon();
        builder.appendQueryParameter("action", "deleteLop");
        builder.appendQueryParameter("malop", lop.getMalop() + "");
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