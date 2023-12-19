package com.example.ex07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReadActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseFirestore db;
    EditText edtTitle, edtContents;
    Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("글 수정 :" + user.getEmail());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //id값 가져오기
        edtTitle = findViewById(R.id.edtTitle);
        edtContents = findViewById(R.id.edtContents);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        //db에서 데이터 읽어오기
        db.collection("post")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) { //가져온 데이터가 task에 있다.
                        DocumentSnapshot doc = task.getResult();
                        PostVO vo = new PostVO();
                        vo.setId(id);
                        vo.setTitle(doc.getData().get("title").toString());
                        vo.setContents(doc.getData().get("contents").toString());
                        vo.setEmail(doc.getData().get("email").toString());
                        vo.setDate(doc.getData().get("date").toString());
                        //System.out.println(vo.toString());
                        edtTitle.setText(vo.getTitle());
                        edtContents.setText(vo.getContents());

                        if(!user.getEmail().equals(vo.getEmail())){ //login user의 게시글이 아니면
                            edtTitle.setEnabled(false); //수정 불가
                            edtContents.setEnabled(false);
                            btnSave.setVisibility(View.GONE); //버튼 안보이게
                            btnCancel.setVisibility(View.GONE);
                        }else{
                            btnSave.setText("수정");
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}