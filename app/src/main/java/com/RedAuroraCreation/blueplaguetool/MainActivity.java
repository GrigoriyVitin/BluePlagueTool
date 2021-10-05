package com.RedAuroraCreation.blueplaguetool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public String Nickname;
    String CurScreen;
    ArrayList Story;
    FirebaseDatabase DBsrc;
    DatabaseReference DBref;
    TextView RoomNameCreate;
    TextView PlayersShitIndicator;
    TextView StatesShitIndicator;
    String Code;
    List<String> CodesList;
    Boolean test = false;

    @Override
    protected void onRestart() {
        super.onRestart();
        Obj(CurScreen).setVisibility(View.GONE);
        Obj("MainMenu").setVisibility(View.VISIBLE);
        CurScreen = "MainMenu";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CurScreen = "StartNick";
        Story = new ArrayList();
        DBsrc = FirebaseDatabase.getInstance();
        PlayersShitIndicator = findViewById(R.id.PlayersShitIndicator);
        StatesShitIndicator = findViewById(R.id.StatesShitIndicator);
        RoomNameCreate = findViewById(R.id.RoomNameCreate);
        CodesList = new ArrayList();


    }

    public void ButPrinatNick(View but) {
        TextView PredSetNick = findViewById(R.id.PredSetNick);
        PredSetNick.setVisibility(View.GONE);
        Nickname = ((TextView) findViewById(R.id.TextNick)).getText().toString();
        Log.d("1", Nickname);
        if (Nickname != "") {
            if (Nickname.length() >= 3 && Nickname.length() <= 24) {
                layswitch("MainMenu");
            } else if (Nickname.length() < 3) {
                PredSetNick.setVisibility(View.VISIBLE);
                PredSetNick.setText("Не меньше трёх знаков!");
            } else if (Nickname.length() > 24) {
                PredSetNick.setVisibility(View.VISIBLE);
                PredSetNick.setText("Не больше двадцати четырёх знаков!");
            }
        } else {
            PredSetNick.setVisibility(View.VISIBLE);
            PredSetNick.setText("Вы не ввели псевдоним!");
        }
    }

    public void ButCreate(View but) {
        layswitch("CreateWindow");
    }

    public void ButJoin(View but) {
        layswitch("JoinWindow");
    }

    public void ButChangeNick(View but) {
        layswitch("StartNick");
    }

    public void ButExit(View but) {
        layswitch("ExitWindow");
    }

    public void ButPlayersShitMenshe(View but) {
        TextView indic = (TextView) Obj("PlayersShitIndicator");
        String indicstr = indic.getText().toString();
        int indicint = Integer.parseInt(indicstr);
        if (indicint > 2) {
            indicint -= 1;
            indic.setText(indicint + "");
        }
    }
    public void ButPlayersShitBolshe(View but) {
        EditText indic = (EditText) Obj("PlayersShitIndicator");
        String indicstr = indic.getText().toString();
        int indicint = Integer.parseInt(indicstr);
        if (indicint < 32) {
            indicint += 1;
            indic.setText(indicint + "");
        }
    }
    public void ButStatesShitMenshe(View but) {
        TextView indic = (TextView) Obj("StatesShitIndicator");
        String indicstr = indic.getText().toString();
        int indicint = Integer.parseInt(indicstr);
        if (indicint > 2) {
            indicint -= 1;
            indic.setText(indicint + "");
        }
    }
    public void ButStatesShitBolshe(View but) {
        TextView indic = (TextView) Obj("StatesShitIndicator");
        String indicstr = indic.getText().toString();
        int indicint = Integer.parseInt(indicstr);
        if (indicint < 16) {
            indicint += 1;
            indic.setText(indicint + "");
        }
    }

    public void ButCreateClaim(View but) {
        String RoomName = RoomNameCreate.getText().toString();
        TextView PredSetRoomName = findViewById(R.id.PredSetRoomName);
        TextView textView7 = findViewById(R.id.textView7);
        if (RoomName != "") {
            if (RoomName.length() >= 3 && RoomName.length() <= 24) {
                DBref = DBsrc.getReference("Servers" + "/" + RoomName);//RoomName == "Бун"
                OnCompleteListener listenerVasya = new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                CodesList.add((String)task.getResult().getValue());
                                Log.d("CodesList: ", (String) task.getResult().getValue());
                            } catch (Exception e) {
                                try{
                                    Map<String, String> hmCodesList = (HashMap<String, String>) task.getResult().getValue();
                                    for (String key : hmCodesList.keySet()) {
                                        CodesList.add(key);
                                        Log.d("CodesList: ", key);
                                    }
                                }catch (Exception e1){
                                    Log.d("CodesList: ", "Комната полностью пуста");
                                }
                            }
                            Boolean CheckCodeSovpad;
                            do {
                                CheckCodeSovpad = true;
                                int intCode = (int) (Math.random() * 10000);
                                if (intCode >= 1000) {
                                    Code = "" + intCode;
                                } else if (intCode >= 100) {
                                    Code = "0" + intCode;
                                } else if (intCode >= 10) {
                                    Code = "00" + intCode;
                                } else {
                                    Code = "000" + intCode;
                                }
                                for (int shit = 0; shit < CodesList.size(); shit++) {
                                    if (Code == CodesList.get(shit)) {
                                        CheckCodeSovpad = false;
                                        Log.d("FireBase", "Проверка кода провалена: " + Code + " уже существует.");
                                    }
                                }

                            } while (!CheckCodeSovpad);
                            Log.d("FireBase", "Проверка кода завершена: " + Code + " уникален.");
                            DBref = DBsrc.getReference("Servers" + "/" + RoomName + "/" + Code);
                            Room room = new Room();
                            room.RoomAdmin = Nickname;
                            room.PlayersShit = PlayersShitIndicator.getText().toString();
                            room.StatesShit = StatesShitIndicator.getText().toString();
                            DBref.setValue(room);

                            Intent intent = new Intent(MainActivity.this, JoinActivity.class);
                            intent.putExtra("Nickname", Nickname);
                            intent.putExtra("RoomName", RoomName);
                            intent.putExtra("RoomCode", Code);
                            intent.putExtra("AdminMode", true);
                            startActivity(intent);
                        }else {
                            Log.d("FireBase", "DBerror");
                        }


                    }
                };
                DBref.get().addOnCompleteListener(listenerVasya);
            }else if (RoomName.length() > 24) {
                PredSetRoomName.setVisibility(View.VISIBLE);
                PredSetRoomName.setText("Не больше двадцати четырёх знаков!");
            }else if (RoomName.length() < 3) {
                PredSetRoomName.setVisibility(View.VISIBLE);
                PredSetRoomName.setText("Не меньше трёх знаков!");
            }
        }else{
        PredSetRoomName.setVisibility(View.VISIBLE);
        PredSetRoomName.setText("Вы не ввели название комнаты!");
    }
}



    public void ButJoinClaim(View but){
        Boolean AllRight = true;
        String RoomName = (((TextView) Obj("TextRoomJoinName")).getText()).toString();
        String RoomCode = (((TextView) Obj("TextCodeJoinName")).getText()).toString();
        TextView PredJoinRoom = findViewById(R.id.PredJoinRoom);
        TextView PredJoinCode = findViewById(R.id.PredJoinCode);
        if(RoomName==""){
            AllRight = false;
            PredJoinRoom.setVisibility(View.VISIBLE);
            PredJoinRoom.setText("Вы не ввели название комнаты!");
        }
        if(RoomCode==""){
            AllRight = false;
            PredJoinCode.setVisibility(View.VISIBLE);
            PredJoinCode.setText("Вы не ввели код!");
        }
        if(AllRight){

        }
    }

    public void ButExitNe(View but){
        laycancel();
    }
    public void ButExitDa(View but){
        System.exit(0);
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
        return a;
    }//получение вьюшки
    public void layswitch(String view){
        Story.add(CurScreen);
        Obj(CurScreen).setVisibility(View.GONE);
        Obj(view).setVisibility(View.VISIBLE);
        CurScreen = view;
    }//переключение экрана
    public void laycancel(){
        if(CurScreen != "MainMenu"&&CurScreen != "StartNick") {
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
}
