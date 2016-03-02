package triteKnightBot;

/*
 *
 * Author: Jesse McDermott
 * Description:
 * 		Driver for pircbot implementation
 * 
 */

public class Main {

	public static void main(String[] args) throws Exception {

		String channelName = "#tritemare";

		// Start the bot up.
        triteKnightBot bot = new triteKnightBot();

        // Enable debugging output.
        bot.setVerbose(true);

        // Connect to an IRC server.
        bot.connect("irc.twitch.tv", 6667, "oauth:qx8osywv2pcvyekr56ylaec6odnh5u");

        // Join the channel.
        bot.joinChannel(channelName);
        bot.getName();

	}
}
