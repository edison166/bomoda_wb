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

public class WordCount3 {

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
	
	void WordCount3(ArrayList input) throws IOException, JSONException{
		
		PrintWriter writer = new PrintWriter("./WordCount3", "UTF-8");
		
		HashMap top10_kors = new HashMap();	
		ArrayList tmp_10_spade = new ArrayList();	
		HashMap top10_spade = new HashMap();	
		ArrayList tmp_10_kors = new ArrayList();
		
		for(int i = 3; i < input.size(); i++){
			//System.out.println(input.get(i).toString());
			if(input.get(i).toString().contains("comments")){
				
				
				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(input.get(i).toString()), "gb2312"));
				
				String str;
				while ((str = in.readLine()) != null) {
					//System.out.println(str);
					JSONObject jsonObj = new JSONObject(str);
					
					String text = jsonObj.getString("text");
					//System.out.println(text);
					StringReader reader = new StringReader(text);
					TokenStream ts = analyzer.tokenStream("", reader);
					CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
					while (ts.incrementToken()) {		
						String word = term.toString();
						//System.out.println(word);
						if(text.toLowerCase().replace(" ", "").contains("michaelkors"))
							if(top10_kors.containsKey(word)){
								top10_kors.put(word, (Integer)top10_kors.get(word) + 1);
							}else
								top10_kors.put(word, + 1);
						if(text.toLowerCase().replace(" ", "").contains("katespade"))
							if(top10_spade.containsKey(word)){
								top10_spade.put(word, (Integer)top10_spade.get(word) + 1);
							}else
								top10_spade.put(word, + 1);
					}
					
				}
			}
		}
		//System.out.println("");
		Iterator it = top10_kors.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     
		     tmp_10_kors.add(new ob(k, (Integer)top10_kors.get(k)));
		}
		Collections.sort(tmp_10_kors, Collections.reverseOrder());
		writer.write("Top 10 mentioned Chinese terms associated with Michael Kors are ");
		writer.write("\n");
		for(int i = 0; i < (tmp_10_kors.size() > 10 ? 10 : tmp_10_kors.size()); i++){
			writer.write(((ob)tmp_10_kors.get(i)).name + "\t" +  String.valueOf(((ob)tmp_10_kors.get(i)).score));
			writer.write("\n");
		}
		writer.write("\n");
		it = top10_spade.keySet().iterator();
		while(it.hasNext()){    
		     String k = it.next().toString();
		     
		     tmp_10_spade.add(new ob(k, (Integer)top10_spade.get(k)));
		}
		Collections.sort(tmp_10_spade, Collections.reverseOrder());
		writer.write("Top 10 mentioned Chinese terms associated with Kate Spade are ");
		writer.write("\n");
		for(int i = 0; i < (tmp_10_spade.size() > 10 ? 10 : tmp_10_spade.size()); i++){
			writer.write(((ob)tmp_10_spade.get(i)).name + "\t" +  String.valueOf(((ob)tmp_10_spade.get(i)).score));
			writer.write("\n");
		}
		writer.flush();
		writer.close();
		
	}
	
	
	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		WordCount3 ana = new WordCount3();
		ana.refreshFileList("/Users/edisonzhao1/Downloads/weibo");
		ana.WordCount3(ana.filelist);
	}

}
