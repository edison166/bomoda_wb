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

public class CountComment {

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
	
	void CountComment(ArrayList input) throws JSONException, IOException{
		PrintWriter writer = new PrintWriter("./CountComment", "UTF-8");
		
		HashMap comment_kors = new HashMap();	
		HashMap comment_spade = new HashMap();	
		
		
		for(int i = 3; i < input.size(); i++){
			//System.out.println(input.get(i).toString());
			if(input.get(i).toString().contains("comment")){
				
				
				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(input.get(i).toString()), "gb2312"));
				
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
					String date = sb.toString();
					if(text.contains("Michael Kors")){
						
						if(comment_kors.containsKey(date)){
							comment_kors.put(date, (Integer)comment_kors.get(date) + 1);
						}else
							comment_kors.put(date, + 1);
					}
					if(text.contains("Kate Spade")){
						
						if(comment_spade.containsKey(date)){
							comment_spade.put(date, (Integer)comment_spade.get(date) + 1);
						}else
							comment_spade.put(date, + 1);
					}
					
				}
			}
		}
		writer.write("The number of comments for Michael Kors per day is ");
		writer.write("\n");
		Iterator it = comment_kors.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     writer.write(k);
		     writer.write("\t");
		     writer.write(String.valueOf(comment_kors.get(k)));
		     writer.write("\n");
		}
		writer.write("\n");
		
		writer.write("The number of comments for Kate Spade per day is ");
		writer.write("\n");
		it = comment_spade.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     writer.write(k);
		     writer.write("\t");
		     writer.write(String.valueOf(comment_spade.get(k)));
		     writer.write("\n");
		}
		writer.flush();
		writer.close();
		
	}
	
	
	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		CountComment ana = new CountComment();
		ana.refreshFileList("/Users/edisonzhao1/Downloads/weibo");
		ana.CountComment(ana.filelist);
	}

}
