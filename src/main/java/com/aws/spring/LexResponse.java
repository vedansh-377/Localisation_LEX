package com.aws.spring;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.lexruntimev2.LexRuntimeV2Client;
import software.amazon.awssdk.services.lexruntimev2.model.*;

import java.util.List;
import org.json.JSONArray;
@Component
public class LexResponse {
    public String lexResponseMethod(String translatedInput) {
        String region = "us-east-1"; // Replace with your desired AWS region
        String botId = "V2V5BO42CC";
        String botAliasId = "TSTALIASID";
        String sessionId = "003261238302935";


        // Create LexRuntimeV2Client
        RecognizeTextResponse response;
        try (LexRuntimeV2Client lexClient = LexRuntimeV2Client.builder()
                .region(Region.of(region))
                .build()) {

            // Make Lex v2 API call to recognize text
            RecognizeTextRequest recognizeTextRequest = RecognizeTextRequest.builder()
                    .botId(botId)
                    .botAliasId(botAliasId)
                    .localeId("en_US")
                    .sessionId(sessionId)
                    .text(translatedInput)
                    .build();

            response = lexClient.recognizeText(recognizeTextRequest);
        }

        // Process the Lex response as needed
//        StringBuilder lexResponseStringBuilder = new StringBuilder();
        JSONObject lexResponseJson = new JSONObject();


        // Extract and append Messages
        if (response.messages() != null) {
            JSONArray messagesArray = new JSONArray();

            List<Message> messages = response.messages();
            for (Message message : messages) {
//                lexResponseStringBuilder.append("Message ContentType: ").append(message.contentType()).append("\n");
                JSONObject messageJson = new JSONObject();

                // Handle different content types
                switch (message.contentType()) {
                    case PLAIN_TEXT:
//                        messageJson.put("contentType", "PLAIN_TEXT");
                        messageJson.put("content", message.content());
//                        lexResponseStringBuilder.append(" ").append(message.content()).append("\n");
                        break;



                    case IMAGE_RESPONSE_CARD:
                        // Assuming ImageResponseCard has buttons
                        ImageResponseCard imageResponseCard = message.imageResponseCard();
//                        lexResponseStringBuilder.append("ImageResponseCard Title: ").append(imageResponseCard.title()).append("\n");

                        // Append buttons
                        JSONArray buttonsArray = new JSONArray();
                        for (Button button : imageResponseCard.buttons()) {
                            JSONObject buttonJson = new JSONObject();
                            buttonJson.put("text", button.text());
                            buttonsArray.put(buttonJson);

//                            lexResponseStringBuilder.append("Button Text: ").append(button.text()).append("\n");
//                            lexResponseStringBuilder.append(" ").append(button.value()).append("\n");
                        }
                        messageJson.put("buttons", buttonsArray);

                        break;

                    // Add more cases for other content types if needed

                    default:
                        messageJson.put("contentType", message.contentType());

//                        lexResponseStringBuilder.append(" ").append(message.contentType()).append("\n");
                }
                messagesArray.put(messageJson);
            }
            lexResponseJson.put("messages", messagesArray);

        } else {
            lexResponseJson.put("messages", new JSONArray());

//            lexResponseStringBuilder.append("No messages in the response.").append("\n");
        }

        // Return the Lex response as a String, including message content
        return lexResponseJson.toString();
    }

//    public static void main(String[] args) {
//        LexResponse lexResponseInstance = new LexResponse();
//        String translatedInput = "2";
//////      Calling Translation.........................................................
//////        String translatedText = TranslationService.translate(translatedInput, "en", "it");
//////        System.out.println(translatedText);
//////      Calling Lex.................................................................
//        String text = lexResponseInstance.lexResponseMethod(translatedInput);
//        System.out.println(text);
//    }
}
