import twitter4j.*;

import java.util.*;

/**
 * Created by Dakota on 7/10/2016.
 */
public class Example {

    private static StatusListener GiveawayListener() {
        final int[] totalActions = {0};
        Twitter twitter = TwitterFactory.getSingleton();
        List<String> ReplyMessages = new ArrayList<String>();
        ReplyMessages.add("Thank you so much for doing an amazing giveaway!");
        ReplyMessages.add("Holy cow, that's an amazing giveaway");
        ReplyMessages.add("I sure hope I win some of this awesome stuff!");
        ReplyMessages.add("Thx a lot for the killer giveaway!");

        return new StatusListener() {
            @Override
            public void onStatus(Status status) {
                String message = status.getText().toLowerCase();

                // Find all tweets that say "giveaway" and aren't retweets
                if ((message.contains("giveaway") || message.contains("give away") || message.contains("follow to win")
                        || message.contains("rt to win") || message.contains("retweet to win") || message.contains("re tweet to win")
                        || message.contains("re-tweet to win") || message.contains("like to win") || message.contains("favorite to win"))
                        && !status.isRetweet()) {

                    Date date = new Date();

                    // Retweet message if needed
                    if (message.contains("rt") || message.contains("retweet") || message.contains("share")) {
                        try {
                            twitter.retweetStatus(status.getId());
                            totalActions[0]++;
                            System.out.println("" + Arrays.toString(totalActions) + " [" + date + "]Retweeted: " + status.getText());
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                    }

                    // Follow user if needed
                    if (message.contains("follow") || message.contains("f+") || message.contains("+f") || message.contains("f +")
                            || message.contains("+ f")) {
                        try {
                            twitter.createFriendship(status.getUser().getId());
                            totalActions[0]++;
                            System.out.println("" + Arrays.toString(totalActions) + " [" + date + "]Followed: " + status.getUser().getScreenName());
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                    }

                    // Favorite the tweet if needed
                    if (message.contains("favorite") || message.contains("fav") || message.contains("like") || message.contains("f+")
                            || message.contains("+f") || message.contains("f +") || message.contains("+ f")) {
                        try {
                            twitter.createFavorite(status.getId());
                            totalActions[0]++;
                            System.out.println("" + Arrays.toString(totalActions) + " [" + date + "]Favorited: " + status.getText());
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
                            totalActions[0]++;
                            System.out.println("" + Arrays.toString(totalActions) + " [" + date + "]Replied to: " + status.getUser().getScreenName() + "\r\nWith: " + response);
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message.contains("direct message") || message.contains("message") || message.contains("dm")) {
                        try {
                            String response = "Hey " + status.getUser().getName() + "I'd love to take place in your giveaway! Let me know what I need to do. Thank you, " + twitter.getScreenName();
                            twitter.sendDirectMessage(status.getUser().getId(), response);
                            totalActions[0]++;
                            System.out.println("" + Arrays.toString(totalActions) + " [" + date + "]DM sent to: " + status.getUser().getScreenName() + "\r\nWith: " + response);
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int i) {

            }

            @Override
            public void onScrubGeo(long l, long l1) {

            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
    }

    public static void main(String[] args) throws TwitterException, InterruptedException {
        Gui gui = new Gui(GiveawayListener());
        gui.setVisible(true);
    }
}
