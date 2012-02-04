package hudson.plugins.selenium.configuration;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.plugins.selenium.SeleniumRunOptions;
import hudson.plugins.selenium.configuration.browser.Browser;
import hudson.plugins.selenium.configuration.browser.BrowserDescriptor;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.springframework.util.StringUtils;

public class CustomConfiguration extends Configuration {

	private int port = 4444;
    private boolean rcBrowserSideLog;
    private boolean rcDebug;
    private boolean rcTrustAllSSLCerts;
    private boolean rcBrowserSessionReuse;
    private String rcLog;
    private List<? extends Browser> browsers = new ArrayList<Browser>();

    @DataBoundConstructor
    public CustomConfiguration(int port, 
    							boolean rcBrowserSideLog, 
    							boolean rcDebug, 
    							boolean rcTrustAllSSLCerts, 
    							boolean rcBrowserSessionReuse,
    							String rcLog, 
    							List<? extends Browser> browsers) {
    	this.port = port;
    	this.rcBrowserSideLog = rcBrowserSideLog;
    	this.rcDebug = rcDebug;
    	this.rcTrustAllSSLCerts = rcTrustAllSSLCerts;
    	this.rcBrowserSessionReuse = rcBrowserSessionReuse;
    	this.rcLog = rcLog;
    	this.browsers = browsers;
    	
    }
    
    @Exported
    public String getRcLog(){
        return rcLog;
    }

    @Exported
    public boolean getRcBrowserSideLog(){
        return rcBrowserSideLog;
    }

    @Exported
    public boolean getRcDebug(){
        return rcDebug;
    }

    @Exported
    public boolean getRcTrustAllSSLCerts() {
        return rcTrustAllSSLCerts;
    }
    
    @Exported
    public boolean getRcBrowserSessionReuse() {
    	return rcBrowserSessionReuse;
    }
    
    @Exported
    public int getPort() {
    	return port;
    }

    @Exported
    public List<? extends Browser> getBrowsers() {
    	return browsers;
    }
    
	public DescriptorExtensionList<Browser, BrowserDescriptor> getBrowserTypes() {
		return Browser.all();
	}
	
	@Extension
	public static class DescriptorImpl extends ConfigurationDescriptor {

		@Override
		public String getDisplayName() {
			return "Custom configuration";
		}
		
		@Override
		public CustomConfiguration newInstance(StaplerRequest req, JSONObject json) {
			
			//String rcLog = json.getString("rcLog");
			
			return req.bindJSON(CustomConfiguration.class, json);
		}
		
		public static List<Descriptor<Browser>> getBrowserTypes() {
			List<Descriptor<Browser>> lst = new ArrayList<Descriptor<Browser>>();
			for (BrowserDescriptor b : Browser.all()) {
				lst.add(b);
			}
			return lst;
		}
		
	}

	@Override
	public SeleniumRunOptions initOptions(Computer c) {
		SeleniumRunOptions opt = new SeleniumRunOptions();
        opt.addOptionIfSet("-log", getRcLog());
        if (getRcBrowserSideLog()){
        	opt.addOption("-browserSideLog");
        }
        if (getRcDebug()){
        	opt.addOption("-debug");
        }
        if (getRcTrustAllSSLCerts()){
        	opt.addOption("-trustAllSSLCertificates");
        }
        if (getRcBrowserSessionReuse()) {
        	opt.addOption("-browserSessionReuse");
        }
        //addIfHasText(args, "-firefoxProfileTemplate", getRcFirefoxProfileTemplate());
        for (Browser b : browsers) {
        	b.initOptions(c, opt);
        }

		return opt;
	}
	
}