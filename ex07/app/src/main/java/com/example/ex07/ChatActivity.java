package com.example.ex07;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseDatabase db; //Realtime Database
    DatabaseReference ref; //위치 지정
    List<ChatVO> array = new ArrayList<>();
    ChatAdapter adapter = new ChatAdapter();
    RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        getList();

        list = findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        //registerForContextMenu(list);
        
        getSupportActionBar().setTitle("채팅: " + user.getEmail());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //send 아이콘 눌렀을때
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText contents = findViewById(R.id.edtContents);
                String strContents = contents.getText().toString();
                if(strContents.equals("")){
                    Toast.makeText(ChatActivity.this, "내용을 입력하세요!", Toast.LENGTH_SHORT).show();
                }else{
                    //메세지 전송
                    ChatVO vo = new ChatVO();
                    vo.setEmail(user.getEmail());
                    vo.setContents(strContents);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = sdf.format(new Date());
                    vo.setDate(strDate);

                    //System.out.println("............................" +vo.toString());
                    //저장할 reference 지정
                    ref = db.getReference("/chat").push();
                    vo.setKey(ref.getKey());
                    ref.setValue(vo);

                    //System.out.println("............................" +vo.toString());
                    contents.setText(""); //전송 후 메세지창 비워주기
                }
            }
        });
    }

    //데이터 읽어오기
    public void getList() {
        ref = db.getReference("chat");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //데이터를 하나씩 읽어서 chatvo에
                ChatVO vo = snapshot.getValue(ChatVO.class);
                array.add(vo); //읽어온 데이터(vo)를 array에 넣기
                //System.out.println(vo.toString());
                adapter.notifyDataSetChanged(); //데이터가 바뀔때마다 adapter에 알려주기
                list.scrollToPosition(array.size()-1); //스크롤 이동
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ChatVO vo = snapshot.getValue(ChatVO.class);
                for(ChatVO chat:array){
                    if(chat.getKey().equals(vo.getKey())){
                        array.remove(chat);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    //어댑터 생성
    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        @NonNull
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //데이터의 갯수만큼 item chat 생성
            View view = getLayoutInflater().inflate(R.layout.item_chat, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) { //내용 가져오기
            //데이터 가져오기
            ChatVO vo = array.get(position);

            holder.txtContents.setText(vo.getContents());
            holder.txtEmail.setText(vo.getEmail());
            holder.txtDate.setText(vo.getDate());

            LinearLayout.LayoutParams pcontents = (LinearLayout.LayoutParams) holder.txtContents.getLayoutParams();
            LinearLayout.LayoutParams pdate = (LinearLayout.LayoutParams) holder.txtDate.getLayoutParams();

            if(vo.getEmail().equals(user.getEmail())){
                pcontents.gravity = Gravity.RIGHT;
                pdate.gravity = Gravity.RIGHT;
                holder.txtEmail.setVisibility(View.GONE); //Login한 user의 이메일은 안보이게
            }else{
                pcontents.gravity = Gravity.LEFT;
                pdate.gravity = Gravity.LEFT;
                holder.txtEmail.setVisibility(View.VISIBLE);
            }

            //내용을 길게 눌렀을때 삭제
            holder.txtContents.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(vo.getEmail().equals(user.getEmail())){ //login 한 user의 글만
                        AlertDialog.Builder box = new AlertDialog.Builder(ChatActivity.this);
                        box.setTitle("메뉴를 선택하세요!");
                        box.setItems(new String[]{"삭제", "취소"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { //index값이 which에 들어감
                                if(which == 0){
                                    //db에서 삭제
                                    db.getReference("chat/" + vo.getKey()).removeValue();
                                }
                            }
                        });
                        box.show();
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder { //id 읽어들이기
            TextView txtContents, txtDate, txtEmail;
            LinearLayout item;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                txtContents = itemView.findViewById(R.id.txtContents);
                txtDate = itemView.findViewById(R.id.txtDate);
                txtEmail = itemView.findViewById(R.id.txtEmail);
                item = itemView.findViewById(R.id.item);
            }
        }
    }

    //삭제 (long click)했을때
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("선택");
        menu.add(0, 1, 0, "삭제");
    }
} //Activity