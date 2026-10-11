package com.hernandovela.reto7;
import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.*;
import com.google.firebase.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
public final class MainActivity extends Activity {
 OnlineRepository repo; ListenerRegistration listener;
 LinearLayout content,rooms; TextView status; BoardView board;
 String gameId; boolean busy,connected; DocumentSnapshot current;
 final TicTacToeGame game=new TicTacToeGame();
 @Override public void onCreate(Bundle state){
  super.onCreate(state);gameId=getPreferences(0).getString("gameId",null);
  shell("Conectando con Firebase…");
  try{
   if(FirebaseApp.getApps(this).isEmpty()){
    java.util.Properties p=new java.util.Properties();
    try(java.io.InputStream in=getAssets().open("firebase.properties")){p.load(in);}
    FirebaseApp.initializeApp(this,new FirebaseOptions.Builder().setProjectId(p.getProperty("projectId")).setApplicationId(p.getProperty("applicationId")).setApiKey(p.getProperty("apiKey")).build());
   }
   repo=new OnlineRepository();
   FirebaseAuth a=FirebaseAuth.getInstance();
   if(a.getCurrentUser()!=null)ready();else a.signInAnonymously().addOnSuccessListener(r->ready()).addOnFailureListener(this::failure);
  }catch(Exception e){status.setText("Falta configurar Firebase");label("Consulta README.md y configura app/src/main/assets/firebase.properties con los datos de la aplicación Android registrada en Firebase.");}
 }
 int dp(int n){return (int)(n*getResources().getDisplayMetrics().density);}
 TextView label(String s){TextView v=new TextView(this);v.setText(s);v.setTextSize(18);v.setTextColor(Color.WHITE);v.setPadding(0,dp(8),0,dp(8));content.addView(v);return v;}
 void shell(String title){
  if(listener!=null){listener.remove();listener=null;}
  ScrollView scroll=new ScrollView(this);content=new LinearLayout(this);content.setOrientation(1);content.setPadding(dp(22),dp(36),dp(22),dp(24));content.setBackgroundColor(Color.rgb(15,23,42));scroll.addView(content);setContentView(scroll);label("TRIQUI ONLINE").setTextSize(28);status=label(title);board=null;
 }
 void button(String s,Runnable action){Button b=new Button(this);b.setText(s);content.addView(b);b.setOnClickListener(v->action.run());}
 void ready(){if(isDestroyed()||isFinishing())return;if(gameId==null)lobby();else watch();}
 void select(String id){gameId=id;getPreferences(0).edit().putString("gameId",id).apply();watch();}
 void lobby(){
  shell("Partidas disponibles · 2 jugadores");
  EditText name=new EditText(this);name.setHint("Nombre de la partida");name.setTextColor(Color.WHITE);name.setHintTextColor(Color.LTGRAY);content.addView(name);
  button("Crear partida",()->{if(busy)return;busy=true;String s=name.getText().toString().trim();if(s.isEmpty())s="Partida de triqui";if(s.length()>40)s=s.substring(0,40);repo.create(s).addOnSuccessListener(r->{busy=false;select(r.getId());}).addOnFailureListener(this::failure);});
  rooms=new LinearLayout(this);rooms.setOrientation(1);content.addView(rooms);
  listener=repo.games().whereEqualTo("status","waiting").limit(50).addSnapshotListener(MetadataChanges.INCLUDE,(snapshot,error)->{
   if(error!=null){failure(error);return;}if(gameId!=null)return;rooms.removeAllViews();
   status.setText(snapshot.getMetadata().isFromCache()?"Reconectando… lista guardada":"Partidas disponibles · 2 jugadores");
   if(snapshot.isEmpty()){TextView empty=new TextView(this);empty.setText("No hay partidas. ¡Crea la primera!");empty.setTextColor(Color.WHITE);rooms.addView(empty);}
   for(DocumentSnapshot g:snapshot.getDocuments()){
    boolean own=repo.uid().equals(g.getString("host"));Button b=new Button(this);b.setText(g.getString("name")+(own?" · Reanudar":" · Unirse"));rooms.addView(b);
    b.setOnClickListener(v->{if(busy)return;if(own){select(g.getId());return;}busy=true;repo.change(g.getId(),-1).addOnSuccessListener(r->{busy=false;select(g.getId());}).addOnFailureListener(this::failure);});
   }
  });
 }
 void watch(){
  shell("Cargando partida…");label("Código: "+gameId);
  board=new BoardView(this);board.setGame(game);board.setEnabled(false);content.addView(board,new LinearLayout.LayoutParams(-1,dp(320)));
  board.setMoveListener(cell->{if(busy||!connected)return;busy=true;board.setEnabled(false);repo.change(gameId,cell).addOnSuccessListener(r->{busy=false;render();}).addOnFailureListener(this::failure);});
  button("Salir y volver a la lista",()->new AlertDialog.Builder(this).setMessage("Salir cierra la partida para ambos jugadores.").setNegativeButton("Cancelar",null).setPositiveButton("Salir",(d,w)->{
   if(busy)return;busy=true;repo.change(gameId,-2).addOnSuccessListener(r->{busy=false;gameId=null;current=null;getPreferences(0).edit().remove("gameId").apply();lobby();}).addOnFailureListener(this::failure);
  }).show());
  listener=repo.games().document(gameId).addSnapshotListener(MetadataChanges.INCLUDE,(g,e)->{if(e!=null){failure(e);return;}current=g;connected=!g.getMetadata().isFromCache();render();});
 }
 void render(){
  if(current==null||board==null)return;
  if(!current.exists()){status.setText("La partida no existe.");board.setEnabled(false);button("Volver a la lista",()->{gameId=null;getPreferences(0).edit().remove("gameId").apply();lobby();});return;}
  String mark=repo.uid().equals(current.getString("host"))?"X":repo.uid().equals(current.getString("guest"))?"O":"";
  String phase=current.getString("status"),turn=current.getString("turn");
  game.restore(String.join("",(java.util.List<String>)current.get("board")));board.invalidate();
  String s="waiting".equals(phase)?"Eres X · Esperando al segundo jugador…":"playing".equals(phase)?"Eres "+mark+" · "+(mark.equals(turn)?"Tu turno":"Turno del rival"):"draw".equals(phase)?"¡Empate!":"closed".equals(phase)?"Partida cerrada":mark.equals(phase)?"¡Ganaste!":"Ganó "+phase;
  status.setText(connected?s:"Sin conexión · Reconectando…");board.setEnabled(connected&&!busy&&!mark.isEmpty()&&"playing".equals(phase)&&mark.equals(turn));
 }
 void failure(Exception e){busy=false;if(isDestroyed()||isFinishing())return;Toast.makeText(this,"No se pudo completar: "+e.getMessage(),Toast.LENGTH_LONG).show();if(board!=null)render();else status.setText("Error de conexión. Revisa Internet y Firebase.");}
 @Override public void onBackPressed(){if(gameId!=null)new AlertDialog.Builder(this).setMessage("La partida se conservará para reanudar al abrir la app.").setPositiveButton("Cerrar app",(d,w)->finish()).setNegativeButton("Continuar",null).show();else super.onBackPressed();}
 @Override protected void onDestroy(){if(listener!=null)listener.remove();super.onDestroy();}
}
