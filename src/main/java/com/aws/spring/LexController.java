// LexController.java
package com.aws.spring;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LexController {

    @Autowired
    LexResponse lexResponse;
    @Autowired
    TranslationService translationService;

    @GetMapping("/chat")
    public String showChatForm() {
        return "chatbot"; // Assuming chatbot.html is in src/main/resources/templates
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String userInput, String selectedLanguage, Model model) {
        // Call LexResponse to get bot response
//        long translationStartTime = System.currentTimeMillis();

        TranslationService.TranslatedContent userTranslation = translationService.translate(userInput, selectedLanguage, "en");
        String userContent = userTranslation.getMessages().getJSONObject(0).getString("content");
        System.out.println(userContent);
//        long translationEndTime = System.currentTimeMillis();

        // Calculate the translation time
//        long translationTime = translationEndTime - translationStartTime;

        // Log or store the translation time
//        System.out.println("Translation time: " + translationTime + " milliseconds");
        long translationStartTime = System.currentTimeMillis();

        String botResponse = lexResponse.lexResponseMethod(userContent);
        System.out.println(botResponse);
//        long lexResponseStartTime = System.currentTimeMillis();
        long lexResponseEndTime = System.currentTimeMillis();

        TranslationService.TranslatedContent botTranslation = translationService.translate(botResponse, "en", selectedLanguage);
        JSONArray botMessages = botTranslation.getMessages();

//        long lexResponseEndTime = System.currentTimeMillis();

        // Calculate the bot response time
        double lexResponseTime = (lexResponseEndTime - translationStartTime)/1000.0;

        // Log or store the bot response time
        System.out.println("LexResponse time: " + lexResponseTime + "seconds");

        // Check if buttons are present in the first message
        if (botMessages.length() > 0) {
            JSONObject firstMessage = botMessages.getJSONObject(0);

            System.out.println(firstMessage);

            String botContent = firstMessage.getString("content");
            System.out.println(botContent);

            // Add user input, bot response, and messages array to the model
            model.addAttribute("userInput", userContent);
            model.addAttribute("botResponse", botContent);

            // Check if buttons are present in the first message
            if (botMessages.length() > 1) {
                JSONObject second = botMessages.getJSONObject(1);
                System.out.println(second);
                JSONArray buttonsArray = second.getJSONArray("buttons");
                List<String> buttonTexts = new ArrayList<>();

                // Extract "text" values and store them in the list
                for (int i = 0; i < buttonsArray.length(); i++) {
                    JSONObject buttonObject = buttonsArray.getJSONObject(i);
                    buttonTexts.add(buttonObject.getString("text"));
                }
                System.out.println(buttonTexts);
                model.addAttribute("buttons", buttonTexts);
            }
        }

        return "chat-result.html";
    }
}
