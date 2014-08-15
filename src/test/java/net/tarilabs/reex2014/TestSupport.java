package net.tarilabs.reex2014;

import org.junit.Before;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 */
public class TestSupport {
	final static Logger LOG = LoggerFactory.getLogger(TestSupport.class);
	final static Logger RE_LOG = LoggerFactory.getLogger("ruleengine");
	
	KieSession session;
	
	@Before
	public void setup() {
    	KieServices kieServices = KieServices.Factory.get();
        KieContainer kContainer = kieServices.getKieClasspathContainer();
        KieBaseConfiguration kieBaseConf = kieServices.newKieBaseConfiguration();
		kieBaseConf.setOption( EventProcessingOption.STREAM );
        KieBase kieBase = kContainer.newKieBase(kieBaseConf);
       
        LOG.info("There should be rules: ");
        for ( KiePackage kp : kieBase.getKiePackages() ) {
        	for (Rule rule : kp.getRules()) {
        		System.out.println("kp" + kp + " rule " + rule.getName());
        	}
        }
        
        LOG.info("Creating kieSession");
        KieSessionConfiguration config = kieServices.newKieSessionConfiguration();
		config.setOption( ClockTypeOption.get("pseudo") );
        session = kieBase.newKieSession(config, null);
        
        LOG.info("Pupulating globals");
        session.setGlobal("LOG", RE_LOG);
	}
}
