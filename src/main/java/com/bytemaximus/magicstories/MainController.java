package com.bytemaximus.magicstories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bytemaximus.magicstories.data.Story;
import com.bytemaximus.magicstories.data.UserRepository;
import com.bytemaximus.magicstories.data.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class MainController {

    private UserService userService;
    private UserRepository userRepository;
    @Value("${chatgpt.key}")
    private String chatGPTKey;
    @Value("${chatgpt.organizationid}")
    private String chatGPTOrganizationId;
    @Value("${chatgpt.url}")
    private String chatGPTUrl;

    public MainController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // Login form
    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    // Login form with error
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login.html";
    }

    // Welcome page
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("storyForm", new Story());
        return "index.html";
    }

    @PostMapping("/story")
    public String submitForm(@ModelAttribute("inputForm") Story story, Model model) {
        try {
            // Send API request here to ChatGPT
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("Authorization", "Bearer " + chatGPTKey);
            httpHeaders.add("OpenAI-Organization", chatGPTOrganizationId);

            JSONObject storyCommand = getStoryCommandJson(story);

            HttpEntity<String> request = new HttpEntity<String>(storyCommand.toString(), httpHeaders);

            ResponseEntity<String> response = restTemplate.postForEntity(chatGPTUrl, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choicesArray = root.get("choices");
            JsonNode choiceObject = choicesArray.get(0);
            JsonNode messageObject = choiceObject.get("message");
            JsonNode storyObject = objectMapper.readTree(messageObject.get("content").asText());
            story.setStoryTitle(storyObject.get("title").asText());
            story.setStory(storyObject.get("story").asText());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("storyTitle", story.getStoryTitle());
        model.addAttribute("storyText", story.getStory());
        return "story.html";
    }

    // Welcome page
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("userlist", userRepository.findAll());
        return "users.html";
    }


    private static JSONObject getStoryCommandJson(Story story) {
        // Set the story command
        JSONObject storyCommand = new JSONObject();
        storyCommand.put("model", "gpt-3.5-turbo");

        JSONObject type = new JSONObject();
        type.put("type", "json_object");
        storyCommand.put("response_format", type);

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant designed to output stories in JSON format with nodes: title and story.");
        messages.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Write a child friendly story under 300 words based on the following criteria: " + story.getGptCommand());
        messages.put(userMessage);

        storyCommand.put("messages", messages);

        return storyCommand;
    }
}
