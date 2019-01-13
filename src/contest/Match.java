package contest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Match {
	static String matchID;
	static String api_key;
	static String summonerID;
	public Match(String matchID,String summonerID,String api) {
		api_key=api;
		this.summonerID=summonerID;
		this.matchID=matchID;
	}
	public double getKDA() {
		
		String sURL = "https://na1.api.riotgames.com/lol/match/v4/matches/"+matchID+"?api_key="+api_key; //just a string
		// Connect to the URL using java's native library
		URL url = null;
		URLConnection request = null;
		try {
			url = new URL(sURL);
			request = url.openConnection();
			request.connect();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			System.out.println("MalformedURL");
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("IOException "+e1.getMessage());
		}
		
		// Convert to a JSON object to print data
		JsonParser jp = new JsonParser(); //from gson
		JsonElement root = null;
		try {
			root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//Convert the input stream to a json element
		JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
		JsonArray participantList=rootobj.get("participantIdentities").getAsJsonArray();
		String participantID="-1";
		for(int i=0;i<participantList.size();i++) {
			if(((JsonObject) participantList.get(i)).get("player").getAsJsonObject().get("summonerId").getAsString().equals(summonerID)) {
				participantID=((JsonObject) participantList.get(i)).get("participantId").getAsString();
			}
		}
		
		//get (k+d)/a
		int kill=-1;
		int assist=-1;
		int death=-1;
		JsonArray statsList=rootobj.get("participants").getAsJsonArray();
		for(int i=0;i<statsList.size();i++) {
			if(((JsonObject) statsList.get(i)).get("stats").getAsJsonObject().get("participantId").getAsString().equals(participantID)) {
				JsonObject thisPlayer=((JsonObject) statsList.get(i)).get("stats").getAsJsonObject();
				kill=Integer.parseInt(thisPlayer.get("kills").getAsString());
				assist=Integer.parseInt(thisPlayer.get("assists").getAsString());
				death=Integer.parseInt(thisPlayer.get("deaths").getAsString());
			}
		}
		death=death+1;
		return (kill+assist)/(double)death;
	}

}
