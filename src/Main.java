import twitter4j.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Dakota on 7/10/2016.
 */
public class Main {
    public static void main(String[] args) throws TwitterException {
        List<String> ReplyMessages = new ArrayList<String>();
        ReplyMessages.add("Thank you so much for doing an amazing giveaway!");
        ReplyMessages.add("Holy cow, that's an amazing giveaway");
        ReplyMessages.add("I sure hope I win some of this awesome stuff!");
        ReplyMessages.add("Thx a lot for the killer giveaway!");

        Twitter twitter = TwitterFactory.getSingleton();
        twitter4j.Query query = new Query("\"giveaway\" \"win\"");
        query.setCount(100);
        QueryResult results = twitter.search(query);

//        System.out.println(results.getTweets());

        for (Status status : results.getTweets()) {
            String message = status.getText().toLowerCase();

            // Find all tweets that say "giveaway" and aren't retweets
            if ((message.contains("giveaway") || message.contains("give away") || message.contains("follow to win")
                    || message.contains("rt to win") || message.contains("retweet to win") || message.contains("re tweet to win")
                    || message.contains("re-tweet to win") || message.contains("like to win") || message.contains("favorite to win"))
                    && !status.isRetweet() && !status.isRetweetedByMe()) {

                // Retweet message if needed
                if (message.contains("rt") || message.contains("retweet") || message.contains("share")) {
                    try {
                        twitter.retweetStatus(status.getId());
                        System.out.println("Retweeted: " + status.getText());
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }

                // Follow user if needed
                if (message.contains("follow")) {
                    try {
                        twitter.createFriendship(status.getUser().getId());
                        System.out.println("Followed: " + status.getUser().getScreenName());
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }

                // Favorite the tweet if needed
                if (message.contains("favorite")) {
                    try {
                        twitter.createFavorite(status.getId());
                        System.out.println("Favorited: " + status.getText());
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }

                // Reply to message if needed
                if (message.contains("comment") || message.contains("reply")) {
                    Random random = new Random();
                    String response = ReplyMessages.get(random.nextInt(ReplyMessages.size()));
                    StatusUpdate statusUpdate = new StatusUpdate(response);
                    statusUpdate.inReplyToStatusId(status.getId());
                    try {
                        twitter.updateStatus(statusUpdate);
                        System.out.println("Replied to: " + status.getUser().getScreenName() + "\r\nWith: " + response);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
