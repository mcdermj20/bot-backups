package triteKnightBot;

/*
 *
 * Author: Jesse McDermott
 * Description:
 * 		Driver for pircbot implementation
 *		Not sure what else to change without actually doing something
 */

public class Main {

	public static void main(String[] args) throws Exception {

		String channelName = "#tritemare";

		// Start the bot up.
        triteKnightBot bot = new triteKnightBot();

        // Enable debugging output.
        bot.setVerbose(true);

        // Connect to an IRC server.
				//              server        port   passphrase, which in my case is a 0Auth token
        bot.connect("irc.twitch.tv", 6667, "oauth:qx8osywv2pcvyekr56ylaec6odnh5u");

        // Join the channel.
        bot.joinChannel(channelName);
				//Gets the bots name, not sure why, just never changed it. Outputs it maybe? Dunno.
        bot.getName();

	}
}
