package net.tarilabs.reex2014;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;
import org.kie.api.runtime.rule.QueryResultsRow;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

public class TweetTest extends TestSupport {
	
	@Test(timeout=10000)
	public void nothing() throws TwitterException {
		Status tweet = tweet("jut a simple twitter message");
		SessionPseudoClock sessionClock = session.getSessionClock();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        session.insert(tweet);
        session.fireAllRules();
        
        assertEquals("There should be no Alert", 0, session.getQueryResults("Alert").size());
        Cogito cogito = (Cogito) session.getQueryResults("cogitoergosum").iterator().next().get("$cogito");
		assertEquals("Last tweet should be present", 1000, cogito.getLastTweetTs());
		assertEquals("Cogito sentence should be Not much happened yet.", "not much happened yet.", cogito.getText().toLowerCase());
	}
	
	@Test(timeout=10000)
	public void delays() throws TwitterException {
		Status tweet = tweet("#M1 major delays sorry for this");
		SessionPseudoClock sessionClock = session.getSessionClock();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        session.insert(tweet);
        session.fireAllRules();
        
        @SuppressWarnings("unchecked")
		Alert<Status> alert = (Alert<Status>) session.getQueryResults("Alert").iterator().next().get("$alert");
        assertEquals("Alert should ref the Status", tweet, alert.getRef());
        Cogito cogito = (Cogito) session.getQueryResults("cogitoergosum").iterator().next().get("$cogito");
        assertEquals("Last tweet should be present", 1000, cogito.getLastTweetTs());
		assertTrue("Cogito sentence should contains word delay", cogito.getText().toLowerCase().contains("delay"));
	}
	
	@Test(timeout=10000)
	public void serviceInterrupt() throws TwitterException {
		Status tweet = tweet("#M1 è interrotta sulla tratta lorem - ipsum");
		SessionPseudoClock sessionClock = session.getSessionClock();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        session.insert(tweet);
        session.fireAllRules();
        
        @SuppressWarnings("unchecked")
		Alert<Status> alert = (Alert<Status>) session.getQueryResults("Alert").iterator().next().get("$alert");
        assertEquals("Alert should ref the Status", tweet, alert.getRef());
        Cogito cogito = (Cogito) session.getQueryResults("cogitoergosum").iterator().next().get("$cogito");
		assertTrue("Cogito sentence should contains phrase service interrupt", cogito.getText().toLowerCase().contains("service interrupt"));
	}
	
	@Test(timeout=10000)
	public void multiple() throws TwitterException {
		Status tweet1 = tweet("#M1 is the red line");		
		SessionPseudoClock sessionClock = session.getSessionClock();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        session.insert(tweet1);
        session.fireAllRules();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        Status tweet2 = tweet("#M1 is the one for Duomo");
		session.insert(tweet2);
        session.fireAllRules();
        
        Status[] tweets = new Status[]{tweet1, tweet2};
        
        @SuppressWarnings("unchecked")
		Alert<Status[]> alert = (Alert<Status[]>) session.getQueryResults("Alert").iterator().next().get("$alert");
		assertArrayEquals("Alert should ref the Statuses", tweets, alert.getRef());
        Cogito cogito = (Cogito) session.getQueryResults("cogitoergosum").iterator().next().get("$cogito");
		assertTrue("Cogito sentence should contains word multiple", cogito.getText().toLowerCase().contains("multiple"));
	}
	
	@Test(timeout=10000)
	public void multipleExtendedNoRepeatAlert() throws TwitterException {
		Status tweet1 = tweet("#M1 is the red line");		
		SessionPseudoClock sessionClock = session.getSessionClock();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        session.insert(tweet1);
        session.fireAllRules();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        Status tweet2 = tweet("#M1 is the one for Duomo");
		session.insert(tweet2);
        session.fireAllRules();
        
        Status[] tweets = new Status[]{tweet1, tweet2};
        
        @SuppressWarnings("unchecked")
		Alert<Status[]> alert = (Alert<Status[]>) session.getQueryResults("Alert").iterator().next().get("$alert");
		assertEquals("Alert type should be INFO", AlertType.INFO, alert.getType());
		assertArrayEquals("Alert should ref the Statuses", tweets, alert.getRef());
        Cogito cogito = (Cogito) session.getQueryResults("cogitoergosum").iterator().next().get("$cogito");
		assertTrue("Cogito sentence should contains word multiple", cogito.getText().toLowerCase().contains("multiple"));
		
		
		
		sessionClock.advanceTime(1, TimeUnit.MINUTES);
		session.insert(tweet("#M1 is the one for Duomo, really"));
        session.fireAllRules();
        
        assertEquals("There should be still just 1 Alert", 1, session.getQueryResults("Alert").size());
        @SuppressWarnings("unchecked")
		Alert<Status[]> alertExtractedAgain = (Alert<Status[]>) session.getQueryResults("Alert").iterator().next().get("$alert");
        assertEquals("Alert is still the same", alert, alertExtractedAgain);
		assertArrayEquals("Alert should ref the Statuses", tweets, alertExtractedAgain.getRef());
	}
	
