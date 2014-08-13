package net.tarilabs.reex2014;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * You can find examples on how to create RSS with Rome at http://rometools.github.io/rome/RssAndAtOMUtilitiEsROMEV0.5AndAboveTutorialsAndArticles/RssAndAtOMUtilitiEsROMEV0.5TutorialUsingROMEToCreateAndWriteASyndicationFeed.html
 *
 */
public class RomeRssTest extends TestSupport {
	private static final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(timeout=10000)
	public void uno() throws ParseException {
		SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");

        feed.setTitle("Sample Feed (created with ROME)");
        feed.setLink("http://google.com");
        feed.setDescription("This feed has been created using ROME (Java syndication utilities");

        List entries = new ArrayList();
        SyndEntry entry;
        SyndContent description;

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v1.0");
        entry.setLink("http://google.com");
        entry.setPublishedDate(DATE_PARSER.parse("2004-06-08"));
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Initial release of ROME");
        entry.setDescription(description);
        entries.add(entry);

        feed.setEntries(entries);
        
        SessionPseudoClock sessionClock = session.getSessionClock();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        session.insert(feed);
        session.fireAllRules();
        
        assertEquals("There should be no Alert", 0, session.getQueryResults("Alert").size());
        Cogito cogito = (Cogito) session.getQueryResults("cogitoergosum").iterator().next().get("$cogito");
		assertEquals("Last RSS should be present", 1000, cogito.getLastRssTs());
		assertEquals("Cogito sentence should be Not much happened yet.", "not much happened yet.", cogito.getText().toLowerCase());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(timeout=10000)
	public void due() throws ParseException {
		SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");

        feed.setTitle("Sample Feed (created with ROME)");
        feed.setLink("http://google.com");
        feed.setDescription("This feed has been created using ROME (Java syndication utilities");

        List entries = new ArrayList();
        SyndEntry entry;
        SyndContent description;

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v1.0");
        entry.setLink("http://google.com");
        entry.setPublishedDate(DATE_PARSER.parse("2004-06-08"));
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Initial\n\r dsdassasda \n\r dsadsadsadas SciOpeRo nazionale del trasporto pubblico locale STRIKE release of ROME");
        entry.setDescription(description);
        entries.add(entry);

        feed.setEntries(entries);
        
        SessionPseudoClock sessionClock = session.getSessionClock();
        sessionClock.advanceTime(1, TimeUnit.SECONDS);
        session.insert(feed);
        session.fireAllRules();
        
        assertEquals("There should be 1 Alert", 1, session.getQueryResults("Alert").size());
        Alert<SyndEntryImpl> alert = (Alert<SyndEntryImpl>) session.getQueryResults("Alert").iterator().next().get("$alert");
        assertEquals("Alert should ref the SyndEntry", entry, alert.getRef());
        Cogito cogito = (Cogito) session.getQueryResults("cogitoergosum").iterator().next().get("$cogito");
		assertEquals("Last RSS should be present", 1000, cogito.getLastRssTs());
		assertTrue("Cogito sentence should contains word strike", cogito.getText().toLowerCase().contains("strike"));
		
	}
}
