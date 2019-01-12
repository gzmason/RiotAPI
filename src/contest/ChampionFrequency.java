package contest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class ChampionFrequency {
	final static String api_key="RGAPI-7b53ac55-13c3-4d36-b6f4-b05b2c36a6f7";
	static String summonerID=null;
	static String accountID=null;
	static Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
			.create();
	static Map<Integer,String> champIDMap;
	
	public static void SummonerIDbyName () {
		System.out.println("Please enter your summoner name: ");
		Scanner scan=new Scanner(System.in);
		String summonerName=scan.nextLine();
		String sURL = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+summonerName+"?api_key="+api_key; //just a string
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
		summonerID = rootobj.get("id").getAsString(); //just grab the name
		System.out.println("Your encrypted summonerID is "+summonerID);
		accountID=rootobj.get("accountId").getAsString(); 
		System.out.println("Your encrypted accountID is "+accountID);
	}
	public static Map<Integer,String> getChampName(){
		Map<Integer,String> res=new HashMap<>();
		String sURL = "http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json";
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
		JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
		String dataString = rootobj.get("data").toString(); 
		for(Entry<String, JsonElement> champ:rootobj.get("data").getAsJsonObject().entrySet()) {
			String champName=champ.getKey();
			String idQuotation=champ.getValue().getAsJsonObject().get("key").toString();
			Integer id=Integer.parseInt(idQuotation.substring(1, idQuotation.length()-1));
			res.put(id, champName);
		}
		
		return res;
	}
	public static Map<String,Integer> getChampFreq() {
		Map<String,Integer> freqMap=new HashMap<>();
		String sURL = "https://na1.api.riotgames.com/lol/match/v4/matchlists/by-account/"+accountID+"?api_key="+api_key; //just a string
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
		List<String> list = new ArrayList<String>();
		JsonArray array = rootobj.getAsJsonArray("matches");
		for(int i=0;i<array.size();i++) {
			int championID=((JsonObject) array.get(i)).get("champion").getAsInt();
			freqMap.put(champIDMap.get(championID), freqMap.getOrDefault(championID,0)+1);
		}
		return freqMap;
	}
	public static void main(String[] args) {
		champIDMap=getChampName();
		
		SummonerIDbyName();
		Map<String,Integer> freqMap=getChampFreq();
		for(Map.Entry<String, Integer> entry:freqMap.entrySet()) {
			System.out.println("Champion: "+entry.getKey()+"   Times: "+entry.getValue());
		}
		
	}
	
}