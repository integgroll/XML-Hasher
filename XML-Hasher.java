import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
/*
 * This is the class I am using to manage my XML stuff so that I can access it rather simply in a hash
 * Probably won't make sense to everyone but it makes sense to me.
 * 
 * All the examples are assuming the following XML 
 * 
 * <data>
 * 	<phones operatingSystem="Android">
 * 	 <Motorola name="Sholes" releaseDate="Not soon enough" carrier="Verizon" />
 * 	 <Motorola name="Cliq" releaseDate="October '09" carrier="T-Mobile" />
 * 	</phones>
 * 	<carrier name="Sprint" whatAboutIt="They have crappy phones and I am leaving for the Sholes!" />
 * </data>
 * 
 */
public class XMLHashHandler extends DefaultHandler {

	public HashMap<String,String> data = new HashMap<String,String>();
	static private int tempSize;
	static private HashMap<String,Integer> duplicateCheck = new HashMap<String,Integer>();
	static private String tempName;
	static public String duplicateString = "";
	public String xml;
	private Stack<String> currentURI = new Stack<String>();
	
	
	/*
	 * endDocument()
	 * I am clearing the variables here, sure it technically isn't the best of ideas.
	 * But I don't like the values sitting around all day. Especially since
	 * the duplicateString HashMap can get quite large at the end of a file
	 */
	@Override
	public void endDocument(){
		duplicateCheck = null;
		tempName = null;
		duplicateString = null;
		currentURI = null;		
	}
	
	
	/*
	 * endElement(String uri,String localName, String qName)
	 * Who cares what all of those variables are, I am just clearing off the
	 * current element from the stack that tells the code which element it is 
	 * currently processing
	 */
	@Override
	public void endElement(String uri,String localName,String qName){
		this.currentURI.pop();
	}
		
	
	/*
	 * startElement(String namespaceURI, String localName, String qName, Attributes atts)
	 * And this one is the beast of a function that does all of the work
	 * It will take and XML file and convert it into a hash.
	 * 
	 * EXAMPLE: Using the XML file from the top
	 * data,phones,operatingSystem = "Android"
	 * data,phones,Motorola,name = "Sholes"
	 * data,phones,Motorola,releaseDate = "Sholes"
	 * data,phones,Motorola,carrier = "Verizon"
	 * data,phones,Motorola1,name = "Cliq"
	 * data,phones,Motorola1,releaseDate = "October '09"
	 * data,phones,Motorola1,carrier = "T-Mobile"
	 * data,carrier,name = "Sprint"
	 * data,carrier,whatAboutIt = "They have crappy phones and I am leaving for the Sholes!" />
	 * 
	 * Sure I WANTED to go with a hash of hashes and arrays like the ruby version that this is sort of modeled after
	 * but memory concerns made me decide not to do that.
	 * 
	 * (7 hashes and 2 arrays for the example above)
	 * 
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
		String qName, Attributes atts){
		
		this.currentURI.push(localName);
		tempName = this.currentURI.toString().replace("[","").replace("]","").replace(" ","");
		
		if(duplicateCheck.containsKey(tempName)){
			tempSize = duplicateCheck.get(tempName);
			duplicateCheck.put(tempName, tempSize+1);
			tempName = tempName + tempSize;	
		}else{
			duplicateCheck.put(tempName,1);
		}
		int totalAttributes = atts.getLength();
		for(int x=0;x<totalAttributes;x++){
		
			this.data.put(tempName + "," + atts.getLocalName(x),atts.getValue(x));
		}
	}
	
	
	/*
	 * characters(char ch[],int start, int length)
	 * 
	 * Pulls data from things that have non attributed values.
	 * 
	 */
	@Override
	public void characters(char ch[],int start, int length){
		this.data.put(tempName, new String(ch,start,length));
	}
	
	
	public String get(String key){
		return this.data.get(key);
	}
	
	public void put(String key, String value){
		this.data.put(key, value);
	}
	
	/*
	 * toString()
	 * You will never guess it, but this returns the contents of the hash as a string
	 * 
	 * EXAMPLE: (first line only from the XML at the top
	 * "data,phones,operatingSystem => Android\n"
	 * 
	 */
	@Override
	public String toString()
	{		
		int size = this.data.size();
		String result = new String("");
		Set<String> keys = this.data.keySet();
		Object[] keyArray = keys.toArray();
		for(int x=0;x<size;x++){
			result += "\n" + keyArray[x] + " => " + this.data.get(keyArray[x]);
		}
		return result;
	}

}
