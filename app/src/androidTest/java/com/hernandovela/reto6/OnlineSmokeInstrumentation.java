package com.hernandovela.reto7;
import android.app.*;
import android.content.*;
import android.os.Bundle;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.concurrent.TimeUnit;
/** Uses the real Android SDK and configured cloud, then closes its disposable test room. */
public final class OnlineSmokeInstrumentation extends Instrumentation {
 @Override public void onCreate(Bundle args){super.onCreate(args);start();}
 @Override public void onStart(){
  Bundle result=new Bundle();String room=null;OnlineRepository repo=null;
  try{
   Context c=getTargetContext();
   if(FirebaseApp.getApps(c).isEmpty())FirebaseApp.initializeApp(c,FirebaseConfig.load(c));
   if(FirebaseAuth.getInstance().getCurrentUser()==null)
    Tasks.await(FirebaseAuth.getInstance().signInAnonymously(),30,TimeUnit.SECONDS);
   repo=new OnlineRepository();
   DocumentReference ref=Tasks.await(repo.create("Verificación Android · Reto 7"),30,TimeUnit.SECONDS);room=ref.getId();
   DocumentSnapshot game=Tasks.await(ref.get(Source.SERVER),30,TimeUnit.SECONDS);
   if(!repo.uid().equals(game.getString("host"))||!"waiting".equals(game.getString("status")))
    throw new AssertionError("Estado inicial incorrecto");
   Activity activity=startActivitySync(new Intent(c,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
   waitForIdleSync();runOnMainSync(activity::recreate);waitForIdleSync();runOnMainSync(activity::finish);
   Tasks.await(repo.change(room,-2),30,TimeUnit.SECONDS);room=null;
   result.putString("stream","OK: autenticación Android, creación y lectura desde Firestore, recreación de Activity y cierre de partida.\n");
   finish(Activity.RESULT_OK,result);
  }catch(Exception error){
   if(room!=null&&repo!=null)try{Tasks.await(repo.change(room,-2),10,TimeUnit.SECONDS);}catch(Exception ignored){}
   result.putString("stream","FAIL: "+error+"\n");finish(Activity.RESULT_CANCELED,result);
  }
 }
}
