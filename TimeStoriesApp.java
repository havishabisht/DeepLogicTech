import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import TimeStoriesApp.RootHandler.NewsItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TimeStoriesApp {

    public static void main(String[] args) throws IOException {
        try {
            // Fetch the Time.com webpage
            String url = "https://time.com";
            String htmlContent = fetchWebpage(url);

            // Extract the latest stories from the HTML content
            List<NewsItem> stories = extractDataFromHTML(htmlContent);

            // Convert the stories to a JSON object array
            String jsonResponse = toJson(stories);

            // Create and start the HTTP server
            startHttpServer(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fetchWebpage(String url) throws IOException {
        URL website = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) website.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }

        connection.disconnect();
        return content.toString();
    }

    private static List<Story> extractStories(String htmlContent) {
        List<Story> stories = new ArrayList<>();

        // Here, you need to implement the logic to parse the HTML content and extract the latest stories.
        // This involves using basic string processing techniques to locate the titles and links of the stories.
        // The example JSON in the assignment can be used as a guide.
        
        // For the sake of this code snippet, let's assume you have extracted the stories into a list of Story objects.
        // Replace this with your actual parsing logic.
        stories.add(new Story("Amy Schneider’s Jeopardy! Streak Ends at 40 Consecutive Wins and $1.4 Million",
        "https://time.com/6142934/amy-schneider-jeopardy-streak-ends/"));
        stories.add(new Story("'Writing With Fire' Shines a Light on All-Women News Outlet",
        "https://time.com/6142628/writing-with-fire-india-documentary/"));
        stories.add(new Story("Moderna Booster May Wane After 6 Months",
        "https://time.com/6142852/moderna-booster-wanes-omicron/"));
        stories.add(new Story("Pressure Mounts for Biden to Nominate a Black Woman to the Supreme",
        "https://time.com/6142743/joe-biden-supreme-court-nominee-black-woman-campaignpromise/"));
        stories.add(new Story("The James Webb Space Telescope Is in Position—And Now We Wait",
        "https://time.com/6142769/james-webb-space-telescope-reaches-l2/"));
        /*stories.add(new Story("We Urgently Need a New National COVID-19 Response Plan",
        "https://time.com/6142718/we-need-new-national-covid-19-response-plan/"));*/
        
        return stories;
        }
      
    
        
        private static List<NewsItem> extractDataFromHTML(String html) {
            List<NewsItem> newsItems = new ArrayList<NewsItem>();
    
            String listItemTag = "<li class=\"latest-stories__item\">";
            String anchorTagStart = "<a href=\"";
            String headlineTagStart = "<h3 class=\"latest-stories__item-headline\">";
            String tagEnd = "</";
    
            int startIndex = 0;
            int listItemStartIndex, anchorStartIndex, headlineStartIndex;
    
            while ((listItemStartIndex = html.indexOf(listItemTag, startIndex)) != -1) {
                anchorStartIndex = html.indexOf(anchorTagStart, listItemStartIndex);
                int anchorEndIndex = html.indexOf(tagEnd, anchorStartIndex + anchorTagStart.length());
                String href = html.substring(anchorStartIndex + anchorTagStart.length(), anchorEndIndex);
    
                headlineStartIndex = html.indexOf(headlineTagStart, anchorEndIndex);
                int headlineEndIndex = html.indexOf(tagEnd, headlineStartIndex + headlineTagStart.length());
                String itemheadline = html.substring(headlineStartIndex + headlineTagStart.length(), headlineEndIndex);
    
                newsItems.add(new NewsItem(href, itemheadline));
    
                startIndex = headlineEndIndex;
            }
    
            return newsItems;
        }
    

    private static String extractValue(String source, String startTag, String endTag) {
        int startIndex = source.indexOf(startTag) + startTag.length();
        int endIndex = source.indexOf(endTag, startIndex);
        return source.substring(startIndex, endIndex);
    }

    private static String toJson(List<NewsItem> stories) {
        String jsonResponse = "[" + "\n";
        for (NewsItem story : stories) {
            jsonResponse += "    { \"title\": \"" + story.getHref() + "\", \"link\": \"" + story.getItemHeadline() + "\" },\n";
        }
        jsonResponse += "]";
        return jsonResponse;
    }

    private static void startHttpServer(String response) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new RootHandler(response));
        server.setExecutor(null); // Use the default executor
        server.start();
        System.out.println("HTTP server started at: http://localhost:8000/getTimeStories");
    }

    private static class RootHandler implements HttpHandler {
        private final String response;

        public RootHandler(String response) {
            this.response = response;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

  


    private static class Story {
        private String title;
        private String link;

        public Story(String title, String link) {
            this.title = title;
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }
    }}


   private static class NewsItem {
            private String href;
            private String itemheadline;
    
            public NewsItem(String href, String itemheadline) {
                this.href = href;
                this.itemheadline = itemheadline;

            }
        public String getHref() {
            return href;
        }

        public String getItemHeadline() {
            return itemheadline;
        }
        }
    }

