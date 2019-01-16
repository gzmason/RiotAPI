
package application;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import javafx.scene.image.Image;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class LoginController implements Initializable {

	@FXML
	private Button loginButton;

	@FXML
	private Button anotherButton;
	
	@FXML
	private Button newChampBtn;

	@FXML
	private TextField nameTextField;
	
	@FXML
	private TextField newChamp;

	@FXML
	private TextField recomTextField;

	@FXML
	private ImageView oldchampIcon;
	
	@FXML
	private ImageView newchampIcon;
	
	@FXML
	private Label oldLable;
	
	@FXML
	private Label newLable;

	private Service<Void> backgroundThread;

	private Alert alert = new Alert(AlertType.INFORMATION);
	
	private Map<String,Double> finalResult;
	
	private List<String> oldRecommendList = new ArrayList<>();
	
	private List<String> newRecommendList = new ArrayList<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		recomTextField.setDisable(true);
		newChamp.setDisable(true);
	}

	@FXML
	public void login(ActionEvent event) {
		
		try {
			if (nameTextField.getLength() != 0) {
				loginButton.setDisable(true);
				
				backgroundThread = new Service<Void>() {

					@Override
					protected Task<Void> createTask() {
						// TODO Auto-generated method stub
						return new Task<Void>() {

							@Override
							protected Void call() throws Exception {
								updateMessage("Logging in...");
								ChampionFrequency.getChampName();
								ChampionFrequency.SummonerIDbyName(nameTextField.getText());
								System.out.println("test1");
								finalResult = ChampionFrequency.usedChampFinalRank();
								System.out.println("test2");
								newRecommendList = ChampionFrequency.recommendNew();
								System.out.println("test");
								for (Map.Entry<String, Double> entry : finalResult.entrySet()) {
									String championName = entry.getKey();
									oldRecommendList.add(championName);
									System.out.println("Champion: " + championName + " RankValue: " 
									+ finalResult.get(championName));
								}
								
								return null;
							}
						};
					}

				};

				backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

					@Override
					public void handle(WorkerStateEvent arg0) {
						// TODO Auto-generated method stub

						alert.setHeaderText(null);
						alert.setContentText("Login Success!");
						alert.showAndWait();
						anotherButton.setDisable(false);
//						
//						
						loginButton.textProperty().unbind();
						anotherButton.textProperty().unbind();
						newChampBtn.setDisable(false);
						loginButton.setText("Logged in");
						loginButton.setDisable(true);
						nameTextField.setDisable(true);
					}

				});
				
				loginButton.textProperty().bind(backgroundThread.messageProperty());
				//anotherButton.textProperty().bind(backgroundThread.messageProperty());
			}
			
			backgroundThread.restart();
		}
		catch(Exception e) {
			alert.setHeaderText(null);
			alert.setContentText("APIException, Pleas try again!");
			alert.showAndWait();
			nameTextField.setText("");
			loginButton.setText("Login");
			loginButton.setDisable(false);
		}
		
		
		

	}

	public void anotherChamp(ActionEvent event) {
		anotherButton.setText("Another Pick");
		
		if(oldRecommendList.size() != 0) {
			String champ = oldRecommendList.remove(0);
			int kda = (int) Math.round(ChampionFrequency.kdaMap.get(champ));
			System.out.println(ChampionFrequency.lastPlay.get(champ));
			Date lastPlay = new java.util.Date((ChampionFrequency.lastPlay.get(champ)));
			System.out.println(lastPlay);
			SimpleDateFormat dt = new SimpleDateFormat("MM/dd"); 
			String date = dt.format(lastPlay);
			String winLose = ChampionFrequency.winLose.get(champ) == 0 ? "Win" : "Lose";
			int recomVal = (int) Math.round(finalResult.get(champ));
			
			oldLable.setText("KDA: " + kda + "\n" +
							 "Last Play: " + date + "\n" +
							 "Win/Lose: " + winLose + "\n" +
							 "RecmdVal: " + recomVal);
			
			recomTextField.setText(champ);
			recomTextField.setDisable(true);
			String url = "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/" + champ + ".png";
			Image icon = new Image(url);
			oldchampIcon.setImage(icon);
			oldchampIcon.setSmooth(true);
			oldchampIcon.setCache(true);
		}	
	}
	
	public void anotherNew(ActionEvent event) {
		newChampBtn.setText("Another Pick");
		
		if(newRecommendList.size() != 0) {
			String champ = newRecommendList.remove(0);
			int count = 0;
			String[] mostPlayed = new String[2];
			for(Entry<String,Integer> entry:ChampionFrequency.tagFreq.entrySet()) {		
				mostPlayed[count++] = entry.getKey();
				if(count == 2) break;
			}
			
			newLable.setText("Most play tag: \n"
					+ mostPlayed[0] + "\n" + mostPlayed[1]);
			newChamp.setText(champ);
			newChamp.setDisable(true);
			String url = "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/" + champ + ".png";
			Image icon = new Image(url);
			newchampIcon.setImage(icon);
			newchampIcon.setSmooth(true);
			newchampIcon.setCache(true);
		}	
	}


}