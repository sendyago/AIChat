import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GoogleSearch;
import com.google.genai.types.Tool;

/**
 * Gemini chat 调用测试 IDEA版
 * @author wecode
 */
void main() {

    Tool searchTool = Tool.builder().googleSearch(GoogleSearch.builder()).build();
    GenerateContentConfig config = GenerateContentConfig.builder()
            .tools(searchTool)
            .build();
    Chat chat = Client
            .builder()
            .apiKey("")  // 必须填写
            .build()
            .chats
            .create("gemini-2.5-flash");
    Scanner scanner = new Scanner(System.in);

    do {
        IO.print("> ");
        var text = scanner.nextLine();
        if ("exit".equals(text)) {
            break;
        }
        GenerateContentResponse response = chat.sendMessage(text, config);
        IO.println("AI: " + response.text());
    }
    while (true) ;


}