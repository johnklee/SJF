package sjf.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class StatJSonWrapper {
	private HashMap<String,Integer> cateCntMap = null;
	
	public StatJSonWrapper(){}
	public StatJSonWrapper(HashMap<String,Integer> cateCntMap)
	{
		this.cateCntMap = cateCntMap;
	}
	
	public static String EmptyString()
	{
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("{");  
    	strBuf.append("metaData : {totalProperty: 'results',");  
    	strBuf.append("root: 'rows',");  
    	strBuf.append("id: 'category' ,");  
    	strBuf.append("fields : [");  
    	strBuf.append("{name: 'category', mapping:'category'},");  
    	strBuf.append("{name: 'count', mapping:'count'}");      	 
    	strBuf.append("]},");  
    	strBuf.append("results : 0,");  
    	strBuf.append("rows : []}");
    	return strBuf.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuffer strBuf = new StringBuffer();  
	    if(cateCntMap==null || cateCntMap.size()==0)
	    {
	    	return EmptyString();  		    
	    }
	    else
	    {
	    	strBuf.append("{");  
	    	strBuf.append("metaData : {totalProperty: 'results',");  
	    	strBuf.append("root: 'rows',");  
	    	strBuf.append("id: 'category' ,");  
	    	strBuf.append("fields : [");  
	    	strBuf.append("{name: 'category', mapping:'category'},");  
	    	strBuf.append("{name: 'count', mapping:'count'}");  
		    strBuf.append("]},");  
		    strBuf.append(String.format("results : %d,", cateCntMap.size()));  
		    strBuf.append("rows : [");  
		    boolean isFirst=true;
		    Iterator<Entry<String,Integer>> iter = cateCntMap.entrySet().iterator();
		    while(iter.hasNext())
		    {
		    	Entry<String,Integer> ety = iter.next();
            	//System.out.printf("\t\t%s=%d\n", ety.getKey(), ety.getValue());
            	if(isFirst)
            	{
            		strBuf.append(String.format("{category:'%s',count:%d}", ety.getKey(), ety.getValue()));
            		isFirst = false;
            	}
            	else
            	{
            		strBuf.append(String.format(",{category:'%s',count:%d}", ety.getKey(), ety.getValue()));
            	}
		    }
		    strBuf.append("]");  
		    strBuf.append("}");
	    }
	    return strBuf.toString();
	}
}
