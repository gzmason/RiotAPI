package application;

import java.io.IOException;
import java.time.Instant;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import java.util.Comparator;

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

	final static String api_key = "RGAPI-c7663ff9-3431-4e8c-88b6-0f3ec75efdb4";

	static String summonerID = null;
	static String accountID = null;
	static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
	static Map<Integer, String> champIDMap;
	static Map<String, Double> kdaMap = new HashMap<String, Double>();
	static Map<String, Integer> freqMap = new HashMap<String, Integer>();
	static Map<String, Long> lastPlay = new HashMap<String, Long>();
	static Map<String, ArrayList<Integer>> champFeatureMap = new HashMap<String, ArrayList<Integer>>();
	static ArrayList<Double> playerFeature = new ArrayList<Double>(
			Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
	static Set<String> playedChampion = new HashSet<>();
	static Map<String, Integer> tagFreq = new HashMap<String, Integer>();
	static Map<String, Integer> winLose = new HashMap<String, Integer>();

	public static void SummonerIDbyName(String name) throws ForbiddenException, ServiceUnavailableException,
			RateLimitException, InternalServerException, JsonIOException, JsonSyntaxException, IOException {
		String summonerName = name;
		String sURL = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName + "?api_key="
				+ api_key; // just a string
		// Connect to the URL using java's native library
		URL url = null;
		HttpURLConnection request = null;
		// try {
		url = new URL(sURL);
		request = (HttpURLConnection) url.openConnection();
		request.connect();

		int response = request.getResponseCode();
		if (response == 429) {
			throw new RateLimitException("429");
		} else if (response == 500) {
			throw new InternalServerException("500");
		} else if (response == 503) {
			throw new ServiceUnavailableException("503");
		} else if (response == 404) {
			throw new ForbiddenException("404");
		}

		// Convert to a JSON object to print data
		JsonParser jp = new JsonParser(); // from gson
		JsonElement root = null;

		root = jp.parse(new InputStreamReader((InputStream) request.getContent()));

		// Convert the input stream to a json element
		JsonObject rootobj = root.getAsJsonObject(); // May be an array, may be an object.
		summonerID = rootobj.get("id").getAsString(); // just grab the name
		System.out.println("Your encrypted summonerID is " + summonerID);
		accountID = rootobj.get("accountId").getAsString();
		System.out.println("Your encrypted accountID is " + accountID);
	}

	public static void getChampName() throws JsonIOException, JsonSyntaxException, IOException {
		Map<Integer, String> idToName = new HashMap<>();
		Map<String, ArrayList<Integer>> nameToFeature = new HashMap<>();
		String sURL = "http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json";
		// Connect to the URL using java's native library
		URL url = null;
		URLConnection request = null;
		url = new URL(sURL);
		request = url.openConnection();
		request.connect();


		// Convert to a JSON object to print data
		JsonParser jp = new JsonParser(); // from gson
		JsonElement root = null;

		root = jp.parse(new InputStreamReader((InputStream) request.getContent()));

		JsonObject rootobj = root.getAsJsonObject(); // May be an array, may be an object.
		for (Entry<String, JsonElement> champ : rootobj.get("data").getAsJsonObject().entrySet()) {
			// add to idToName
			String champName = champ.getKey();
			String idQuotation = champ.getValue().getAsJsonObject().get("key").toString();
			Integer id = Integer.parseInt(idQuotation.substring(1, idQuotation.length() - 1));
			idToName.put(id, champName);

			// add to nameToFeature
			ArrayList<Integer> thisFeature = new ArrayList<Integer>();
			int attack = champ.getValue().getAsJsonObject().get("info").getAsJsonObject().get("attack").getAsInt();
			int defense = champ.getValue().getAsJsonObject().get("info").getAsJsonObject().get("defense").getAsInt();
			int magic = champ.getValue().getAsJsonObject().get("info").getAsJsonObject().get("magic").getAsInt();
			int difficulty = champ.getValue().getAsJsonObject().get("info").getAsJsonObject().get("difficulty")
					.getAsInt();
			thisFeature.add(attack);
			thisFeature.add(defense);
			thisFeature.add(magic);
			thisFeature.add(difficulty);

			boolean assassin = false;
			boolean fighter = false;
			boolean mage = false;
			boolean tank = false;
			boolean support = false;
			boolean marksman = false;
			JsonArray array = champ.getValue().getAsJsonObject().get("tags").getAsJsonArray();
			int tagCount = 0;
			for (int i = 0; i < array.size(); i++) {
				if (array.get(i).getAsString().equals("Assassin")) {
					assassin = true;
					tagCount++;
				} else if (array.get(i).getAsString().equals("Fighter")) {
					fighter = true;
					tagCount++;
				} else if (array.get(i).getAsString().equals("Mage")) {
					mage = true;
					tagCount++;
				} else if (array.get(i).getAsString().equals("Tank")) {
					tank = true;
					tagCount++;
				} else if (array.get(i).getAsString().equals("Support")) {
					support = true;
					tagCount++;
				} else if (array.get(i).getAsString().equals("Marksman")) {
					marksman = true;
					tagCount++;
				}
			}
			int tagValue = 20 / tagCount;
			if (assassin) {
				thisFeature.add(tagValue);
			} else {
				thisFeature.add(0);
			}
			if (fighter) {
				thisFeature.add(tagValue);
			} else {
				thisFeature.add(0);
			}
			if (mage) {
				thisFeature.add(tagValue);
			} else {
				thisFeature.add(0);
			}
			if (tank) {
				thisFeature.add(tagValue);
			} else {
				thisFeature.add(0);
			}
			if (support) {
				thisFeature.add(tagValue);
			} else {
				thisFeature.add(0);
			}
			if (marksman) {
				thisFeature.add(tagValue);
			} else {
				thisFeature.add(0);
			}
			nameToFeature.put(champName, thisFeature);
		}

		champIDMap = idToName;
		champFeatureMap = nameToFeature;
	}

	public static Map<String, Double> getChampKDA() throws ForbiddenException, ServiceUnavailableException,
			RateLimitException, InternalServerException, JsonIOException, JsonSyntaxException, IOException {

		String sURL = "https://na1.api.riotgames.com/lol/match/v4/matchlists/by-account/" + accountID + "?api_key="
				+ api_key; // just a string
		// Connect to the URL using java's native library
		URL url = null;
		HttpURLConnection request = null;
		url = new URL(sURL);
		request = (HttpURLConnection) url.openConnection();
		request.connect();

		int response = request.getResponseCode();
		if (response == 429) {
			throw new RateLimitException("429");
		} else if (response == 500) {
			throw new InternalServerException("500");
		} else if (response == 503) {
			throw new ServiceUnavailableException("503");
		} else if (response == 404) {
			throw new ForbiddenException("404");
		}

		// Convert to a JSON object to print data
		JsonParser jp = new JsonParser(); // from gson
		JsonElement root = null;

		root = jp.parse(new InputStreamReader((InputStream) request.getContent()));

		// Convert the input stream to a json element
		JsonObject rootobj = root.getAsJsonObject(); // May be an array, may be an object.
		List<String> list = new ArrayList<String>();
		JsonArray array = rootobj.getAsJsonArray("matches");

		for (int i = 0; i < array.size() - 2; i++) { // for each match
			int championID = ((JsonObject) array.get(i)).get("champion").getAsInt();
			// System.out.print(" championID: "+championID);
			String championName = champIDMap.get(championID);
			// System.out.println(" championName: "+championName);
			if (championName == null) {
				continue;
			}
			String matchID = ((JsonObject) array.get(i)).get("gameId").getAsString();
			Match m = new Match(matchID, accountID, api_key);
			kdaWinLoseTime wlt = m.getGameStats();
			double winBonus = 0;
			if (wlt.win) {
				winBonus = 1.5;
				winLose.put(championName, 0);
			} else {
				winLose.put(championName, 1);
			}
			kdaMap.put(championName, kdaMap.getOrDefault(championName, 0.0) + wlt.kda + winBonus);
			freqMap.put(championName, freqMap.getOrDefault(championName, 0) + 1);

			if (!lastPlay.containsKey(championName)) {
				lastPlay.put(championName, wlt.time);
			} else {
				if (lastPlay.get(championName) < wlt.time) {
					lastPlay.put(championName, wlt.time);
				}
			}

			// update playerFeature
			ArrayList<Integer> championFeature = champFeatureMap.get(championName);
			for (int j = 0; j < 10; j++) {
				playerFeature.set(j, playerFeature.get(j) + championFeature.get(j));
			}

			// update playedChampion
			playedChampion.add(championName);

			// update tagFreq
			if (championFeature.get(4) != 0) {
				tagFreq.put("Assassin", tagFreq.getOrDefault("Assassin", 0) + 1);
			}
			if (championFeature.get(5) != 0) {
				tagFreq.put("Fighter", tagFreq.getOrDefault("Fighter", 0) + 1);
			}
			if (championFeature.get(6) != 0) {
				tagFreq.put("Mage", tagFreq.getOrDefault("Mage", 0) + 1);
			}
			if (championFeature.get(7) != 0) {
				tagFreq.put("Tank", tagFreq.getOrDefault("Tank", 0) + 1);
			}
			if (championFeature.get(8) != 0) {
				tagFreq.put("Support", tagFreq.getOrDefault("Support", 0) + 1);
			}
			if (championFeature.get(9) != 0) {
				tagFreq.put("Marksman", tagFreq.getOrDefault("Marksman", 0) + 1);
			}
		}
		for (int j = 0; j < 10; j++) {
			playerFeature.set(j, playerFeature.get(j) / (array.size() - 2));
		}
		Map<String, Double> res = new HashMap<String, Double>();
		for (String champName : kdaMap.keySet()) {
			res.put(champName, kdaMap.get(champName) / freqMap.get(champName));
		}

		// sort freqMap
		freqMap = freqMap.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		// sort tagFreq
		tagFreq = tagFreq.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		return res;
	}

	//Returns the oldRankMap in decendent order.
	public static Map<String, Double> usedChampFinalRank() throws Exception {
		Map<String, Double> rank = getChampKDA();
		// Numerize kda as part of the final value
		rank.forEach((champ, kda) -> {

			double timeDifference = (System.currentTimeMillis() - lastPlay.get(champ)) / (1000 * 60 * 60 * 24) == 0 ? 1
					: (System.currentTimeMillis() - lastPlay.get(champ)) / (1000 * 60 * 60 * 24);

			rank.put(champ, (double)
			// KDA of the champ, Having good gaming experience? WinLose incorporated
			(kda * 1.2 +
			// Level of proficiency, Likeness of the champ
			freqMap.get(champ) * 0.5 -
			// Doesn't prefer using champs have used recently.
			5 / timeDifference +
			// If one hasn't use for a while, gradully grow the value of it.
			timeDifference > 8 ? timeDifference * 0.2 : 0));
		});

		// Sort Map for recommending order
		Map<String, Double> finalSortedRank = rank.entrySet().stream()
				.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		return finalSortedRank;
	}

	public static ArrayList<String> recommendNew() {
		ArrayList<String> res = new ArrayList<String>();
		Map<String, Double> differenceMap = new HashMap<>();
		for (Map.Entry<String, ArrayList<Integer>> entry : champFeatureMap.entrySet()) { // for every champion
			String championName = entry.getKey();

			// if the champion has been played, skip it
			if (playedChampion.contains(championName)) {
				continue;
			}
			ArrayList<Integer> championFeature = entry.getValue();
			double difference = 0;
			for (int i = 0; i < 10; i++) { // for each feature
				if (i == 3) { // if it is the difficulty feature
					// if the player used to play hard champions, he can easily handle easier ones
					difference = difference + 2 * (championFeature.get(i) - playerFeature.get(i));
				} else { // for other features
					difference = difference + Math.pow((championFeature.get(i) - playerFeature.get(i)), 2);
				}
			}
			differenceMap.put(championName, difference);
		}

		List<Entry<String, Double>> list = new ArrayList<>(differenceMap.entrySet());
		list.sort(Entry.comparingByValue());
		for (int i = 0; i < Math.min(5, list.size()); i++) {
			res.add(list.get(i).getKey());
		}
		return res;
	}

	public static boolean checkName(String name) {
		String testString = "^[0-9\\p{L} _\\.]+$";
		for (int i = 0; i < name.length(); i++) {
			char nameChar = name.charAt(i);
			if (testString.indexOf(nameChar) == -1) {
				if (nameChar < 'a' || nameChar > 'z') {
					if (nameChar < 'A' || nameChar > 'Z') {
						if (nameChar < '0' || nameChar > '9') {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

}