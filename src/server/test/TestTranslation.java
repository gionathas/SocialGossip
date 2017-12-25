package server.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.model.Translator;

public class TestTranslation 
{	
	public static void main(String[] args) throws Exception 
	{
		String text = Translator.translate("ciao come stai? ","it","en");
		
		System.out.println(text);
	}
}
