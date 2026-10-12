package com.hernandovela.reto7;
import android.app.*;
import android.content.*;
import android.os.Bundle;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.concurrent.TimeUnit;
public final class OnlineSmokeInstrumentation extends Instrumentation {
 @Override public void onCreate(Bundle args){super.onCreate(args);start();}
 @Override public void onStart(){
  Bundle result=new Bundle();int exitCode=Activity.RESULT_CANCELED;String room=null;OnlineRepository host=null;FirebaseApp guestApp=null;MainActivity activity=null;
  Context c=getTargetContext();SharedPreferences prefs=c.getSharedPreferences("MainActivity",0);
  String previousRoom=prefs.getString("gameV2Id",null),previousName=prefs.getString("playerName",null);
  try{
   if(FirebaseApp.getApps(c).isEmpty())FirebaseApp.initializeApp(c,FirebaseConfig.load(c));
   if(FirebaseAuth.getInstance().getCurrentUser()==null)Tasks.await(FirebaseAuth.getInstance().signInAnonymously(),30,TimeUnit.SECONDS);
   guestApp=FirebaseApp.initializeApp(c,FirebaseConfig.load(c),"verification-"+System.nanoTime());
   Tasks.await(FirebaseAuth.getInstance(guestApp).signInAnonymously(),30,TimeUnit.SECONDS);
   host=new OnlineRepository();OnlineRepository guest=new OnlineRepository(guestApp);
   DocumentReference ref=Tasks.await(host.create("Verificación Android 1.1","Ana"),30,TimeUnit.SECONDS);room=ref.getId();
   if(!RoomCode.valid(room))throw new AssertionError("Código inválido");
   try{Tasks.await(host.change(room,0),10,TimeUnit.SECONDS);throw new AssertionError("Jugó sin rival");}catch(java.util.concurrent.ExecutionException expected){}
   Tasks.await(guest.join(RoomCode.display(room).toLowerCase(java.util.Locale.ROOT),"Luis"),30,TimeUnit.SECONDS);
   DocumentSnapshot document=Tasks.await(ref.get(Source.SERVER),30,TimeUnit.SECONDS);
   if(!"Ana".equals(document.getString("hostName"))||!"Luis".equals(document.getString("guestName")))throw new AssertionError("Nombres incorrectos");
   prefs.edit().putString("gameV2Id",room).putString("playerName","Ana").commit();
   activity=(MainActivity)startActivitySync(new Intent(c,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
   final MainActivity first=activity;
   long deadline=System.currentTimeMillis()+15000;
   while((first.current==null||!first.connected)&&System.currentTimeMillis()<deadline)Thread.sleep(100);
   runOnMainSync(()->{
    if(first.current==null||!first.connected)throw new AssertionError("No llegó el estado online");
    if(!first.hostLabel.getText().toString().contains("Ana")||!first.guestLabel.getText().toString().contains("Luis"))throw new AssertionError("Nombres no visibles");
    first.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
   });
   waitForIdleSync();
   Tasks.await(host.change(room,0),30,TimeUnit.SECONDS);
   Tasks.await(guest.change(room,4),30,TimeUnit.SECONDS);
   DocumentSnapshot remote=Tasks.await(guest.games().document(room).get(Source.SERVER),30,TimeUnit.SECONDS);
   java.util.List<?> board=(java.util.List<?>)remote.get("board");
   if(!"X".equals(board.get(0))||!"O".equals(board.get(4)))throw new AssertionError("Jugadas no sincronizadas");
   Tasks.await(host.change(room,-2),30,TimeUnit.SECONDS);room=null;
   result.putString("stream","OK 1.1.0: código formateado, dos usuarios Android, nombres visibles, orientación horizontal y jugadas sincronizadas.\n");
   exitCode=Activity.RESULT_OK;
  }catch(Throwable error){
   if(room!=null&&host!=null)try{Tasks.await(host.change(room,-2),10,TimeUnit.SECONDS);}catch(Exception ignored){}
   result.putString("stream","FAIL: "+error+"\n");
  }finally{
   if(guestApp!=null)guestApp.delete();
   SharedPreferences.Editor edit=prefs.edit();
   if(previousRoom==null)edit.remove("gameV2Id");else edit.putString("gameV2Id",previousRoom);
   if(previousName==null)edit.remove("playerName");else edit.putString("playerName",previousName);edit.commit();
  }
  finish(exitCode,result);
 }
}
