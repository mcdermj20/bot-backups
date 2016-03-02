package triteKnightBot;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

import org.jibble.pircbot.*;

//Now, how does this work?


public class triteKnightBot extends PircBot {
	//Initial setup of some variables that are easier to just make global than bugger with passing them around.
	long secondsSinceCommand = System.nanoTime();
	long secondsSinceAd = System.nanoTime();
	//String knightNamesList = "R:\\\\Desktop\\\\Channel Bot Attempt\\\\names.txt";
	String modNameList = "C:\\\\Users\\\\helgr\\\\Google Drive\\\\Bot things\\\\mods.txt";
	String knightNameList = "C:\\\\Users\\\\helgr\\\\Google Drive\\\\Bot things\\\\names.txt";
	String editNameList = "C:\\\\Users\\\\helgr\\\\Google Drive\\\\Bot things\\\\names.txt.tmp";
	String timestampList = "C:\\\\Users\\\\helgr\\\\Google Drive\\\\Bot things\\\\timestamps.txt";
    ArrayList<String> users = new ArrayList<>(); //Dynamic list of those who've user either !name or !myname. Gets emptied every 2-5 minutes

	public triteKnightBot(){
		this.setName("triteknight_bot");
		System.out.println("\n\nNow running " + this.getName() + "!");
		System.out.println("Verbose has been turned off, so only actual sent messages will be shown.\n\n\n");
	}

        /*
        * This function takes a username and checks it against the arraylist of usernames that
        * have used the command recently. This list is cleared every 90 seconds.
        * Users are only added to the list if they are not already on it and only if they're using the
        * !name or !myname command
        */
    public int cmdUse(String username){
        username = username.toLowerCase(); // just in case it didn't actually get set to lower outside
        for (String s: users){
        	//Debugging, will print out the contents of the entire list before running through again to do the check.
            System.out.println(s);
        }
        for (String entries: users){
            if(entries.equalsIgnoreCase(username)){
                System.out.println("Found " + username + " in the list");
                return 19; // If 19 is returned, it's found that person in the list and basically ignores their request.
            }
        }
        users.add(username);
        System.out.println(username + " has been added to the list");
        //If it returns 42, it added their name to the list and continues on with their request for !name or !myname
        return 42;
        }

        // This is a modified function of the onMessage that initially came with Pircbot.
        // For Trite, I added a few commands specific to the channel and maintenance of such commands.
        /*
				!name
				!myname
				!editname user=knightname
				!addname user=knightname

				As well as an oncall listener that watches for moobot to say a certain phrase, triggering the
				recording of a video timestamp
        */
    public String getName(String username){
    	String knight;
    	username = username.toLowerCase();

			//System.out.println("\n\nThe username sent via chat was: " + userName + "\n\n");
                try (BufferedReader br = new BufferedReader(new FileReader(knightNameList)))
				{
					String line;
					while(true)
					{
						//reading line of file
						line = br.readLine();
						//EOF check, if null, then it's hit past the last row.
						if(line == null){
							//setting return value to something it should never be able to ACTUALLY be
							knight = "fuckme";
							secondsSinceCommand = System.nanoTime();
							br.close();
							return knight;
						}

						//if not EOF, split the line on the first equals to check username
						String user = line.split("=")[0].toLowerCase();


						//checking username of line against username given to function
						if(username.equals(user))
						{
							//Pulling off everything but the username
							String holdKnightandTime = line.split("=")[1];

							//Pulling the timestamp off of the end of the line
							String justKnight = holdKnightandTime.split("~")[0];

							//Setting the return variable to the name if they all match
							knight = justKnight;

							//resetting the counter
							secondsSinceCommand = System.nanoTime();
							br.close();
							return knight;
						}
					}
				}
				catch (Exception e)
				{
					System.err.println(e.getMessage());
				}
                knight = "fuckme";
                return knight;
		}

    public String newStamp(String theUrl)
    {

    	try
		{
			URL url = new URL(theUrl);
			URLConnection urlCon = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			String lineOfPage;
			lineOfPage = br.readLine();
			String breakTheLineUp[] = lineOfPage.split(",");
			// The 8th option is where the specific URL ID was. Found it through trial and error
			//System.out.println(breakTheLineUp[8]);
			String id = breakTheLineUp[8];
			id = id.split(":")[1];
			String test = id.replace('"', ' ');
			String test1 = test.trim();
			//System.out.println(test1);
			return test1;
		}
		catch(Exception e)
		{
			System.out.println(e);
			return "Oh shit bby someone broke it here";
		}
    }

