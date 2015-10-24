package cz.vutbr.wislab.symphony.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;

public class JsonParser {
	
	
	
		//---------------------------------------------------------------------//
		// Method for parsing JSON to HashMap strings						   //
		//---------------------------------------------------------------------//
		
	public HashMap<String, ArrayList<String>> outputData = new HashMap<>();
		
		public HashMap<String, ArrayList<String>> parse (JsonNode node){
			Iterator<JsonNode> it = node.iterator();
			Iterator<String> itFN = node.getFieldNames();
			while (it.hasNext()){
				JsonNode testOne = (JsonNode)it.next();
				String name = itFN.next().toString();
				//TEXT NODE
				if (testOne.getClass() == org.codehaus.jackson.node.TextNode.class) {
					String values = testOne.getTextValue();
					System.out.println(name.toString() + " = " + values);
					if (outputData.containsKey(name.toString())){
						ArrayList<String> list = new ArrayList<>();
						list.addAll(outputData.get(name.toString()));
						list.add(values);
						outputData.put(name.toString(), list);
					}else {
						ArrayList<String> list = new ArrayList<>();
						list.add(values);
						outputData.put(name.toString(), list);
					}
					
					
				//INT NODE	
				}else if (testOne.getClass() == org.codehaus.jackson.node.IntNode.class) {
					int values = testOne.getIntValue();
					System.out.println(name.toString() + " = " + values);
					
					if (outputData.containsKey(name.toString())){
						ArrayList<String> list = new ArrayList<>();
						list.addAll(outputData.get(name.toString()));
						list.add(Integer.toString(values));
						outputData.put(name.toString(), list);
					}else {
						ArrayList<String> list = new ArrayList<>();
						list.add(Integer.toString(values));
						outputData.put(name.toString(), list);
					}
				
				//DOUBLE NODE
				}else if(testOne.getClass() == org.codehaus.jackson.node.DoubleNode.class){
					double values = testOne.getDoubleValue();
					System.out.println(name.toString() + " = " + values);
					
					if (outputData.containsKey(name.toString())){
						ArrayList<String> list = new ArrayList<>();
						list.addAll(outputData.get(name.toString()));
						list.add(Double.toString(values));
						outputData.put(name.toString(), list);
					}else {
						ArrayList<String> list = new ArrayList<>();
						list.add(Double.toString(values));
						outputData.put(name.toString(), list);
					}
					
				// OBJECT NODE
				}else if(testOne.getClass() == org.codehaus.jackson.node.ObjectNode.class){
					parse(testOne);
					
					
				// ARRAY NODE
				}else if(testOne.getClass() == org.codehaus.jackson.node.ArrayNode.class){
					for (int i = 0; i < testOne.size(); i++) {
						parse(testOne.get(i));
					}
				}
				
			}
			return outputData;
		}
}
