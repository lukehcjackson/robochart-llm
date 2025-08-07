package circus.robocalc.robochart.generator.llm;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.ChatResponseMessage;
import com.azure.ai.openai.models.CompletionsUsage;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;

public final class LLMgen {

    public static String getResponse(String deploymentOrModelId, OpenAIClient client, String systemPrompt, List<String> prompts, List<String> responses) {
        /*
         * Construct the chatMessages object we will pass to the API
         */
        List<ChatRequestMessage> chatMessages = new ArrayList<ChatRequestMessage>();
        chatMessages.add(new ChatRequestSystemMessage(systemPrompt));
        for (int i = 0; i < prompts.size(); i++) {
            String prompt = prompts.get(i);
            if (prompt != null && !prompt.isEmpty()) {
                chatMessages.add(new ChatRequestUserMessage(prompt));
            }
            if (i < responses.size()) {
                chatMessages.add(new ChatRequestAssistantMessage(responses.get(i)));
            }
        }

        /*
         * Testing: Has that object been constructed correctly?
         */
        System.out.println("--------- NEW CHAT -----------");
        for (ChatRequestMessage message : chatMessages) {
            String role = message.getRole().toString(); // Convert ChatRole enum to String
            String content = "";

            if (message instanceof ChatRequestSystemMessage) {
                BinaryData data = ((ChatRequestSystemMessage) message).getContent();
                content = data.toString();
            } else if (message instanceof ChatRequestUserMessage) {
                BinaryData data = ((ChatRequestUserMessage) message).getContent();
                content = data.toString();
            } else if (message instanceof ChatRequestAssistantMessage) {
                BinaryData data = ((ChatRequestAssistantMessage) message).getContent();
                content = data.toString();
            }

            System.out.println("[" + role + "]: " + content + "\n");
        }

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        String finalResponse = chatCompletions.getChoices().get(0).getMessage().getContent();
        return finalResponse;

    }

    // https://www.geeksforgeeks.org/java/java-program-to-read-a-file-to-string/
    private static String asString(String filePath)
    {

        StringBuilder builder = new StringBuilder();

        try (BufferedReader buffer = new BufferedReader(
                 new FileReader(filePath))) {

            String str;

            while ((str = buffer.readLine()) != null) {
                builder.append(str).append("\n");
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString().trim();
    }

    public static void LLMgenMain(String[] args) {
        //Should have these as an option to be set in the eclipse preferences menu
        String key = ";
        String endpoint = "";
        String deploymentOrModelId = "";


        OpenAIClient client = new OpenAIClientBuilder()
            .credential(new AzureKeyCredential(key))
            .endpoint(endpoint)
            .buildClient();

        //replace with a line that gets working directory
        //String wd = "/home/luke/Documents/demo";

        //String systemMessage       = asString(wd + "/systemPrompt");
        String systemMessage = "You are a helpful assistant. Reply to each message with exactly the contents of the message, followed by a random word of your choice.";
        //String SRangerPrompt       = asString(wd + "/srangerPrompt");
        String SRangerPrompt = "yadada fadfa ";
        //String PSRangerPrompt      = asString(wd + "/psrangerPrompt");
        //String AutonomousGasPrompt = asString(wd + "/autoGasPrompt");       
        //String AlphaPrompt         = asString(wd + "/alphaPrompt");
        String AlphaPrompt = "hehehehe hoohhohoho";

        String tempUserPrompt = "Using the examples of the DSL I have provided you, please generate DSL code that corresponds to the requirements for this new robotic system: 'The robot performs a random walk. While doing so, it detects the light level of the environment. If the brightness of the light is greater than some threshold, a flag event is raised and execution stops.'";

        List<String> allPrompts = Arrays.asList(
            SRangerPrompt,
 //           PSRangerPrompt,
//            AutonomousGasPrompt,
            AlphaPrompt
        );
        List<String> prompts = new ArrayList<String>();
        List<String> responses = new ArrayList<String>();

        
        for (String prompt : allPrompts) {
            prompts.add(prompt);
            responses.add(getResponse(deploymentOrModelId, client, systemMessage, prompts, responses));
        }

        System.out.println("\n---------------------------ALL PROMPTS AND RESPONSES:---------------------------------\n");

        for (int i = 0; i < prompts.size(); i++) {
            System.out.println(prompts.get(i) + "\n");
            System.out.println(responses.get(i) + "\n");
        }
    }
}



