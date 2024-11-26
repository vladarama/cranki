package ca.mcgill.cranki.dto;

public class TodoDescriptionDto {
    private String description;

    public TodoDescriptionDto() {
    }

    public TodoDescriptionDto(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
