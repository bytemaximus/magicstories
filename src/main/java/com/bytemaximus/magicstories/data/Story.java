package com.bytemaximus.magicstories.data;

import jakarta.persistence.*;

@Entity
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    String gptCommand;
    @Column
    String storyTitle;
    @Column
    String story;

    public Story() {

    }

    public Long getId() {
        return id;
    }

    public String getGptCommand() {
        return gptCommand;
    }

    public void setGptCommand(String gptCommand) {
        this.gptCommand = gptCommand;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
}