	public void onMessage(String channel, String sender, String login, String hostname, String message)
        {
		//Little message logging, easier on the eyes than just the default verbose mode.
		String timestamp = new SimpleDateFormat("MM dd yyyy HH:mm:ss").format(new java.util.Date());
		System.out.println(timestamp + " ~~ " + sender + " >> " + message);

		//Some Variable Initialization/Setup that should be reset on each message
		long timeOfMessage = System.nanoTime(); //Pulling nanosecond time of the message received
		long elapsed = timeOfMessage - secondsSinceCommand; //Subtracting that from the launching time initially,
															//then against the last used time
		long totalRunning = System.nanoTime();
		long secondElapsed = totalRunning - secondsSinceAd;

		long difference = TimeUnit.NANOSECONDS.toSeconds(elapsed); // Converting nanoseconds to seconds
		long adDifference = TimeUnit.NANOSECONDS.toSeconds(secondElapsed); //Used for advertising the commands, not currently activated, commented out

		/**************************************************/
		/*                                                 /
		/* Moobot Timestamp Tracking Command		  	   /
		/*                                                 /
		/**************************************************/
		String mooTest = sender.toLowerCase(); // I don't really have to do this, it's just personal preference
		if(mooTest.equals("moobot")){
			if(message.startsWith("TimeStamp: ")){
				try(PrintWriter file = new PrintWriter(new BufferedWriter(new FileWriter(timestampList, true))))
				{
					// Trimming the command out and any trailing whitespace
                	String newStamp = message.substring(11).trim();
                    String temptimeCode = newStamp.split(":")[0];
                    temptimeCode = temptimeCode.substring(1);
                    String timeNoSpace = temptimeCode.replaceAll("\\s+", "");

                    if(!timeNoSpace.contains("m"))
                    {
                    	String noMin = "0m";
                    	String beforeMin = timeNoSpace.substring(0, timeNoSpace.indexOf('h')+1);
                    	String afterMin = timeNoSpace.substring(timeNoSpace.indexOf('h')+1);

                    	timeNoSpace = beforeMin.concat(noMin.concat(afterMin));

                    }

                    // Making a datetime
                	Date insertDate = new Date();
                    String insDateStr = new SimpleDateFormat("yyyy-MM-dd").format(insertDate);	//formatting said datetime

                    String urlIDThing = newStamp("https://api.twitch.tv/kraken/channels/tritemare/videos?broadcasts=true&limit=1");
                    String theUrl = "http://www.twitch.tv/tritemare/manager/" + urlIDThing + "/highlight?t=" + timeNoSpace;
                    file.println(insDateStr + " ~~~ " + newStamp + " ~~~ " + theUrl); // inserting date
                    //System.out.println("A new timestamp was added at this time!");
                    sendMessage(channel, "That timestamp has been saved, thanks!");

				}catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
        //Currently using this for clearing the recent users list instead of the ad.
		//probably should change variable names, but meh. Note, it should only bother with this when 2 minutes
		//has passed and there actually IS anything within the list. Could probably shorten it if I wanted, but meh.
		if(adDifference > 180)
		{
			//sendMessage(channel, "Feel free to try out the commands I can do! !name username for a requested persons knightly name, "
			//		+ "or !myname to request to view your own! (Only work's on subscriber's names, sorry!)");
			if(users.size()>0)
			{
				System.out.println("Recent Command users list was cleared!");
                users.clear();
                secondsSinceAd = System.nanoTime();
            }
		}
		//
		/**************************************************/
		/*                                                 /
		/* !name command				  				   /
		/*  Username must follow (!name user)              /
		/*                                                 /
		/**************************************************/
		if(message.startsWith("!name ") && (difference >= 25))
		{
			//sendMessage(channel, "This is a test of the startsWith function on a chat message.");
            int alreadyCalled = cmdUse(sender.toLowerCase());
            String userName = message.substring(6).trim();
            if (alreadyCalled == 19)
            {
            	//Lol, this is a console only message, seems a bit harsh, but it's all in jest
                System.out.println("Some twat named " + sender + " tried to use the command again before the list was refreshed.");

            } else if(userName.toLowerCase().equals("triteknight_bot"))
            {
				sendMessage(channel, "I am simply a scribe, I hold no title.");

			} else
			{
				String knightName = getName(userName);
				if (knightName.equals("fuckme"))
				{
					sendMessage(channel, "There was no knightly name found for " + userName + ". If they HAVE been knighted, "
							+ "please send a message to JaxXx_oL or one of the mods to get it added to the list!");
				}
				else
				{
					sendMessage(channel, userName + "'s knightly name is " + knightName);
				}
			}
		}

		/*****************************************/
		/*										 */
		/*	!myname command                      */
		/*										 */
		/*	Pulls the senders username into      */
		/*	essentially the !name command        */
		/*                                       */
		/*****************************************/

		if(message.startsWith("!myname") && (difference >= 25))
		{
			//sendMessage(channel, "This is a test of the startsWith function on a chat message.");
			String userName = sender;
            int called = cmdUse(sender.toLowerCase()); // checking if user has used command since last clearing of the list
            if (called == 19)
            {
            	//Lol, this is a console only message, seems a bit harsh, but it's all in jest
                System.out.println("Some twat named " + sender + " tried to use the command again before the list was refreshed.");
            }
            else
            {
				String knightName = getName(userName);
				if (knightName.equals("fuckme"))
				{
					sendMessage(channel, "There was no knightly name found for " + userName + ". If they HAVE been knighted, "
							+ "please send a message to JaxXx_oL or one of the mods to get it added to the list!");
				}
				else
				{
					sendMessage(channel, userName + "'s knightly name is " + knightName);
				}
			}
        }


		/*****************************************/
		/*										 */
		/*	!editname command                    */
		/*										 */
		/*	Allows for the editing of a name     */
		/*	that may have been entered wrong     */
		/*  (Durrr We spul gud)                  */
		/*									     */
		/*****************************************/
		if(message.startsWith("!editname "))
		{
			try (BufferedReader br = new BufferedReader(new FileReader(modNameList)))
			{
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\\\Users\\\\helgr\\\\Google Drive\\\\Bot things\\\\names.txt.tmp")));
				String modline;
				String userName = sender;
				String nameToAdd = message.substring(10).split("=")[0];
				//System.out.println(nameToAdd);
				while(true)
				{
					//System.out.println(line); Debugging, prints every line of the file
					modline = br.readLine();
					if(modline == null) // If we get to the end of the modlist and haven't matched someone
					{
						sendMessage(channel, "I'm sorry, only mods are allowed to use this command!");
						break;
					}
					modline = modline.toLowerCase().trim();
					userName = userName.toLowerCase().trim();
					//System.out.println(modline + "  <<<<>>>>  " + userName); //debugging for modlist comparison
					if(userName.equals(modline))
					{
						try (BufferedReader nbr = new BufferedReader(new FileReader(knightNameList)))
						{
							String testline="";
							while(true){
								testline = nbr.readLine();

								if(testline == null)
								{
									writer.close();
									break;
								}
								if(testline.split("=")[0].toLowerCase().equals(nameToAdd.toLowerCase())){
									Date insertDate = new Date();
									String insDateStr = new SimpleDateFormat("yyyy-MM-dd").format(insertDate);
									String nameAndKnight = message.substring(10);
									writer.println(nameAndKnight + " ~" + insDateStr);
								}
								else{
									writer.println(testline);
								}
							}

						}
						catch (Exception e){
							System.err.println(e.getMessage());
						}
					File realFile = new File("C:\\\\Users\\\\helgr\\\\Google Drive\\\\Bot things\\\\names.txt");
					realFile.delete();
					new File("C:\\\\Users\\\\helgr\\\\Google Drive\\\\Bot things\\\\names.txt.tmp").renameTo(realFile);
					sendMessage(channel, "That name has been edited, thanks!");
					writer.close();
					break;
					}

				}
			}
			catch (Exception e)
			{
				System.err.println(e.getMessage());
			}

		}
		/*****************************************/
		/*										 */
		/*	!addname command                     */
		/*										 */
		/*  Adds a name to the textfile          */
		/*****************************************/
		if(message.startsWith("!addname "))
		{
			try (BufferedReader br = new BufferedReader(new FileReader(modNameList)))
			{
				String modline;
				String userName = sender;
				String nameToAdd = message.substring(9).split("=")[0];
				//System.out.println(nameToAdd);

				while(true)
				{
					//System.out.println(line); Debugging, prints every line of the file
					modline = br.readLine();
					if(modline == null) // If we get to the end of the modlist and haven't matched someone
					{
						sendMessage(channel, "I'm sorry " + sender + ", only mods are allowed to use this command!");
						break;
					}
					modline = modline.toLowerCase().trim();
					userName = userName.toLowerCase().trim();
					//System.out.println(modline + "  <<<<>>>>  " + userName); //debugging for modlist comparison
					if(userName.equals(modline))
					{
						try (BufferedReader nbr = new BufferedReader(new FileReader(knightNameList)))
						{
							String testline="";
							while(true){
								 //Debugging, prints every line of the file
								testline = nbr.readLine();
								//System.out.println(testline);
								//Line is empty
								if(testline == null)
								{
									try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(knightNameList, true))))
									{
										String nameAndKnight = message.substring(9);
										Date insertDate = new Date();
										String insDateStr = new SimpleDateFormat("yyyy-MM-dd").format(insertDate);

										out.println(nameAndKnight + " ~" + insDateStr);
										sendMessage(channel, "That name has now been added, thanks!");
										break;

									}catch (IOException e) {
										System.err.println(e.getMessage());
									}
								}
								//Line Is not empty
								String user = testline.split("=")[0]; // split on =, choosing value on left
								String userL = user.toLowerCase(); // lowercasing for matching easiness
								nameToAdd = nameToAdd.toLowerCase();
								//compare name sent via chat to names in list
								if(nameToAdd.equals(userL))
								{   // comparison
									//If there's already a name, don't add another, duh.
									sendMessage(channel, "That user has already been knighted!");
									nbr.close();
									break;
								}
							}
						}
						catch (Exception e){
							System.err.println(e.getMessage());
						}
					break;
					}
				}
			}
			catch (Exception e)
			{
				System.err.println(e.getMessage());
			}
		}
	}
}
