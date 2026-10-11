package com.hernandovela.reto7;
import android.content.Context;
import com.google.firebase.FirebaseOptions;
import org.json.*;
import java.io.*;
import java.util.Properties;
final class FirebaseConfig {
 static FirebaseOptions load(Context context) throws Exception {
  try(InputStream input=context.getAssets().open("google-services.json")){
   ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] buffer=new byte[4096];int size;
   while((size=input.read(buffer))!=-1)bytes.write(buffer,0,size);
   JSONObject json=new JSONObject(bytes.toString("UTF-8"));
   if(!"reto7jugandotriquionline".equals(json.getJSONObject("project_info").getString("project_id")))
    throw new IllegalArgumentException("google-services.json pertenece a otro proyecto.");
   JSONArray clients=json.getJSONArray("client");
   for(int i=0;i<clients.length();i++){
    JSONObject client=clients.getJSONObject(i),info=client.getJSONObject("client_info");
    if(context.getPackageName().equals(info.getJSONObject("android_client_info").getString("package_name")))
     return new FirebaseOptions.Builder().setProjectId("reto7jugandotriquionline")
      .setApplicationId(info.getString("mobilesdk_app_id"))
      .setApiKey(client.getJSONArray("api_key").getJSONObject(0).getString("current_key")).build();
   }
   throw new IllegalArgumentException("No existe la app com.hernandovela.reto7 en google-services.json.");
  }catch(FileNotFoundException missing){
   Properties p=new Properties();
   try(InputStream input=context.getAssets().open("firebase.properties")){p.load(input);}
   return new FirebaseOptions.Builder().setProjectId(p.getProperty("projectId"))
    .setApplicationId(p.getProperty("applicationId")).setApiKey(p.getProperty("apiKey")).build();
  }
 }
}
