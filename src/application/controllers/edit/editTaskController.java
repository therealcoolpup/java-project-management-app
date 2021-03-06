package application.controllers.edit;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import application.Model;
import application.controllers.dashboardController;
import application.domains.Column;
import application.domains.Project;
import application.domains.Task;
import application.domains.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssParser.ParseError.StringParsingError;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class editTaskController {
	
	public void editTaskController() {
		
	}
	
	private Stage stage;
	
	private Task task;
	
	private Project project;
	
	private Model model = new Model();

    @FXML
    private Label lbl_heading;

    @FXML
    private TextField txtFieldTaskName;

    @FXML
    private Button btn_saveChanges;

    @FXML
    private Label lbl_notification;

    @FXML
    private TextArea txtAreaDescription;

    @FXML
    private DatePicker datePicker;

    @FXML
    private CheckBox checkBox_completed;
    
    @FXML
    private ComboBox<String> comboBoxChooseColumn;
    
    boolean changeColumn = false;
    
    ArrayList<Column> columns = new ArrayList<Column>();
    
    ArrayList<String> columnNames = new ArrayList<String>();
    
    //Load task details for this page.
	public void loadEditTask(Task task, Project project) throws SQLException {
		this.project = project;
		this.task = task;
		
		//Fill in input fields with task details.
		txtFieldTaskName.setText(task.getTaskName());
		txtAreaDescription.setText(task.getDescription());
		datePicker.setValue(task.getDueDate().toLocalDate());
		if (task.isCompleted()) {
			checkBox_completed.setSelected(true);
		}
		else {
			checkBox_completed.setSelected(false);
		}
		
		
		
		//Show available columns in the column selecter.
		columns = model.getProjectDAO().loadColumns(project.getProjectID());				
		for (Column column : columns) {			
			columnNames.add(column.getColumn_name());
		}
		
		ObservableList<String> columnNameOptions = FXCollections.observableArrayList(columnNames);
		
		Column defaultColumn = model.getProjectDAO().searchColumn(task.getColumnID());
		
		comboBoxChooseColumn.setPromptText(defaultColumn.getColumn_name());
		comboBoxChooseColumn.setItems(columnNameOptions);
		
	}
	
	//Switch task column.
	@FXML
	void choseOption(ActionEvent event) {
		
		if (comboBoxChooseColumn.getSelectionModel().getSelectedItem() != null) {
			System.out.println("Chaning column to " + comboBoxChooseColumn.getSelectionModel().getSelectedItem().toString());
			changeColumn = true;
		}
		
	}

	//Save changes to task.
    @FXML
    void saveTaskChanges(ActionEvent event) throws SQLException {
    	//If all fields are filled in
    	if (txtFieldTaskName.getText() != "" && txtAreaDescription.getText() != "" && datePicker.getValue() != null) {
			//Convert the date selected to a value that can be sent to the database.
    		LocalDate tmpLocalDate = datePicker.getValue();
			Date tmpDate = Date.valueOf(tmpLocalDate);

			//Save task changes and notify the user that it was successful.
			model.getProjectDAO().saveTaskChanges(task.getTaskID(), txtFieldTaskName.getText(), txtAreaDescription.getText(), tmpDate, checkBox_completed.isSelected());
			lbl_notification.setTextFill(Color.GREEN);
			lbl_notification.setText(txtFieldTaskName.getText() + " edited successfully");

			//If the user decides to change the task column
			if (changeColumn) {			
				int columnID = 0;
				//Move task to the new column.
				for (Column column : columns) {
					if (column.getColumn_name() == comboBoxChooseColumn.getSelectionModel().getSelectedItem().toString()) {
						columnID = column.getColumnID();
					}
				}				
				
				model.getProjectDAO().changeTaskColumn(task.getTaskID(), columnID);
			}
		}
    	else {
    		//If not all neccessary fields weere filled prompt the user to fill them in.
    		lbl_notification.setTextFill(Color.ORANGE);
    		lbl_notification.setText("You didn't fill in all neccessary fields.");
    	}
    }
    
	//Go back to dashboard.
    @FXML
	public void back(ActionEvent event) throws Exception {
		System.out.println("Back to dashboard");
		
		//Load the dashboard and set the neccessary parameters.
		FXMLLoader dashboardScene = new FXMLLoader(getClass().getResource("/application/views/Dashboard.fxml"));
		Parent root = dashboardScene.load();
		dashboardController dashboardController = dashboardScene.getController();
		dashboardController.setQuote();
		dashboardController.setUserID(project.getUserID());
		User user = model.getUserDAO().getUser(project.getUserID());
		dashboardController.setWelcomeMessage(user.getFirstName());
		dashboardController.showProjects(project.getUserID());
		dashboardController.tabpane_mainTab.getSelectionModel().select(1);
		dashboardController.loadUser(user);
		
		//Go to dashboard.
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

}
