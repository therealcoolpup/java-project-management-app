package application.domains;

import java.sql.Date;
import java.util.ArrayList;

public class Column {
	int columnID;
	int projectID;
	String column_name;
	Date dueDate;
	String description;
	
	public Column() {
		
	}

	public int getColumnID() {
		return columnID;
	}

	public void setColumnID(int columnID) {
		this.columnID = columnID;
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}

	public Date getDue_date() {
		return dueDate;
	}

	public void setDue_date(Date due_date) {
		this.dueDate = due_date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	


}