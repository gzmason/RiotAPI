package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class LoginController implements Initializable {

	@FXML
	private Button loginButton;

	@FXML
	private Button anotherButton;

	@FXML
	private TextField nameTextField;

	@FXML
	private TextField recomTextField;

	@FXML
	private ImageView champIcon;

	private Service<Void> backgroundThread;

	private Alert alert = new Alert(AlertType.INFORMATION);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		recomTextField.setDisable(true);
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
								Map<String, Double> kdaResult = ChampionFrequency.getChampKDA();
								long currentTime=System.currentTimeMillis();
								for (Map.Entry<String, Double> entry : kdaResult.entrySet()) {
									String championName = entry.getKey();
									System.out.println("Champion: " + championName + "   Times: "
											+ ChampionFrequency.freqMap.get(championName) + "   Average KDA: "
											+ entry.getValue() + "   Last Played: "
											+ (currentTime-ChampionFrequency.lastPlay.get(championName)));
								}
								ChampionFrequency.recommendNew();
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
		recomTextField.setText("Aatrox");
		recomTextField.setDisable(true);
		Image icon = new Image("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Aatrox.png");
		champIcon.setImage(icon);
		champIcon.setSmooth(true);
		champIcon.setCache(true);

	}

}