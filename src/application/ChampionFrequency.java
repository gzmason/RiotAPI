package application;

import java.io.IOException;
import java.time.Instant;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
	final static String api_key="RGAPI-635bcc8d-7026-496f-a95a-f428583efdb3";
	static String summonerID=null;
	static String accountID=null;
	static Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
			.create();
	static Map<Integer,String> champIDMap;
	static Map<String,Double> kdaMap=new HashMap<String,Double>();
	static Map<String,Integer> freqMap=new HashMap<String,Integer>();
	static Map<String,Long> lastPlay=new HashMap<String,Long>();
	static Map<String,ArrayList<Integer>> champFeatureMap=new HashMap<String,ArrayList<Integer>>();
	static ArrayList<Double> playerFeature=new ArrayList<Double>(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0));
	static Set<String> playedChampion=new HashSet<>();
	
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
		Map<Integer,String> idToName=new HashMap<>();
		Map<String,ArrayList<Integer>> nameToFeature=new HashMap<>();
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
		for(Entry<String, JsonElement> champ:rootobj.get("data").getAsJsonObject().entrySet()) {
			//add to idToName
			String champName=champ.getKey();
			String idQuotation=champ.getValue().getAsJsonObject().get("key").toString();
			Integer id=Integer.parseInt(idQuotation.substring(1, idQuotation.length()-1));
			idToName.put(id, champName);
			
			//add to nameToFeature
			ArrayList<Integer> thisFeature=new ArrayList<Integer>();
			int attack=champ.getValue().getAsJsonObject().get("info").getAsJsonObject().get("attack").getAsInt();
			int defense=champ.getValue().getAsJsonObject().get("info").getAsJsonObject().get("defense").getAsInt();
			int magic=champ.getValue().getAsJsonObject().get("info").getAsJsonObject().get("magic").getAsInt();
			int difficulty=champ.getValue().getAsJsonObject().get("info").getAsJsonObject().get("difficulty").getAsInt();
			thisFeature.add(attack);
			thisFeature.add(defense);
			thisFeature.add(magic);
			thisFeature.add(difficulty);
			
			boolean assassin=false;
			boolean fighter=false;
			boolean mage=false;
			boolean tank=false;
			boolean support=false;
			boolean marksman=false;
			JsonArray array = champ.getValue().getAsJsonObject().get("tags").getAsJsonArray();
			int tagCount=0;
			for(int i=0;i<array.size();i++) {
				if(array.get(i).getAsString().equals("Assassin")) {
					assassin=true;
					tagCount++;
				}
				else if (array.get(i).getAsString().equals("Fighter")) {
					fighter=true;
					tagCount++;
				}
				else if (array.get(i).getAsString().equals("Mage")) {
					mage=true;
					tagCount++;
				}
				else if (array.get(i).getAsString().equals("Tank")) {
					tank=true;
					tagCount++;
				}
				else if (array.get(i).getAsString().equals("Support")) {
					support=true;
					tagCount++;
				}
				else if (array.get(i).getAsString().equals("Marksman")) {
					marksman=true;
					tagCount++;
				}
			}
			int tagValue=15/tagCount;
			if(assassin) {
				thisFeature.add(tagValue);
			}else {
				thisFeature.add(0);
			}
			if(fighter) {
				thisFeature.add(tagValue);
			}else {
				thisFeature.add(0);
			}
			if(mage) {
				thisFeature.add(tagValue);
			}else {
				thisFeature.add(0);
			}
			if(tank) {
				thisFeature.add(tagValue);
			}else {
				thisFeature.add(0);
			}
			if(support) {
				thisFeature.add(tagValue);
			}else {
				thisFeature.add(0);
			}
			if(marksman) {
				thisFeature.add(tagValue);
			}else {
				thisFeature.add(0);
			}
			nameToFeature.put(champName, thisFeature);
		}
		
		champIDMap=idToName;
		champFeatureMap=nameToFeature;
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
		
		for(int i=0;i<array.size()-2;i++) { //for each match
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
			
			//update playerFeature
			ArrayList<Integer> championFeature=champFeatureMap.get(championName);
			for(int j=0;j<10;j++) {
				playerFeature.set(j, playerFeature.get(j)+championFeature.get(j));
			}
			//update playedChampion
			playedChampion.add(championName);
		}
		for(int j=0;j<10;j++) {
			playerFeature.set(j, playerFeature.get(j)/(array.size()-2));
		}
		Map<String,Double> res=new HashMap<String,Double>();
		for(String champName:kdaMap.keySet()) {
			res.put(champName, kdaMap.get(champName)/freqMap.get(champName));
		}
		return res;
	}
	
	public static ArrayList<String> recommendNew(){
		ArrayList<String> res=new ArrayList<String>();
		Map<String,Double> differenceMap=new HashMap<>();
		for (Map.Entry <String,ArrayList<Integer>> entry : champFeatureMap.entrySet()) { //for every champion
			String championName=entry.getKey();
			//System.out.println("Champion: "+championName+"   "+entry.getValue());
			//System.out.println("Now looking at: "+championName);
			//if the champion has been played, skip it
			if(playedChampion.contains(championName)) {
				continue;
			}
			ArrayList<Integer> championFeature=entry.getValue();
			double difference=0;
			for(int i=0;i<10;i++) { //for each feature
				if(i==3) { //if it is the difficulty feature
					//if the player used to play hard champions, he can easily handle easier ones
					difference=difference+(championFeature.get(i)-playerFeature.get(i));
				}
				else { //for other features
					difference=difference+Math.pow((championFeature.get(i)-playerFeature.get(i)),2);
				}
			}
			System.out.println("champion: "+championName+"   difference: "+difference);
			differenceMap.put(championName, difference);	
		}
		List<Entry<String, Double>> list = new ArrayList<>(differenceMap.entrySet());
        list.sort(Entry.comparingByValue());
        //Map<String, Double> sortedMap = new LinkedHashMap<>();
        for(int i=0;i<Math.min(5,list.size());i++) {
        	System.out.println(i+"   name: "+list.get(i).getKey()+"   difference: "+list.get(i).getValue());
        	res.add(list.get(i).getKey());
        }
		return res;
	}
	
	public static void main(String[] args) {
		long currentTime=System.currentTimeMillis();
		getChampName();
		//SummonerIDbyName();
		//Map<String,Double> kdaResult=getChampKDA();
		/*for(Map.Entry<String, ArrayList<Integer>> entry:champFeatureMap.entrySet()) {
			String championName=entry.getKey();
			//long timeDifference=currentTime-lastPlay.get(championName).longValue();
			System.out.println("Champion: "+championName+"   "+entry.getValue());
		}*/
	}
	
}