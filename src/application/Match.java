package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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


class kdaWinLoseTime{
	public double kda;
	public boolean win;
	public long time;
	public kdaWinLoseTime(double kda,boolean win,long time) {
		this.kda=kda;
		this.win=win;
		this.time=time;
	}
}
public class Match {
	static String matchID;
	static String api_key;
	static String accountID;
	public Match(String matchID,String accountID,String api) {
		api_key=api;
		this.accountID=accountID;
		this.matchID=matchID;
	}
	public kdaWinLoseTime getGameStats() throws ForbiddenException,ServiceUnavailableException, RateLimitException, InternalServerException, JsonIOException, JsonSyntaxException, IOException{
		
		String sURL = "https://na1.api.riotgames.com/lol/match/v4/matches/"+matchID+"?api_key="+api_key; //just a string
		// Connect to the URL using java's native library
		URL url = null;
		HttpURLConnection request = null;
		try {
			url = new URL(sURL);
			request = (HttpURLConnection) url.openConnection();
			request.connect();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			System.out.println("MalformedURL");
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("IOException "+e1.getMessage());
		}
		
		int response=request.getResponseCode();
		if(response==429) {
			throw new RateLimitException("429");
		}else if(response==500) {
			throw new InternalServerException("500");
		}else if(response==503) {
			throw new ServiceUnavailableException("503");
		}else if(response==404) {
			throw new ForbiddenException("404");
		}
		// Convert to a JSON object to print data
		JsonParser jp = new JsonParser(); //from gson
		JsonElement root = null;
		try {
			root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		} catch (JsonIOException e) {
			System.out.println("JsonIOException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			System.out.println("SyntaxException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//Convert the input stream to a json element
		JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
		long time=Long.parseLong(rootobj.get("gameCreation").getAsString());
		JsonArray participantList=rootobj.get("participantIdentities").getAsJsonArray();
		String participantID="-1";
		for(int i=0;i<participantList.size();i++) {
			if(((JsonObject) participantList.get(i)).get("player").getAsJsonObject().get("accountId").getAsString().equals(accountID)) {
				participantID=((JsonObject) participantList.get(i)).get("participantId").getAsString();
				break;
			}
		}
		
		//get (k+d)/a
		int kill=-1;
		int assist=-1;
		int death=-1;
		String winOrLose="dk";
		JsonArray statsList=rootobj.get("participants").getAsJsonArray();
		for(int i=0;i<statsList.size();i++) {
			if(((JsonObject) statsList.get(i)).get("stats").getAsJsonObject().get("participantId").getAsString().equals(participantID)) {
				JsonObject thisPlayer=((JsonObject) statsList.get(i)).get("stats").getAsJsonObject();
				kill=Integer.parseInt(thisPlayer.get("kills").getAsString());
				assist=Integer.parseInt(thisPlayer.get("assists").getAsString());
				death=Integer.parseInt(thisPlayer.get("deaths").getAsString());
				winOrLose=thisPlayer.get("win").getAsString();
			}
		}
		death=death+1;
		double kda=(kill+assist)/(double)death;
		
		boolean win;
		if(winOrLose.equals("true")) {
			win=true;
		}
		else {
			win=false;
		}
		
		return new kdaWinLoseTime(kda,win,time);
	}

}