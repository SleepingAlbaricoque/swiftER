package kr.co.swiftER.autocomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class Trie {
	private final TrieNode root;
	
	public Trie() {
		root = new TrieNode();
	}
	
	public void insert(String word) {
		TrieNode node = root;
		for(int i =0; i< word.length(); i++) {
			char ch = word.charAt(i);
			TrieNode child = node.getChildren().get(ch);
			
			if(child == null) {
				child = new TrieNode();
				node.getChildren().put(ch, child);
			}
			
			node = child;
		}
		
		node.setEndOfWord(true);
	}
	
	public List<String> autoComplete(String prefix){
		List<String> suggestions = new ArrayList<>();
		TrieNode node = root;
		for(int i=0; i < prefix.length(); i++) {
			char ch = prefix.charAt(i);
			TrieNode child = node.getChildren().get(ch);
			
			if(child == null) { // 일치하는 단어가 없으므로 빈 리스트 리턴
				return suggestions;
			}
			
			node = child;
		}
		
		autoCompleteHelper(prefix, node, suggestions);
		return suggestions;
	}
	
	private void autoCompleteHelper(String prefix, TrieNode node, List<String> suggestions) { // Trie 클래스 내부에서만 사용할 것이므로 private 지시자 사용
		if(node.isEndOfWord()) {
			suggestions.add(prefix);
		}
		
		for(Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
			char ch = entry.getKey();
			autoCompleteHelper(prefix + ch, entry.getValue(), suggestions);
		}
	}
}
