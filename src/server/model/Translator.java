package server.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Translator 
{
	public static int LANGUAGE_STRING_LEN = 2;
	private final static String path = "https://api.mymemory.translated.net/get?";
	private final static String queryTag = "q=";
	private final static String andTag = "&";
	private final static String divideLangTag = "|";
	private final static String langpairTag = "langpair=";
	private final static String spaceDelimiter = "%20";
	
	/**
	 * Traduce un testo da una lingua ad un altra,tramite il servizio MyMemoryTranslated
	 * @param text testo da tradurre
	 * @param fromLang lingua originale
	 * @param toLang lingua in cui tradurre
	 * @return traduzione del testo richiesto,altrimenti testo originale se non si e' riuscita la traduzione
	 * @throws Exception se ci sono errori nella traduzione del testo
	 */
	public static String translate(String text,String fromLang,String toLang) throws Exception
	{
		if(text == null || fromLang == null || toLang == null)
			throw new NullPointerException();
		
		if(text.isEmpty() || fromLang.length() != LANGUAGE_STRING_LEN || toLang.length() != LANGUAGE_STRING_LEN)
			throw new IllegalArgumentException();
		
		//creo url per la richiesta al server REST MyMemory
		String requestURL = createRequestURL(text,fromLang,toLang);
		
		URL url = new URL(requestURL);
		
		//apro connessione con il server REST
		URLConnection uc = url.openConnection();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		
		String line = null;
		StringBuffer response = new StringBuffer();
		
		//leggo risposta
		while((line = in.readLine())!= null) {
			response.append(line);
		}
		
		//parso risposta json
		JSONObject obj = (JSONObject) new JSONParser().parse(response.toString());
		
		//analizzo risposta del server
		JSONObject result = (JSONObject) obj.get("responseData");
		
		//se la risposta e' valida
		if(result != null) {
			return result.get("translatedText").toString();
		}
		
		return text;
		
	}
	
	private static String createRequestURL(String text,String fromLang,String toLang)
	{
		//sostituisco tutti gli spazi con un nuovo carattere,per una migliore traduzione
		String betterText = text.replace(" ",spaceDelimiter);
		
		return path+queryTag+betterText+andTag+langpairTag+fromLang+divideLangTag+toLang;
	}
}
