package application;

import java.io.IOException;
import java.time.Instant;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
	final static String api_key="RGAPI-4f9c7ce3-4030-4e79-b51a-8f7929578f48";
	static String summonerID=null;
	static String accountID=null;
	static Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
			.create();
	static Map<Integer,String> champIDMap;
	static Map<String,Double> kdaMap=new HashMap<String,Double>();
	static Map<String,Integer> freqMap=new HashMap<String,Integer>();
	static Map<String,Long> lastPlay=new HashMap<String,Long>();
	
	public static void SummonerIDbyName (String name) {
		//System.out.println("Please enter your summoner name: ");
		//Scanner scan=new Scanner(System.in);
		String summonerName=name;
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
	public static void getChampName(){
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
		champIDMap=res;
	}
	
	public static Map<String,Double> getChampKDA() {

		
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
		for(int i=0;i<98;i++) {
			int championID=((JsonObject) array.get(i)).get("champion").getAsInt();
			String championName=champIDMap.get(championID);
			String matchID=((JsonObject) array.get(i)).get("gameId").getAsString();
			Match m=new Match(matchID,summonerID,api_key);
			kdaWinLoseTime wlt=m.getGameStats();
			double winBonus=0;
			if(wlt.win) {
				winBonus=1.5;
			}
			kdaMap.put(championName, kdaMap.getOrDefault(championName, 0.0)+wlt.kda+winBonus);
			freqMap.put(championName, freqMap.getOrDefault(championName,0)+1);
			
			if(!lastPlay.containsKey(championName)) {
				lastPlay.put(championName, wlt.time);
			}
			else {
				if(lastPlay.get(championName)<wlt.time) {
					lastPlay.put(championName, wlt.time);
				}
			}
		}
		Map<String,Double> res=new HashMap<String,Double>();
		for(String champName:kdaMap.keySet()) {
			res.put(champName, kdaMap.get(champName)/freqMap.get(champName));
		}
		return res;
	}

	public static Map<String,Double> usedChampFinalRank(){
		Map<String,Double> rank = getChampKDA();
		
		//Numerize kda as part of the final value
		rank.forEach((champ,kda) -> {
			rank.put(
					champ, 
					(double)
					// KDA of the champ, Having good gaming experience? WinLose incorporated
					(kda*1.2 + 
					//Level of proficiency, Likeness of the champ
					freqMap.get(champ) * 0.5 - 
					//Doesn't prefer using champs have used recently.
					5 / ((System.currentTimeMillis() - lastPlay.get(champ)) / (1000*60*60*24) ) + 
					//If one hasn't use for a while, gradully grow the value of it.
					((System.currentTimeMillis() - lastPlay.get(champ)) / (1000*60*60*24) ) > 8 ? 
							((System.currentTimeMillis() - lastPlay.get(champ)) / (1000*60*60*24) ) * 0.2: 0));
		});
		
		Map<String, Double> finalSortedRank = rank.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

	
		
		return finalSortedRank;
	}
	
	
}