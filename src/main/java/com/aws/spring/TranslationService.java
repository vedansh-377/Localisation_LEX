// TranslationService.java
package com.aws.spring;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class TranslationService {

    public static TranslatedContent translate(String text, String sourceLanguageCode, String targetLanguageCode) {
        AmazonTranslate translate = AmazonTranslateClient.builder()
                .withRegion("us-west-2")
                .build();

        try {
            // Attempt to parse the input as JSON
            JSONObject inputJson = new JSONObject(text);

            // Check if the JSON contains the expected "messages" array
            if (inputJson.has("messages")) {
                JSONArray messagesArray = inputJson.getJSONArray("messages");

                // Iterate over messages and replace the "content" field with translated text
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject message = messagesArray.getJSONObject(i);
                    if (message.has("content")) {
                        String originalContent = message.getString("content");
                        String translatedContent = translateText(originalContent, sourceLanguageCode, targetLanguageCode);
                        message.put("content", translatedContent);
                    }
                    if (message.has("buttons")) {
                        JSONArray originalButtons = message.getJSONArray("buttons");
                        JSONArray translatedButtons = new JSONArray();

                        // Iterate over buttons and translate each text
                        for (int j = 0; j < originalButtons.length(); j++) {
                            JSONObject originalButton = originalButtons.getJSONObject(j);
                            String originalText = originalButton.getString("text");
                            String translatedText = translateText(originalText, sourceLanguageCode, targetLanguageCode);

                            // Create a new button with translated text
                            JSONObject translatedButton = new JSONObject().put("text", translatedText);
                            translatedButtons.put(translatedButton);
                        }

                        // Replace the original "buttons" with translated buttons
                        message.put("buttons", translatedButtons);
                    }
                }

                return new TranslatedContent(messagesArray);
            } else {
                // If "messages" array is not present, construct the expected JSON structure
                JSONArray messagesArray = new JSONArray();
                String translatedContent = translateText(text, sourceLanguageCode, targetLanguageCode);
                JSONObject contentJson = new JSONObject().put("content", translatedContent);
                messagesArray.put(contentJson);
                return new TranslatedContent(messagesArray);
            }
        } catch (Exception e) {
            // If parsing as JSON fails, treat it as a simple string
            JSONArray messagesArray = new JSONArray();
            String translatedContent = translateText(text, sourceLanguageCode, targetLanguageCode);
            JSONObject contentJson = new JSONObject().put("content", translatedContent);
            messagesArray.put(contentJson);
            return new TranslatedContent(messagesArray);
        }
    }

    private static String translateText(String text, String sourceLanguageCode, String targetLanguageCode) {
        AmazonTranslate translate = AmazonTranslateClient.builder()
                .withRegion("us-west-2")
                .build();

        TranslateTextRequest request = new TranslateTextRequest()
                .withText(text)
                .withSourceLanguageCode(sourceLanguageCode)
                .withTargetLanguageCode(targetLanguageCode);

        TranslateTextResult result = translate.translateText(request);
        return result.getTranslatedText();
    }

    // Class representing the translated content
    public static class TranslatedContent {
        private final JSONArray messages;

        public TranslatedContent(JSONArray messages) {
            this.messages = messages;
        }

        public JSONArray getMessages() {
            return messages;
        }
    }
}
