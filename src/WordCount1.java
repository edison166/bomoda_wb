import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.wltea.analyzer.lucene.IKAnalyzer;


//Since the time is limited, I just use the original name of the brand, actually we could use
// regular expressionv for "Michael Kors|MichaelKors|MK" etc.

public class WordCount1 {
	
	class ob implements Comparable<ob> {
		String name;
		int score;

		public ob(String u, int s) {
			this.name = u;
			this.score = s;
		}

		public int compareTo(ob o) {

			if (this.score == o.score) {
				return 0;
			} else if (this.score > o.score) {
				return 1;
			} else
				return -1;

		}
	}
	

	IKAnalyzer analyzer = new IKAnalyzer(true);
	private  static ArrayList  filelist = new ArrayList(); 
	
	public static void refreshFileList(String strPath) { 
        File dir = new File(strPath); 
        File[] files = dir.listFiles(); 
        
        if (files == null) 
            return; 
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
                refreshFileList(files[i].getAbsolutePath()); 
            } else { 
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                //System.out.println("---"+strFileName);
                if(!strFileName.contains("DS_Store"))
                	filelist.add(files[i].getAbsolutePath());                    
            } 
        } 
    }
	
	void WordCount1(ArrayList input) throws IOException, JSONException{
		HashMap res_kors = new HashMap();
		HashMap res_spade = new HashMap();
		PrintWriter writer = new PrintWriter("./WordCount1_a", "UTF-8");
		PrintWriter writer2 = new PrintWriter("./WordCount1_b", "UTF-8");
		HashMap loc = new HashMap();
		
		int all_kors = 0;
		int all_spade = 0;
		for(int i = 3; i < input.size(); i++){
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(input.get(i).toString()), "gb2312"));
			//System.out.println(input.get(i).toString());
			String str;
			while ((str = in.readLine()) != null) {
				//System.out.println(str);
				JSONObject jsonObj = new JSONObject(str);
				
				
				String text = jsonObj.getString("text");
				JSONObject user = jsonObj.getJSONObject("user");		
				String name = user.getString("name");
				String location = user.getString("location");
				String help[] = location.split(" ");
				
				if(help.length == 2){
					location = help[1];
				}
				
				if(loc.containsKey(location)){
					loc.put(location, (Integer)loc.get(location) + 1);
				}else
					loc.put(location, 1);
				
				
				if(text.toLowerCase().replace(" ", "").contains("michaelkors")){
					all_kors++;
					if(res_kors.containsKey(name)){
						res_kors.put(name, (Integer)res_kors.get(name) + 1);
					}else
						res_kors.put(name, 1);
				}
				if(text.toLowerCase().replace(" ", "").contains("katespade")){
					all_spade++;
					if(res_spade.containsKey(name)){
						res_spade.put(name, (Integer)res_spade.get(name) + 1);
					}else
						res_spade.put(name, 1);
				}
				//System.out.println(all_kors);
			}
			
		}
		
		ArrayList tmp_kors = new ArrayList();
		ArrayList tmp_spade = new ArrayList();
		ArrayList tmp_loc = new ArrayList();
		writer.write("Michael Kors");
		writer.write("\t");
		writer.write(String.valueOf(all_kors));
		writer.write("\n");
		writer.write("\n");
		Iterator it = res_kors.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     writer.write(k);
		     writer.write("\t");
		     writer.write(String.valueOf((Integer)res_kors.get(k)));
		     writer.write("\n");
		     
		     tmp_kors.add(new ob(k, (Integer)res_kors.get(k)));
		}
		
		writer.write("Kate Spade");
		writer.write("\t");
		writer.write(String.valueOf(all_spade));
		writer.write("\n");
		writer.write("\n");
		it = res_spade.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     writer.write(k);
		     writer.write("\t");
		     writer.write(String.valueOf((Integer)res_spade.get(k)));
		     writer.write("\n");
		    
		    	 tmp_spade.add(new ob(k, (Integer)res_spade.get(k)));
		}
		writer.flush();
		writer.close();
		
		it = loc.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     tmp_loc.add(new ob(k, (Integer)loc.get(k)));
		}
		Collections.sort(tmp_loc, Collections.reverseOrder());
		writer2.write("The top 10 locations with total post are: ");
		writer2.write("\n");
		writer2.write("\n");
		for(int i = 0; i < (tmp_loc.size() > 10 ? 10 : tmp_loc.size()); i ++){
			writer2.write(((ob)tmp_loc.get(i)).name + "\t" +  String.valueOf(((ob)tmp_loc.get(i)).score));
			writer2.write("\n");
		}
		writer2.write("\n");
		writer2.write("\n");
		
		Collections.sort(tmp_kors, Collections.reverseOrder());
		writer2.write("As for Michael Kors the top 10 users with total post are: ");
		writer2.write("\n");
		writer2.write("\n");
		for(int i = 0; i < (tmp_kors.size() > 10 ? 10 : tmp_kors.size()); i ++){
			writer2.write(((ob)tmp_kors.get(i)).name + "\t" +  String.valueOf(((ob)tmp_kors.get(i)).score));
			writer2.write("\n");
		}
		
		writer2.write("\n");
		writer2.write("\n");
		
		Collections.sort(tmp_spade, Collections.reverseOrder());
		writer2.write("As for Kate Spade the top 10 users with total post are: ");
		writer2.write("\n");
		writer2.write("\n");
		for(int i = 0; i < (tmp_spade.size() > 10 ? 10 : tmp_spade.size()); i ++){
			writer2.write(((ob)tmp_spade.get(i)).name + "\t" +  ((ob)tmp_spade.get(i)).score);
			writer2.write("\n");
		}
		
		
		writer2.flush();
		writer2.close();
		
	}
	
	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		WordCount1 ana = new WordCount1();
		ana.refreshFileList("/Users/edisonzhao1/Downloads/weibo");
		ana.WordCount1(ana.filelist);
	}

}