	@Test(timeout=10000)
	public void multipleExtendedRepeatAlertInNewWindow() throws TwitterException {
		Status tweet1 = tweet("#M1 is the red line");		
		SessionPseudoClock sessionClock = session.getSessionClock();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        session.insert(tweet1);
        session.fireAllRules();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        Status tweet2 = tweet("#M1 is the one for Duomo");
		session.insert(tweet2);
        session.fireAllRules();
        
        Status[] tweets = new Status[]{tweet1, tweet2};
        
        @SuppressWarnings("unchecked")
		Alert<Status[]> alert = (Alert<Status[]>) session.getQueryResults("Alert").iterator().next().get("$alert");
		assertEquals("Alert type should be INFO", AlertType.INFO, alert.getType());
		assertArrayEquals("Alert should ref the Statuses", tweets, alert.getRef());
        Cogito cogito = (Cogito) session.getQueryResults("cogitoergosum").iterator().next().get("$cogito");
		assertTrue("Cogito sentence should contains word multiple", cogito.getText().toLowerCase().contains("multiple"));
		
		
		
		sessionClock.advanceTime(6, TimeUnit.MINUTES);
		Status tweet3 = tweet("#M1 is the one for Duomo, really");
		session.insert(tweet3);
        session.fireAllRules();
        
        assertEquals("There should be still just 1 Alert", 1, session.getQueryResults("Alert").size());
        @SuppressWarnings("unchecked")
		Alert<Status[]> alertExtractedAgain = (Alert<Status[]>) session.getQueryResults("Alert").iterator().next().get("$alert");
        assertEquals("Alert is still the same", alert, alertExtractedAgain);
		assertArrayEquals("Alert should ref the Statuses", tweets, alertExtractedAgain.getRef());
		
		
		
		sessionClock.advanceTime(1, TimeUnit.MINUTES);
		Status tweet4 = tweet("#M1 is the one for Duomo, really, for real.");
		session.insert(tweet4);
        session.fireAllRules();
        
        Status[] tweets2 = new Status[]{tweet3, tweet4};
        
        assertEquals("There should be 2 Alert", 2, session.getQueryResults("Alert").size());
        Iterator<QueryResultsRow> it = session.getQueryResults("Alert").iterator();
        @SuppressWarnings({ "unused", "unchecked" })
		Alert<Status[]> alert1 = (Alert<Status[]>) it.next().get("$alert");
		@SuppressWarnings("unchecked")
		Alert<Status[]> alert2 = (Alert<Status[]>) it.next().get("$alert");
		assertEquals("Alert2 type should be INFO", AlertType.INFO, alert2.getType());
		assertArrayEquals("Alert2 should ref the Statuses", tweets2, alert2.getRef());
        Cogito cogito2 = (Cogito) session.getQueryResults("cogitoergosum").iterator().next().get("$cogito");
		assertTrue("Cogito sentence should contains word multiple", cogito2.getText().toLowerCase().contains("multiple"));
	}
	
	/**
	 * Make a dummy Status twitter, with the supplied text. Please note this is only maintained for the text field, other attributes may be not maintained (eg.: hashtag, users, etc.)
	 * 
	 * @param text the tweet's text
	 * @return dummy Status twitter with the supplied text
	 * @throws TwitterException 
	 */
	private Status tweet(String text) throws TwitterException {
		String tweetJson = "{\"contributors\":null,"
				+ "\"text\":\""+text+"\","
				+ "\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,"
				+ "\"lang\":\"en\",\"entities\":{\"symbols\":[],\"urls\":[],"
				+ "\"hashtags\":[],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,"
				+ "\"id\":476021012371632120,\"source\":\"<a href=\\\"http://www.hootsuite.com\\\" rel=\\\"nofollow\\\">Hootsuite<\\/a>\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":0,\"created_at\":\"Mon Jun 09 15:20:28 +0000 2014\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"476021012371632120\","
				+ "\"place\":null,\"user\":{\"location\":\"Milano\",\"default_profile\":false,\"profile_background_tile\":false,\"statuses_count\":18715,\"lang\":\"it\",\"profile_link_color\":\"CC0033\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/988355810/1399291563\",\"id\":988355810,\"following\":false,\"protected\":false,\"favourites_count\":45,\"profile_text_color\":\"333333\","
				+ "\"description\":\" Asd\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"FFFFFF\","
				+ "\"name\":\"Asd\",\"profile_background_color\":\"E5E5E5\",\"created_at\":\"Tue Dec 04 08:48:07 +0000 2012\",\"is_translation_enabled\":false,\"default_profile_image\":false,\"followers_count\":31469,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/3148489174/d864431a925aba1d9f701120fe294442_normal.png\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://pbs.twimg.com/profile_background_images/378800000182749090/321-pxMY.png\",\"profile_background_image_url_https\":\"https://pbs.twimg.com/profile_background_images/378800000182749090/321-pxMY.png\",\"follow_request_sent\":false,\"entities\":{\"description\":{\"urls\":[]},\"url\":{\"urls\":[{\"expanded_url\":\"http://\",\"indices\":[0,22],\"display_url\":\"\",\"url\":\"\"}]}},\"url\":\"\",\"utc_offset\":7200,\"time_zone\":\"Rome\",\"notifications\":false,\"profile_use_background_image\":true,\"friends_count\":79,\"profile_sidebar_fill_color\":\"DDEEF6\","
				+ "\"screen_name\":\"ASd\",\"id_str\":\"988355810\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/3148489174/d864431a925aba1d9f701120fe294442_normal.png\",\"listed_count\":281,\"is_translator\":false},\"coordinates\":null}";
		Status status = (Status) DataObjectFactory.createObject(tweetJson);
		return status;
	}
}
