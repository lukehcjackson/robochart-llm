package circus.robocalc.robochart.generator.llm;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import java.io.File;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;


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
        /*
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
        */

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
    
    public static String getResourcePath(String pluginId, String relativePath) throws Exception {
        Bundle bundle = Platform.getBundle(pluginId);
        URL fileURL = bundle.getEntry(relativePath); // relative to plugin root
        URL resolvedURL = FileLocator.toFileURL(fileURL);
        return resolvedURL.getPath(); // this is your String path
    }

    public static String LLMgenMain(String[] args) throws Exception {
        //Should have these as an option to be set in the eclipse preferences menu



        OpenAIClient client = new OpenAIClientBuilder()
            .credential(new AzureKeyCredential(key))
            .endpoint(endpoint)
            .buildClient();

        //get the prompts directory
        String wd = getResourcePath("circus.robocalc.robochart.generator.llm", "src/circus/robocalc/robochart/generator/llm/prompts/");

        //load the prompts from files
        String systemMessage       = asString(wd + "systemPrompt");
        String SRangerPrompt       = asString(wd + "srangerPrompt");
        String PSRangerPrompt      = asString(wd + "psrangerPrompt");
        String AutonomousGasPrompt = asString(wd + "autoGasPrompt");       
        String AlphaPrompt         = asString(wd + "alphaPrompt");

        String userPromptSuffix = "\nAnswer only with your DSL code in plain text. Do not include anything that could not be directly loaded into a RoboChart file and run. Consider your answer carefully.";
        
        //construct the prompt based on whether we are in edit mode or generate mode
        //this is determined by whether we pass a file in args[] so if args.length > 1 we are in edit mode and args[1] is the file to edit
        //args[0] is the user's input
        String userPrompt = "";
        if (args.length > 1) {
        	String editPromptPrefix = "Using the examples of the DSL I have provided you, please edit the following DSL code corresponding with these requirements: \n";
        	String editPromptContents = "\nDSL code: \n" + asString(args[1]);
            userPrompt = editPromptPrefix + args[0] + editPromptContents + userPromptSuffix;
        } else {
        	String userPromptPrefix = "Using the examples of the DSL I have provided you, please generate DSL code that corresponds to the requirements for this new robotic system: \n";
            userPrompt = userPromptPrefix + args[0] + userPromptSuffix;
        }
        System.out.println(userPrompt);
        
        //construct an object which holds all of our prompts to be passed to the LLM
        List<String> allPrompts = Arrays.asList(
            SRangerPrompt,
            //PSRangerPrompt,
            //AutonomousGasPrompt,
            //AlphaPrompt,
            userPrompt
        );
        List<String> prompts = new ArrayList<String>();
        List<String> responses = new ArrayList<String>();

        //pass the prompts to the LLM and record the responses
        for (String prompt : allPrompts) {
            prompts.add(prompt);
            responses.add(getResponse(deploymentOrModelId, client, systemMessage, prompts, responses));
        }

        System.out.println("\n---------------------------ALL PROMPTS AND RESPONSES:---------------------------------\n");

        for (int i = 0; i < prompts.size(); i++) {
            System.out.println(prompts.get(i) + "\n");
            System.out.println(responses.get(i) + "\n");
        }
        
        return responses.get(responses.size() - 1);
    }
}



