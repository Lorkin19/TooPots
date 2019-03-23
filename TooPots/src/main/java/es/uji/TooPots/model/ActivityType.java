package es.uji.TooPots.model;

public class ActivityType {
	
	private TypeActivityName name;
	private String description;
	
	public ActivityType() {}

	public TypeActivityName getName() {
		return name;
	}

	public void setName(TypeActivityName name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
