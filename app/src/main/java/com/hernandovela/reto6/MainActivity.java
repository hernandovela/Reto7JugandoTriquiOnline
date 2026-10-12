package com.hernandovela.reto7;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.view.*;
import android.widget.*;
import com.google.firebase.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
public final class MainActivity extends Activity {
 OnlineRepository repo; ListenerRegistration listener;
 LinearLayout content,rooms;TextView status,detail,hostLabel,guestLabel,roomTitle;
 BoardView board;EditText playerInput;
 String gameId;boolean busy,connected;DocumentSnapshot current;
 final TicTacToeGame game=new TicTacToeGame();
 static final int PLAYER_INPUT=4101,ROOM_INPUT=4102,CODE_INPUT=4103;
 @Override public void onCreate(Bundle state){
  super.onCreate(state);gameId=getPreferences(0).getString("gameV2Id",null);
  shell("Conectando con Firebase…");
  try{
   if(FirebaseApp.getApps(this).isEmpty())FirebaseApp.initializeApp(this,FirebaseConfig.load(this));
   repo=new OnlineRepository();FirebaseAuth a=FirebaseAuth.getInstance();
   if(a.getCurrentUser()!=null)ready();
   else a.signInAnonymously().addOnSuccessListener(r->{if(!isDestroyed())ready();}).addOnFailureListener(this::failure);
  }catch(Exception e){status.setText("No se pudo configurar Firebase");text(content,e.getMessage(),14,R.color.text_secondary);}
 }
 int dp(int n){return (int)(n*getResources().getDisplayMetrics().density);}
 TextView text(LinearLayout parent,String s,int size,int color){
  TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(getColor(color));v.setGravity(Gravity.CENTER);v.setPadding(0,dp(6),0,dp(6));parent.addView(v);return v;
 }
 void shell(String title){
  if(listener!=null){listener.remove();listener=null;}
  busy=false;current=null;board=null;playerInput=null;
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundResource(R.drawable.app_background);
  content=new LinearLayout(this);content.setOrientation(1);content.setGravity(Gravity.CENTER_HORIZONTAL);
  content.setPadding(dp(18),dp(30),dp(18),dp(24));scroll.addView(content);setContentView(scroll);
  text(content,"RETO 07 · JUGANDO ONLINE",11,R.color.x_label);
  text(content,"Tres en raya",28,R.color.text_primary).setTypeface(null,Typeface.BOLD);
  text(content,"Versión "+BuildConfig.VERSION_NAME+" · build "+BuildConfig.VERSION_CODE,12,R.color.text_secondary);
  status=text(content,title,18,R.color.text_primary);status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
 }
 Button button(LinearLayout parent,String s,boolean primary,Runnable action){
  Button b=new Button(this);b.setText(s);b.setTextSize(14);b.setAllCaps(false);b.setTextColor(getColor(R.color.text_primary));
  b.setBackgroundResource(primary?R.drawable.primary_button:R.drawable.secondary_button);
  b.setBackgroundTintList(null);b.setStateListAnimator(null);
  LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(48));p.topMargin=dp(8);parent.addView(b,p);
  b.setOnClickListener(v->action.run());return b;
 }
 LinearLayout card(LinearLayout parent){
  LinearLayout c=new LinearLayout(this);c.setOrientation(1);c.setPadding(dp(14),dp(10),dp(14),dp(14));c.setBackgroundResource(R.drawable.score_background);
  LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=dp(12);parent.addView(c,p);return c;
 }
 EditText input(LinearLayout parent,String hint,int id,int length){
  EditText e=new EditText(this);e.setId(id);e.setSingleLine(true);e.setTextColor(getColor(R.color.text_primary));e.setHintTextColor(getColor(R.color.text_secondary));e.setHint(hint);e.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});parent.addView(e,new LinearLayout.LayoutParams(-1,dp(52)));return e;
 }
 String player(){
  String name=RoomCode.player(playerInput.getText().toString());
  getPreferences(0).edit().putString("playerName",name).apply();return name;
 }
 void ready(){if(isDestroyed()||isFinishing())return;if(gameId==null)lobby();else watch();}
 void select(String id){
  getPreferences(0).edit().putString("gameV2Id",id).apply();gameId=id;
  if(!isDestroyed()&&!isFinishing())watch();
 }
 void lobby(){
  shell("Crea una partida o ingresa un código");
  LinearLayout profile=card(content);text(profile,"TU NOMBRE",12,R.color.x_label);
  playerInput=input(profile,"Nombre del jugador",PLAYER_INPUT,24);playerInput.setText(getPreferences(0).getString("playerName",""));
  LinearLayout create=card(content);text(create,"Nueva partida",19,R.color.text_primary);
  EditText room=input(create,"Nombre de la partida (opcional)",ROOM_INPUT,40);
  button(create,"Crear y generar código",true,()->{
   if(busy)return;
   try{
    String p=player(),s=room.getText().toString().trim();if(s.isEmpty())s="Partida de "+p;
    busy=true;status.setText("Generando tu código…");
    repo.create(s,p).addOnSuccessListener(r->{busy=false;select(r.getId());}).addOnFailureListener(this::failure);
   }catch(IllegalArgumentException e){failure(e);}
  });
  LinearLayout join=card(content);text(join,"Únete por código",19,R.color.text_primary);
  EditText code=input(join,"Ejemplo: ABCD-2345",CODE_INPUT,16);code.setInputType(android.text.InputType.TYPE_CLASS_TEXT|android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
  button(join,"Ingresar a la partida",true,()->{
   if(busy)return;
   try{
    String p=player(),entered=RoomCode.normalize(code.getText().toString());
    if(!RoomCode.valid(entered))throw new IllegalArgumentException("Ingresa el código de 8 caracteres que te compartieron.");
    busy=true;status.setText("Buscando partida…");
    repo.join(entered,p).addOnSuccessListener(r->{busy=false;select(entered);}).addOnFailureListener(this::failure);
   }catch(IllegalArgumentException e){failure(e);}
  });
  text(content,"Partidas disponibles",19,R.color.text_primary);
  text(content,"Pide el código al creador para ingresar.",13,R.color.text_secondary);
  rooms=new LinearLayout(this);rooms.setOrientation(1);content.addView(rooms,new LinearLayout.LayoutParams(-1,-2));
  listener=repo.games().whereEqualTo("status","waiting").limit(50).addSnapshotListener(MetadataChanges.INCLUDE,(snapshot,error)->{
   if(error!=null){failure(error);return;}if(gameId!=null||isDestroyed())return;rooms.removeAllViews();
   if(!busy)status.setText(snapshot.getMetadata().isFromCache()?"Reconectando…":"Crea una partida o ingresa un código");
   if(snapshot.isEmpty())text(rooms,"No hay partidas esperando.",14,R.color.text_secondary);
   for(DocumentSnapshot g:snapshot.getDocuments()){
    LinearLayout row=card(rooms);text(row,g.getString("name"),16,R.color.text_primary);
    text(row,"Creador: "+g.getString("hostName")+" · esperando",13,R.color.x_label);
    if(repo.uid().equals(g.getString("host")))button(row,"Reanudar mi partida",false,()->{if(!busy)select(g.getId());});
   }
  });
 }
 void watch(){
  shell("Cargando partida…");
  boolean landscape=getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE;
  LinearLayout body=new LinearLayout(this);body.setOrientation(landscape?0:1);body.setGravity(Gravity.CENTER);content.addView(body,new LinearLayout.LayoutParams(-1,-2));
  LinearLayout boardPanel=new LinearLayout(this);boardPanel.setOrientation(1);boardPanel.setGravity(Gravity.CENTER);
  body.addView(boardPanel,landscape?new LinearLayout.LayoutParams(0,-2,1):new LinearLayout.LayoutParams(-1,-2));
  LinearLayout legend=new LinearLayout(this);legend.setOrientation(0);boardPanel.addView(legend,new LinearLayout.LayoutParams(-1,-2));
  hostLabel=text(legend,"X · …",15,R.color.x_label);hostLabel.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
  guestLabel=text(legend,"O · Esperando",15,R.color.o_label);guestLabel.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
  hostLabel.setTypeface(null,Typeface.BOLD);guestLabel.setTypeface(null,Typeface.BOLD);
  board=new BoardView(this);board.setGame(game);board.setEnabled(false);boardPanel.addView(board,new LinearLayout.LayoutParams(dp(landscape?250:280),dp(landscape?250:280)));
  board.setMoveListener(cell->{
   if(busy||!connected)return;busy=true;render();
   repo.change(gameId,cell).addOnSuccessListener(r->{busy=false;if(!isDestroyed())render();}).addOnFailureListener(this::failure);
  });
  LinearLayout panel=new LinearLayout(this);panel.setOrientation(1);panel.setPadding(dp(8),0,dp(8),0);
  body.addView(panel,landscape?new LinearLayout.LayoutParams(0,-2,1):new LinearLayout.LayoutParams(-1,-2));
  roomTitle=text(panel,"",17,R.color.text_primary);
  LinearLayout invitation=card(panel);text(invitation,"CÓDIGO DE LA PARTIDA",11,R.color.x_label);
  TextView code=text(invitation,RoomCode.display(gameId),26,R.color.text_primary);code.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);code.setTextIsSelectable(true);
  button(invitation,"Copiar código",true,()->{
   ((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Código Triqui",RoomCode.display(gameId)));
   Toast.makeText(this,"Código copiado: "+RoomCode.display(gameId),Toast.LENGTH_SHORT).show();
  });
  detail=text(panel,"",15,R.color.text_secondary);
  button(panel,"Volver a la lista / salir",false,this::leave);
  text(panel,"Gira la pantalla: tu partida continúa.",12,R.color.text_secondary);
  listener=repo.games().document(gameId).addSnapshotListener(MetadataChanges.INCLUDE,(g,e)->{
   if(e!=null){failure(e);return;}if(isDestroyed())return;current=g;connected=!g.getMetadata().isFromCache();render();
  });
 }
 void leave(){
  if(busy)return;
  if(current==null||!current.exists()||!java.util.Arrays.asList("waiting","playing").contains(current.getString("status"))){backToLobby();return;}
  new AlertDialog.Builder(this).setTitle("Salir de la partida").setMessage("La partida se cerrará para ambos jugadores.").setNegativeButton("Continuar",null).setPositiveButton("Salir",(d,w)->{
   busy=true;render();repo.change(gameId,-2).addOnSuccessListener(r->{busy=false;getPreferences(0).edit().remove("gameV2Id").apply();gameId=null;if(!isDestroyed())lobby();}).addOnFailureListener(this::failure);
  }).show();
 }
 void backToLobby(){gameId=null;getPreferences(0).edit().remove("gameV2Id").apply();lobby();}
 void render(){
  if(current==null||board==null)return;
  if(!current.exists()){status.setText("La partida ya no existe.");board.setEnabled(false);return;}
  String host=current.getString("hostName"),guest=current.getString("guestName");
  String mark=repo.uid().equals(current.getString("host"))?"X":repo.uid().equals(current.getString("guest"))?"O":"";
  String phase=current.getString("status"),turn=current.getString("turn");
  hostLabel.setText("X · "+host);guestLabel.setText("O · "+(guest==null||guest.isEmpty()?"Esperando":guest));
  roomTitle.setText(current.getString("name"));
  game.restore(android.text.TextUtils.join("",(java.util.List<String>)current.get("board")));board.invalidate();
  String playerTurn="X".equals(turn)?host:guest;
  String s="waiting".equals(phase)?"Esperando al segundo jugador…":"playing".equals(phase)?(mark.equals(turn)?"¡Tu turno, "+playerTurn+"!":"Turno de "+playerTurn):"draw".equals(phase)?"¡Empate!":"closed".equals(phase)?"Partida cerrada":"Ganó "+("X".equals(phase)?host:guest);
  status.setText(!connected?"Sin conexión · Reconectando…":busy?"Enviando…":s);
  detail.setText("waiting".equals(phase)?"Copia el código y envíalo a tu rival.":"playing".equals(phase)?"Eres "+mark+" · "+(mark.equals(turn)?"Elige una casilla libre.":"Espera la jugada del otro jugador."):"Vuelve a la lista para crear otra partida.");
  board.setEnabled(connected&&!busy&&!mark.isEmpty()&&"playing".equals(phase)&&mark.equals(turn));
 }
 void failure(Exception error){
  busy=false;if(isDestroyed()||isFinishing())return;
  Throwable reason=error;while(reason.getCause()!=null)reason=reason.getCause();
  Toast.makeText(this,reason.getMessage(),Toast.LENGTH_LONG).show();
  if(board!=null)render();else status.setText(reason.getMessage());
 }
 @Override protected void onPause(){
  if(playerInput!=null){String name=playerInput.getText().toString().trim();if(!name.isEmpty())getPreferences(0).edit().putString("playerName",name).apply();}
  super.onPause();
 }
 @Override public void onBackPressed(){
  if(gameId!=null)new AlertDialog.Builder(this).setMessage("¿Cerrar la app? Puedes reanudar esta partida al abrirla.").setPositiveButton("Cerrar",(d,w)->finish()).setNegativeButton("Continuar",null).show();
  else super.onBackPressed();
 }
 @Override protected void onDestroy(){if(listener!=null)listener.remove();super.onDestroy();}
}
