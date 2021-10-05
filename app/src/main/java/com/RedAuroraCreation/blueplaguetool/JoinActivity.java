package com.RedAuroraCreation.blueplaguetool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinActivity extends Activity {
    String RoomName;
    String Nickname;
    String RoomCode;
    Boolean AdminMode;
    int PlayersShitBord;
    int PlayersShit;

    String tunnel;
    String ChoosenNick;
    Boolean AdminEntered;

    String CurScreen;
    List<String> Story;
    FirebaseDatabase DBsrc;
    DatabaseReference DBref;
    DatabaseReference DBref2;
    PlayerRow RowDeleteContainer;

    ArrayList PlayerList;
    HashMap<String, Integer> PlayerTagIdSlave;

    @Override
    protected void onDestroy() {
        DBref.removeValue();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        RoomName = getIntent().getExtras().getString("RoomName");
        Nickname = getIntent().getExtras().getString("Nickname");
        RoomCode = getIntent().getExtras().getString("RoomCode");
        AdminMode = getIntent().getExtras().getBoolean("AdminMode");
        PlayerList = new ArrayList();
        PlayerTagIdSlave = new HashMap<String, Integer>();
        RowDeleteContainer = new PlayerRow(JoinActivity.this);

        DBsrc = FirebaseDatabase.getInstance();
        CurScreen = "MainLayout";
        AdminEntered = false;
        Story = new ArrayList();
        LinearLayout LayPlayerList = findViewById(R.id.LayPlayerList);

        if(AdminMode){
            findViewById(R.id.ControlPanel).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.PlayerPanel).setVisibility(View.VISIBLE);
        }

        ((TextView)findViewById(R.id.RoomNameTextView)).setText("Комната «" + RoomName + "»");
        ((TextView)findViewById(R.id.RoomCodeTextView)).setText("Код доступа: " + RoomCode);


        OnCompleteListener listenerVasya = new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                    ArrayList rPlayerList = new ArrayList();
                    try {
                        Map<String, String> hmPlayerList = (HashMap<String, String>) task.getResult().getValue();
                        for (String key : hmPlayerList.keySet()) {
                            rPlayerList.add(key);
                            Log.d("hmPlayerList", key);
                        }
                        Log.d("hmPlayerList", "получен HashMap...");
                        if(PlayerList.size()<rPlayerList.size()){
                            Log.d("PlayerList", "Проекция БД содержит меньшее кол-во игроков, чем сама БД");
                            for(int shit = 0; shit<rPlayerList.size(); shit++){
                                Log.d("PlayerList", "Проверяем игрока "+rPlayerList.get(shit));
                                if(!PlayerList.contains(rPlayerList.get(shit))){
                                    Log.d("PlayerList", "В БД комнаты игрок "+rPlayerList.get(shit)+" содержится, но в самой комнате его нет. Исправляем.");
                                    PlayerRow playerRow = new PlayerRow(JoinActivity.this);
                                    {LinearLayout.LayoutParams PlayerRowLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        playerRow.setLayoutParams(PlayerRowLayoutParams);}
                                    playerRow.setTag("PlayerRow"+rPlayerList.get(shit));
                                    String vID = ""+playerRow.getTag();
                                    playerRow.setBackgroundColor(getResources().getColor(R.color.color0));
                                    playerRow.bullshit = false;

                                    TextView PlayerNick = new TextView(JoinActivity.this);
                                    PlayerNick.setWidth(400);
                                    PlayerNick.setId(genId());
                                    PlayerNick.setText(""+rPlayerList.get(shit));
                                    PlayerNick.setTextSize(24);
                                    playerRow.addView(PlayerNick);

                                    if(AdminMode){
                                        Button deleteBut = new Button(JoinActivity.this);
                                    deleteBut.setWidth(1);
                                    deleteBut.setId(genId());
                                    deleteBut.setVisibility(View.INVISIBLE);
                                    deleteBut.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                    deleteBut.setText("✖");
                                    deleteBut.setTextColor(getResources().getColor(R.color.purple_500));
                                    deleteBut.setTextSize(24);

                                    deleteBut.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v){
                                            if(deleteBut.getVisibility()==View.VISIBLE){
                                                DBref.child("Players").child(PlayerNick.getText().toString()).removeValue();
                                                ((PlayerRow)deleteBut.getParent()).callOnClick();
                                                Log.d("Выделены", PlayerNick.getText().toString()+"//"+Nickname);
                                                if(PlayerNick.getText().toString()==Nickname){
                                                    Log.d("deleteBut", "Удален PlayerRow админа");
                                                    ((Button)Obj("butEnter")).setText("Войти");
                                                    AdminEntered = false;
                                                }
                                                ChoosenNick = null;
                                            }
                                        }
                                    });
                                    playerRow.addView(deleteBut);
                                    }


                                    Log.d("RightsMode", ""+AdminMode);
                                    if(AdminMode){
                                        Log.d("PlayerRow", "Метод оnClick присвоен");
                                        playerRow.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(!playerRow.bullshit){
                                                    Log.d("onClick", "PlayerRow выделен");
                                                    playerRow.select(true);
                                                    if (ChoosenNick!=null){
                                                        PlayerRow prevPlayerRow = (PlayerRow)findViewById(PlayerTagIdSlave.get(("PlayerRow"+ChoosenNick)));
                                                        prevPlayerRow.select(false);
                                                        ((TextView)prevPlayerRow.getChildAt(0)).setTextColor(getResources().getColor(R.color.greyblack));
                                                        prevPlayerRow.getChildAt(1).setVisibility(View.INVISIBLE);
                                                    }
                                                    ((TextView)playerRow.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                                                    playerRow.getChildAt(1).setVisibility(View.VISIBLE);
                                                    Log.d("onClick", "PlayerNick: "+PlayerNick.getText());
                                                    ChoosenNick = (String) PlayerNick.getText();
                                                    Log.d("ChoosenNick", ChoosenNick);
                                                }else{
                                                    Log.d("onClick", "PlayerRow отпущен");
                                                    playerRow.select(false);
                                                    if (ChoosenNick!=null){
                                                        PlayerRow prevPlayerRow = (PlayerRow)findViewById(PlayerTagIdSlave.get(("PlayerRow"+ChoosenNick)));
                                                        prevPlayerRow.select(false);
                                                        ((TextView)prevPlayerRow.getChildAt(0)).setTextColor(getResources().getColor(R.color.greyblack));
                                                        prevPlayerRow.getChildAt(1).setVisibility(View.INVISIBLE);
                                                    }
                                                    ((TextView)playerRow.getChildAt(0)).setTextColor(getResources().getColor(R.color.greyblack));
                                                    playerRow.getChildAt(1).setVisibility(View.INVISIBLE);
                                                    ChoosenNick = null;
                                                }
                                            }
                                        });
                                    }
                                    LayPlayerList.addView(playerRow);
                                    for(int shit1 = 0; shit1<LayPlayerList.getChildCount();shit1++){
                                        Log.d("LayPlayerList, весь список", ""+LayPlayerList.getChildAt(shit1).getTag());
                                    }
                                    PlayerList = rPlayerList;
                                    PlayersShit = rPlayerList.size();
                                    ((TextView)Obj("PlayerShitIndicator")).setText(PlayersShit+"/"+PlayersShitBord);

                                    playerRow.setId(genId());
                                    PlayerTagIdSlave.put("PlayerRow"+rPlayerList.get(shit), playerRow.getId());
                                    Log.d("Сгенерированный Id", ""+playerRow.getId()+", записан под тегом "+"PlayerRow"+rPlayerList.get(shit));
                                    //Григорий: 2050471767; Степан: 1831544557;
                                    break;
                                }else{
                                    Log.d("PlayerList", "Комната содержит игрока "+rPlayerList.get(shit));
                                }
                            }
                        }
                        else if(PlayerList.size()>rPlayerList.size()){
                            Log.d("PlayerList", "Проекция БД содержит большее кол-во игроков, чем сама БД");
                            for(int shit = 0; shit<PlayerList.size(); shit++){
                                Log.d("PlayerList", "Проверяем игрока "+PlayerList.get(shit));
                                if(!rPlayerList.contains(PlayerList.get(shit))){
                                    Log.d("PlayerList", "БД комнаты не содержит игрока "+PlayerList.get(shit));
                                    Log.d("PlayerRow", "Под тегом "+"PlayerRow"+PlayerList.get(shit)+" записан Id "+PlayerTagIdSlave.get("PlayerRow"+PlayerList.get(shit)));
                                    RowDeleteContainer = findViewById(PlayerTagIdSlave.get("PlayerRow"+PlayerList.get(shit)));
                                    RowDeleteContainer.removeAllViews();
                                    LayPlayerList.removeView(RowDeleteContainer);
                                    PlayerList = rPlayerList;
                                    PlayersShit = rPlayerList.size();
                                    ((TextView)Obj("PlayerShitIndicator")).setText(PlayersShit+"/"+PlayersShitBord);
                                    for(int shit1 = 0; shit1<LayPlayerList.getChildCount();shit1++){
                                        Log.d("LayPlayerList, весь список", ""+LayPlayerList.getChildAt(shit1).getTag());
                                    }
                                    break;
                                }else{
                                    Log.d("PlayerList", "БД комнаты содержит игрока "+PlayerList.get(shit));
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d("hmPlayerList", "получен String... "+(String) task.getResult().getValue());
                        if((String) task.getResult().getValue()==null){
                           for(int shit = 0; shit<PlayerList.size(); shit++){
                               RowDeleteContainer = findViewById(PlayerTagIdSlave.get("PlayerRow"+PlayerList.get(shit)));
                               RowDeleteContainer.removeAllViews();
                               LayPlayerList.removeView(RowDeleteContainer);
                               PlayerList = rPlayerList;
                               PlayersShit = rPlayerList.size();
                           }
                        }
                        ((TextView)Obj("PlayerShitIndicator")).setText(0+"/"+PlayersShitBord);
                    }//Получение списка игроков из ДБ (Запись в rPlayerList)

            }
        };
        OnCompleteListener listenerPetya = new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                PlayersShitBord = Integer.parseInt(""+task.getResult().getValue());
            }
        };
        ValueEventListener listenerVova = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                DBref.child("Players").get().addOnCompleteListener(listenerVasya);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FireBase", "DBerror");
            }
        };
        DBref = DBsrc.getReference("Servers"+"/"+RoomName+"/"+RoomCode);
        DBref.child("PlayersShit").get().addOnCompleteListener(listenerPetya);
        DBref.addValueEventListener(listenerVova);

    }

    public void ButTurnOff(View but){
        layswitch("ExitWindow");
    }

    public void ButEnter(View but){
        if(!AdminEntered){
            ((Button)Obj("butEnter")).setText("Выйти");
            AdminEntered = true;
            DBref.child("Players").child(Nickname).setValue("0");
            findViewById(R.id.PlayerPanel).setVisibility(View.VISIBLE);
        }else{
            ((Button)Obj("butEnter")).setText("Войти");
            AdminEntered = false;
            DBref.child("Players").child(Nickname).removeValue();
            findViewById(R.id.PlayerPanel).setVisibility(View.GONE);
        }
    }

    public void ButExitNe(View but){
        laycancel();
    }
    public void ButExitDa(View but){
        DBref.removeValue();
        finish();
    }
    public void onBackPressed() {
        if(!Story.isEmpty()){
            laycancel();
        }else{
            layswitch("ExitWindow");
        }

    }

    //вспомогательные функции
    public View Obj(String vID){
        View a = findViewById(getResources().getIdentifier(vID,"id",getPackageName()));
        Log.d("ID", ""+getResources().getIdentifier(vID,"id",getPackageName())+" ... "+vID);
        return a;
    }//получение вьюшки
    public void layswitch(String view){
        Story.add(CurScreen);
        Obj(CurScreen).setVisibility(View.GONE);
        Obj(view).setVisibility(View.VISIBLE);
        CurScreen = view;
    }//переключение экрана
    public void laycancel(){
        if(CurScreen != "MainLayout") {
            String LastEl = (String) Story.get(Story.size()-1);
            if(LastEl == CurScreen) {Story.remove(Story.size() - 1);}
            String vrobj = (String) Story.get(Story.size()-1);
            Obj(vrobj).setVisibility(View.VISIBLE);
            Obj(CurScreen).setVisibility(View.GONE);
            CurScreen = vrobj;
        }else{
            layswitch("ExitWindow");
        }
    }//предыдущий экран
    public int genId(){
        int id = (int)(Math.random()*2147483647);
        View v = findViewById(id);
        if(id>1000000000){
            while (v != null){
                v = findViewById(id--);
            }
        }else{
            while (v != null){
                v = findViewById(id++);
            }
        }
        return id++;
    }//генерация Id для динамически созданных объектов
}
