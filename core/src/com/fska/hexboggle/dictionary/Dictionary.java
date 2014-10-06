package com.fska.hexboggle.dictionary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Dictionary {

	Set<String> dictionary;
	
	public Dictionary(){
		try{
			load();
		} catch (FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}
	}
	
	private void load() throws FileNotFoundException{
		dictionary = new HashSet<String>();
		
		FileHandle dict = Gdx.files.internal("american-english.txt");
		Scanner scan = new Scanner(dict.readString());
		String tmp;
		while(scan.hasNext()){
			tmp = scan.next();
			if(!tmp.contains("'s") && tmp.length() > 2)
				dictionary.add(tmp.toUpperCase());
			
		}
		scan.close();
	}
	
	public boolean lookup(String word){
		if(dictionary == null)
			return false;
		
		return dictionary.contains(word);
	}
}
