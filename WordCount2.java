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

public class WordCount2 {
	
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
	
	void WordCount2(ArrayList input) throws JSONException, IOException{
		
		PrintWriter writer2 = new PrintWriter("./WordCount2_a", "UTF-8");
		PrintWriter writer = new PrintWriter("./WordCount2_b", "UTF-8");
		HashMap res_kors = new HashMap();
		HashMap res_spade = new HashMap();
		HashMap peek_hour = new HashMap();
		
		ArrayList tmp_peek = new ArrayList();
		ArrayList tmp_kors = new ArrayList();
		ArrayList tmp_spade = new ArrayList();
		
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
				String time = user.getString("created_at");
				
				String s[] = time.split(" ");
				StringBuilder sb = new StringBuilder();
				sb.append(s[0]);
				sb.append(" ");
				sb.append(s[1]);
				sb.append(" ");
				sb.append(s[2]);
				String s2[] = s[3].split(":");
			
					
				if(peek_hour.containsKey(s2[0])){
					peek_hour.put(s2[0], (Integer)peek_hour.get(s2[0]) + 1);
				}else
					peek_hour.put(s2[0], + 1);
				
				
				String date = sb.toString();
				
				if(text.toLowerCase().replace(" ", "").contains("michaelkors")){
					
					if(res_kors.containsKey(date)){
						res_kors.put(date, (Integer)res_kors.get(date) + 1);
					}else
						res_kors.put(date, + 1);
				}
				if(text.toLowerCase().replace(" ", "").contains("katespade")){
					
					if(res_spade.containsKey(date)){
						res_spade.put(date, (Integer)res_spade.get(date) + 1);
					}else
						res_spade.put(date, + 1);
				}
				
			}
		}
		
		Iterator it = peek_hour.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     
		     tmp_peek.add(new ob(k, (Integer)peek_hour.get(k)));
		}
		
		it = res_kors.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     
		     tmp_kors.add(new ob(k, (Integer)res_kors.get(k)));
		}
		
		it = res_spade.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     
		     tmp_spade.add(new ob(k, (Integer)res_spade.get(k)));
		}
		
		Collections.sort(tmp_kors, Collections.reverseOrder());
		writer2.write("Michael Kors ");
		writer2.write("\n");
		writer2.write("The date that has the highest number of posts is ");
		writer2.write(((ob)tmp_kors.get(0)).name);
		writer2.write("\n");
		writer2.write("The highest number of posts is ");
		writer2.write(String.valueOf(((ob)tmp_kors.get(0)).score));
		writer2.write("\n");
		
		writer2.write("\n");
		Collections.sort(tmp_spade, Collections.reverseOrder());
		writer2.write("Kate Spade ");
		writer2.write("\n");
		writer2.write("The date that has the highest number of posts is ");
		writer2.write(((ob)tmp_spade.get(0)).name);
		writer2.write("\n");
		writer2.write("The highest number of posts is ");
		writer2.write(String.valueOf(((ob)tmp_spade.get(0)).score));
		writer2.write("\n");
		
		writer2.flush();
		writer2.close();
		
		Collections.sort(tmp_peek, Collections.reverseOrder());
		writer.write("The peek hour is ");
		writer.write(((ob)tmp_peek.get(0)).name);
		writer.write("\n");
		writer.write("The post number is ");
		writer.write(String.valueOf(((ob)tmp_peek.get(0)).score));
		writer.write("\n");
		writer.flush();
		writer.close();
		
		
	}
	
	
	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		WordCount2 ana = new WordCount2();
		ana.refreshFileList("/Users/edisonzhao1/Downloads/weibo");
		ana.WordCount2(ana.filelist);

	}

}
