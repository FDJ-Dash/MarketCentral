package com.freetime_software.marketcentral.model;

/**
 * Created by Fernando Daniel Jaime on 20/11/18.
 */
class Taller {
    private String id;
    private String name;
    private String description;
    private String service;
    private String image;
    private String comments;

    // Constructors
    public Taller() {
    }

    public Taller(String id, String name, String description, String service, String image, String comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.service = service;
        this.image = image;
        this.comments = comments;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
