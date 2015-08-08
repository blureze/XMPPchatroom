package com.javacodegeeks.xmpp;

import com.memetix.mst.detect.Detect;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class Translator {
	private String translatedText;
	
	public Translator(String str) throws Exception {
        // Set the Client ID / Client Secret once per JVM. It is set statically and applies to all services
        Translate.setClientId("chatroom_project");
        Translate.setClientSecret("HhV4U2WXfimyl/RfVrsViCqVdkvNcuvI/b3mtNyX+kM");
        Detect.setClientId("chatroom_project");
        Detect.setClientSecret("HhV4U2WXfimyl/RfVrsViCqVdkvNcuvI/b3mtNyX+kM");
        
        // Detect returns a Language Enum representing the language code
        Language detectedLanguage = Detect.execute(str);
        
        if(detectedLanguage.toString() == "en") {
        	// From English -> Chinese - AUTO_DETECT the From Language
            translatedText = Translate.execute(str, Language.CHINESE_TRADITIONAL);
            System.out.println("English AUTO_DETECT -> Chinese: " + translatedText);	
        }
        else {
        	// From Chinese -> English
            translatedText = Translate.execute(str, Language.CHINESE_TRADITIONAL, Language.ENGLISH);
            System.out.println("Chinese AUTO_DETECT -> English: " + translatedText);
        }
        
	}
	
	public String getTranslatedText() {
		return translatedText;
	}
}
